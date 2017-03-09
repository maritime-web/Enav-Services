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

package dk.dma.embryo.tiles.service;


import dk.dma.embryo.common.configuration.Property;
import dk.dma.embryo.common.configuration.PropertyFileService;
import dk.dma.embryo.common.log.EmbryoLogService;
import dk.dma.embryo.common.mail.MailSender;
import dk.dma.embryo.tiles.image.Image2Tiles;
import dk.dma.embryo.tiles.image.Image2TilesUsingMaptiler;
import dk.dma.embryo.tiles.image.ImageSourceMeta;
import dk.dma.embryo.tiles.image.ImageSourceMetaReader;
import dk.dma.embryo.tiles.model.TileSet;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by Jesper Tejlgaard on 8/26/14.
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
@Slf4j
public class TilerServiceBean implements TilerService {

    @Inject
    @Property(value = "embryo.tmpDir", substituteSystemProperties = true)
    private String tmpDir;

    @Inject
    @Property(value = "embryo.tiles.directory", substituteSystemProperties = true)
    private String destinationDir;

    @Inject
    @Property(value = "embryo.tiles.mbtiles")
    private String mbtiles;

    @Inject
    private TileSetDao dao;

    @Inject
    private EmbryoLogService logService;

    @Inject
    private MailSender mailSender;

    @Inject
    private Image2Tiles transformer;

    @Inject
    private ImageSourceMetaReader sourceMetaReader;

    @Inject
    private PropertyFileService propertyFileService;

    public Result cleanup() {
        Result result = new Result();
        // todo we should probably assign the error count, the method only return the deleted
        Image2TilesUsingMaptiler.CleanResult cleanResult = transformer.cleanup();
        result.deleted = cleanResult.getDeleted();
        result.errorCount = cleanResult.getErrorCount();
        return result;
    }

    @Asynchronous
    public void transformImage2tiles(File geotifFile, String name, String provider) {
        try {
            long start = System.currentTimeMillis();

            log.info("{}", geotifFile.getAbsolutePath());

            TileSet tileSet = dao.getByNameAndProvider(name, provider);
            tileSet.setStatus(TileSet.Status.CONVERTING);
            tileSet = dao.saveEntity(tileSet);

            ImageSourceMeta meta = sourceMetaReader.read(geotifFile);
            tileSet.setCenter(meta.getCenter());
            tileSet.setExtend(meta.getBoundingBox());
            dao.saveEntity(tileSet);

            initDir(destinationDir);
            initDir(tmpDir);

            String destName = "true".equalsIgnoreCase(mbtiles) ? name + ".mbtiles" : name;

            File tmpDestination = new File(tmpDir, destName);
            if (tmpDestination.exists()) {
                log.debug("Deleting existing tmp destination {} ", tmpDestination.getAbsolutePath());

                FileUtils.deleteQuietly(tmpDestination);
                // wait for delete to happen
                Thread.sleep(10);
            }

            log.debug("Transforming {} to {}", geotifFile, tmpDestination);
            transformer.execute(geotifFile, tmpDestination);

            File destination = new File(destinationDir, tmpDestination.getName());
            log.debug("Moving {} to {}", tmpDestination, destination);

            Files.move(Paths.get(tmpDestination.getAbsolutePath()), Paths.get(destination.getAbsolutePath()));

            tileSet = dao.getByNameAndProvider(name, provider);
            tileSet.setStatus(TileSet.Status.SUCCESS);
            dao.saveEntity(tileSet);

            long diff = System.currentTimeMillis() - start;

            long hours = diff / (60 * 60 * 1000);
            long remainder = diff % (60 * 60 * 1000);
            long minutes = remainder / (60 * 1000);
            remainder = remainder % (60 * 1000);
            long seconds = remainder / 1000;

            String time = hours + ":" + minutes + ":" + seconds;
            String msg = "Transformed " + geotifFile.getAbsolutePath() + " to tiles " + destination.getAbsolutePath() + " in " + time;
            log.info(msg);
            logService.info(msg);
        } catch (Exception e) {
            try {
                TileSet tileSet = dao.getByNameAndProvider(name, provider);
                tileSet.setStatus(TileSet.Status.FAILED);
                dao.saveEntity(tileSet);
            } catch (Exception e2) {
                log.error("Failed updating status", e2);
            }

            try {
                sendEmail(name, provider, e);
            } catch (Exception e2) {
                log.error("Failed sending mail", e2);
            }

            try {
                String msg = "Failed transforming " + geotifFile + " to tiles";
                log.error(msg, e);
                logService.error(msg, e);
            } catch (Exception e2) {
                log.error("Failed logging", e2);
            }
        }

    }

    private void sendEmail(String name, String provider, Exception e) {
        new GeoRefImageImportErrorMail(name, provider, e, propertyFileService).send(mailSender);
    }

    private void initDir(String dir) {
        if (!new File(dir).exists()) {
            File directory = new File(dir);
            if (!directory.mkdirs()) {
                log.error("Failed creating directory {}", dir);
            }
        }
    }


}
