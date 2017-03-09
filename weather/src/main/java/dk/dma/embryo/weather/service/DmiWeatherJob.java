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
package dk.dma.embryo.weather.service;

import dk.dma.embryo.common.configuration.Property;
import dk.dma.embryo.common.log.EmbryoLogService;
import dk.dma.embryo.common.util.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.ScheduleExpression;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.inject.Inject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 
 * @author Jesper Tejlgaard
 */
@Singleton
@Startup
@Slf4j
public class DmiWeatherJob {

    @Resource
    private TimerService timerService;

    @Inject
    private EmbryoLogService embryoLogService;
    
    @Inject
    private WeatherServiceImpl weatherService;

    @Inject
    @Property("embryo.weather.dmi.ftp.cron")
    private ScheduleExpression cron;

    @Inject
    @Property("embryo.weather.dmi.ftp.serverName")
    private String dmiServer;

    @Inject
    @Property("embryo.weather.dmi.ftp.login")
    private String dmiLogin;

    @Inject
    @Property("embryo.weather.dmi.ftp.password")
    private String dmiPassword;

    @Inject
    @Property(value = "embryo.weather.dmi.localDirectory", substituteSystemProperties = true)
    private String localDmiDir;

    @Inject
    @Property(value = "embryo.tmpDir", substituteSystemProperties = true)
    private String tmpDir;

    public DmiWeatherJob() {
    }

    @PostConstruct
    public void init() {
        if (!dmiServer.trim().equals("") && (cron != null)) {
            log.info("Initializing {} with {}", this.getClass().getSimpleName(), cron.toString());
            timerService.createCalendarTimer(cron, new TimerConfig(null, false));
        } else {
            log.info("DMI FTP site is not configured - cron job not scheduled.");
        }
    }

    @Timeout
    public void timeout() {
        // notifications.clearOldThanMinutes(silencePeriod);

        try {
            log.info("Making directories if necessary ...");

            File localDmiDirFile = new File(localDmiDir);
            if (!localDmiDirFile.exists()) {
                log.info("Making local directory for DMI files: " + localDmiDir);
                FileUtils.createDirectories(localDmiDirFile);
            }

            File tempDirFile = new File(tmpDir);
            if (!tempDirFile.exists()) {
                log.info("Making local temporary directory: " + tmpDir);
                FileUtils.createDirectories(tempDirFile);
            }

            FTPClient ftp = connect();

            log.info("Transfer files ...");
            final List<String> transfered = new ArrayList<>();
            final List<String> error = new ArrayList<>();

            try {
                List<FTPFile> files = Arrays.asList(ftp.listFiles(null, DmiFTPFileFilters.FILES));
                for (FTPFile file : files) {
                    try {
                        if (transferFile(ftp, file, localDmiDir)) {
                            transfered.add(file.getName());
                        }
                    } catch (RuntimeException e) {
                        error.add(file.getName());
                    }
                }
            } finally {
                ftp.logout();
            }
            
            try {
                weatherService.refresh();
            }catch(Exception e){
                embryoLogService.error("Error reading transfered file", e);
                error.add(e.getMessage());
            }

            String msg = "Scanned DMI (" + dmiServer + ") for files. Transfered: " + toString(transfered)
                    + ", Errors: " + toString(error);
            if (error.size() == 0) {
                log.info(msg);
                embryoLogService.info(msg);
            } else {
                log.error(msg);
                embryoLogService.error(msg);
            }
        } catch (Throwable t) {
            log.error("Unhandled error scanning/transfering files from DMI (" + dmiServer + "): " + t, t);
            embryoLogService.error("Unhandled error scanning/transfering files from DMI (" + dmiServer + "): " + t, t);
        }
    }

    String toString(List<String> list) {
        StringBuilder builder = new StringBuilder();

        for (String str : list) {
            if (builder.length() > 0) {
                builder.append(", ");
            }
            builder.append(str);
        }

        return builder.toString();
    }

    FTPClient connect() throws IOException {
        FTPClient ftp = new FTPClient();
        log.info("Connecting to " + dmiServer + " using " + dmiLogin + " ...");

        ftp.setDefaultTimeout(30000);
        ftp.connect(dmiServer);
        ftp.login(dmiLogin, dmiPassword);
        ftp.enterLocalPassiveMode();
        ftp.setFileType(FTP.BINARY_FILE_TYPE);
        return ftp;
    }

    private boolean transferFile(FTPClient ftp, FTPFile file, String localDmiDir) throws IOException,
            InterruptedException {

        File tmpFile = new File(tmpDir, "dmiWeather" + Math.random());

        try (FileOutputStream fos = new FileOutputStream(tmpFile)) {
            log.info("Transfering " + file.getName() + " to " + tmpFile.getAbsolutePath());
            if (!ftp.retrieveFile(file.getName(), fos)) {
                Thread.sleep(10);
                if (tmpFile.exists()) {
                    log.info("Deleting temporary file " + tmpFile.getAbsolutePath());
                    org.apache.commons.io.FileUtils.deleteQuietly(tmpFile);
                }

                throw new RuntimeException("File transfer failed (" + file.getName() + ")");
            }
        }

        Thread.sleep(10);

        Path dest = Paths.get(localDmiDir).resolve(file.getName());
        log.info("Moving " + tmpFile + " to " + dest.getFileName());
        Files.move(Paths.get(tmpFile.getAbsolutePath()), dest, StandardCopyOption.REPLACE_EXISTING);

        return true;
    }

}
