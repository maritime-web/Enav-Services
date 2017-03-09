/* Copyright (c) 2011 Danish Maritime Authority.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dk.dma.embryo.dataformats.job;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import dk.dma.embryo.common.configuration.PropertyFileService;
import dk.dma.embryo.common.mail.MailSender;
import dk.dma.embryo.common.util.NamedtimeStamps;
import dk.dma.embryo.dataformats.job.AbstractJob.Dirtype;
import dk.dma.embryo.dataformats.model.ShapeFileMeasurement;
import dk.dma.embryo.dataformats.persistence.ShapeFileMeasurementDao;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilters;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Future;

import static com.google.common.base.Predicates.not;
import static dk.dma.embryo.dataformats.job.DmiIceChartPredicates.acceptedIceCharts;
import static dk.dma.embryo.dataformats.job.DmiIceChartPredicates.validFormat;

@Stateless
@Slf4j
public class FtpAsyncProxy {

    @Inject
    protected ShapeFileMeasurementDao shapeFileMeasurementDao;
    
    @Inject
    protected PropertyFileService propertyFileService;
    
    @Inject
    private MailSender mailSender;

    private String[] iceChartExts = new String[] { ".prj", ".dbf", ".shp", ".shx" };
    private NamedtimeStamps notifications = new NamedtimeStamps();
    
    @Asynchronous
    public Future<Counts> transferFiles(JobContext jobContext) throws IOException, InterruptedException {
        
        Counts counts = new Counts();
        final List<String> subdirectoriesAtServer = new ArrayList<>();
        
        FTPClient ftpClient = jobContext.getFtpClient();
        String ftpBaseDirectory = jobContext.getFtpBaseDirectory();
        
        try {
            if (!ftpClient.changeWorkingDirectory(ftpBaseDirectory)) {
                throw new IOException("Could not change to base directory:" + ftpBaseDirectory);
            }

            List<FTPFile> typeDirs = Arrays.asList(ftpClient.listFiles(null, FTPFileFilters.DIRECTORIES));
            for (FTPFile typedir : typeDirs) {
                log.info("Reading from dir " + typedir.getName());
                Counts c = readFromTypedir(jobContext, typedir, subdirectoriesAtServer);
                counts.addCounts(c);
            }
        } finally {
            ftpClient.logout();
        }

        String msg = "Scanned (" + ftpClient.getRemoteAddress() + ") for files. Transfered: " + counts.transferCount + ", Shapes deleted: "
                + counts.shapeDeleteCount + ". Files deleted: " + counts.fileDeleteCount + ", Errors: " + counts.errorCount;
        if (counts.errorCount == 0) {
            log.info(msg);
            jobContext.getEmbryoLogService().info(msg);
        } else {
            log.error(msg);
            jobContext.getEmbryoLogService().error(msg);
        }

        log.info("Deleting Shape entries no longer existing on FTP.");
        deleteShapeEntriesNoLongerOnFTP(jobContext, counts, subdirectoriesAtServer);

        log.info("Deleting local temp files. ");
        deleteLocalTempFiles(jobContext, counts, subdirectoriesAtServer);

        return new AsyncResult<>(counts);
    }

    private Counts readFromTypedir(JobContext jobContext, FTPFile typedir, List<String> subdirectoriesAtServer) throws IOException, InterruptedException {
        
        Counts counts = new Counts();
        
        String chartType = jobContext.getCharttypes().get(typedir.getName());
        String dirType = jobContext.getDirtypes().get(typedir.getName());
       
        String localDir = jobContext.getLocalDir(chartType, jobContext.getContext().getName());
        Map<String, String> regions = jobContext.getRegions(chartType, jobContext.getContext().getName());
        
        FTPClient ftpClient = jobContext.getFtpClient();

        LocalDate mapsYoungerThan = LocalDate.now().minusDays(jobContext.getAgeInDays()).minusDays(15);

        // Important only to transfer 1 file only when the files are large as is the case for 'prognoses'.
        final boolean TRANSFER_ONE_FILE_ONLY = localDir != null && localDir.contains("prognoses");
        
        
        Thread.sleep(10);

        log.debug("localDir: {} - TRANSFER_ONE_FILE_ONLY = {}", localDir, TRANSFER_ONE_FILE_ONLY);
        log.info("Reading files in: {}/{}", ftpClient.printWorkingDirectory(), typedir.getName());
        
        // Directories and single files should be handled differently.
        if (dirType != null) {
            if (dirType.equals(Dirtype.DIR.type)) {
                if (chartType != null && !chartType.isEmpty()) {
                    log.info("Working with chart type: " + chartType);
                    List<FTPFile> allDirs = Arrays.asList(ftpClient.listFiles(typedir.getName(), FTPFileFilters.DIRECTORIES));
                    log.debug("{}/{} contains files: {}", ftpClient.printWorkingDirectory(), typedir.getName(), allDirs);

                    Collection<FTPFile> rejected = Collections2.filter(allDirs, not(validFormat(regions.keySet())));
                    Collection<FTPFile> accepted = Collections2.filter(allDirs, acceptedIceCharts(regions.keySet(), mapsYoungerThan, localDir, iceChartExts));

                    log.debug("rejected: {}", allDirs);
                    log.debug("accepted: {}", allDirs);

                    subdirectoriesAtServer.addAll(Collections2.transform(allDirs, new NameFunction()));

                    for (FTPFile file : rejected) {
                        sendEmail(jobContext, file.getName(), chartType);
                    }

                    for (FTPFile subdirectory : accepted) {
                        Thread.sleep(10);

                        log.info("Reading files from subdirectories: " + subdirectory.getName());

                        ftpClient.changeWorkingDirectory(typedir.getName() + "/" + subdirectory.getName());

                        List<String> filesInSubdirectory = new ArrayList<>();

                        for (FTPFile ftpFile : ftpClient.listFiles()) {
                            filesInSubdirectory.add(ftpFile.getName());
                        }

                        int numberOfFiles = 0;
                        outerloop: 
                        for (String fileName : filesInSubdirectory) {
                            
                            for (String prefix : iceChartExts) {
                                if (fileName.endsWith(prefix)) {
                             
                                    if(TRANSFER_ONE_FILE_ONLY && numberOfFiles >= 1) {
                                        break outerloop;
                                    } else {
                                        
                                        if (transferFile(jobContext, ftpClient, fileName, localDir, jobContext.getFtpBaseFileName())) {
                                            counts.transferCount++;
                                            numberOfFiles++;
                                        }
                                    }
                                    
                                }
                            }
                        }
                        
                        ftpClient.changeToParentDirectory();
                        ftpClient.changeToParentDirectory();
                    }
                } else {
                    log.debug("No chart type for dir " + typedir.getName() + ", ignoring.");
                }
            } else {
                
                List<FTPFile> allFiles = Arrays.asList(ftpClient.listFiles(typedir.getName(), FTPFileFilters.NON_NULL));
                ftpClient.changeWorkingDirectory(typedir.getName());
                
                int numberOfFiles = 0;
                for (FTPFile ftpFile : allFiles) {
                    
                    // TODO: At this point, everything gets accepted. We might
                    // want to do some file name validation.
                    if(TRANSFER_ONE_FILE_ONLY && numberOfFiles >= 1) {
                        break;
                    } else {
                    
                        if (transferFile(jobContext, ftpClient, ftpFile.getName(), localDir, jobContext.getFtpBaseFileName())) {
                            counts.transferCount++;
                            numberOfFiles++;
                        }
                    }    
                        
                        
                }
                ftpClient.changeToParentDirectory();
            }
        } else {
            log.debug(typedir.getName() + " not found in config file, ignoring directory.");
        }

        return counts;
    }

    private boolean transferFile(JobContext jobContext, FTPClient ftp, String name, String localDir, String ftpBaseFileName) throws IOException, InterruptedException {
        String localName = localDir + "/" + name;

        if (new File(localName).exists()) {
            log.debug("Not transfering " + name + " since the file already exists in " + localName);
            return false;
        }

        File tmpFile = new File(jobContext.getTmpDir(), ftpBaseFileName + Math.random());

        try (FileOutputStream fos = new FileOutputStream(tmpFile)) {
            log.info("Transfering " + name + " to " + tmpFile.getAbsolutePath());
            if (!ftp.retrieveFile(name, fos)) {
                Thread.sleep(10);
                if (tmpFile.exists()) {
                    log.info("Deleting temporary file " + tmpFile.getAbsolutePath());
                    org.apache.commons.io.FileUtils.deleteQuietly(tmpFile);
                }
                throw new RuntimeException("File transfer failed (" + name + ")");
            }
        } catch (Exception e) {
            log.error("Exception occurred, deleting temporary file.");
            org.apache.commons.io.FileUtils.deleteQuietly(tmpFile);
            throw e;
        }

        Thread.sleep(10);

        log.info("Moving " + tmpFile.getAbsolutePath() + " to " + localName);
        if (tmpFile.getUsableSpace() < tmpFile.length()) {
            log.error("Not enough space on disk. Deleting temporary file.");
            org.apache.commons.io.FileUtils.deleteQuietly(tmpFile);
        } else if (!tmpFile.renameTo(new File(localName))) {
            log.error("Could not move file. Deleting temporary file.");
            org.apache.commons.io.FileUtils.deleteQuietly(tmpFile);
        }

        return true;
    }

    protected void deleteShapeEntriesNoLongerOnFTP(JobContext jobContext, Counts counts, final List<String> subdirectoriesAtServer) {
        
        List<ShapeFileMeasurement> measurements = jobContext.getMeasurementsFromDatabase();
        for (ShapeFileMeasurement measurement : measurements) {
            if (!subdirectoriesAtServer.contains(measurement.getFileName())) {
                try {
                    log.info("Deleting shape entry {}", measurement.getFileName());
                    shapeFileMeasurementDao.remove(measurement);
                    counts.shapeDeleteCount++;
                } catch (Exception e) {
                    String msg = "Error deleting shape entry " + measurement.getFileName() + " from PolarWeb server";
                    log.error(msg, e);
                    jobContext.getEmbryoLogService().error(msg, e);
                    counts.errorCount++;
                }
            }
        }
    }
    
    protected void deleteLocalTempFiles(JobContext jobContext, Counts counts, final List<String> subdirectoriesAtServer) {
        
        for (Entry<String, String> entry : jobContext.getCharttypes().entrySet()) {
            String chartType = entry.getValue();
            final String dirtype = jobContext.getDirtypes().get(entry.getKey());
            FileUtility fileService = new FileUtility(jobContext.getLocalDir(chartType, jobContext.getContext().getName()));
            String[] filesToDelete = fileService.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    String[] parts = name.split("\\.");
                    // Note that only files in subdirectories (like ice charts
                    // and icebergs) are removed.
                    return dirtype.equals(Dirtype.DIR.type) && !subdirectoriesAtServer.contains(parts[0]);
                }
            });

            for (String file : filesToDelete) {
                try {
                    log.info("Deleting chart files {}", file);
                    fileService.deleteFile(file);
                    counts.fileDeleteCount++;
                } catch (Exception e) {
                    String msg = "Error deleting chart file " + file + " from PolarWeb server";
                    log.error(msg, e);
                    jobContext.getEmbryoLogService().error(msg, e);
                    counts.errorCount++;
                }
            }
        }
    }
    
    private void sendEmail(JobContext jobContext, String chartName, String chartType) {
        
        if (jobContext.getMailTo() != null && jobContext.getMailTo().trim().length() > 0 && !notifications.contains(chartName)) {
            new IceChartNameNotAcceptedMail(
                jobContext.getContext().getName(), 
                chartName, 
                jobContext.getRegions(chartType, jobContext.getContext().getName()).keySet(), 
                propertyFileService
            ).send(mailSender);
            
            notifications.add(chartName, DateTime.now(DateTimeZone.UTC));
        }
    }

    public class NameFunction implements Function<FTPFile, String> {
        @Override
        public String apply(FTPFile input) {
            return input.getName();
        }
    }
}
