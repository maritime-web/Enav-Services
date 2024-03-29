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

import dk.dma.embryo.common.configuration.Type;
import dk.dma.embryo.common.log.EmbryoLogService;
import dk.dma.embryo.tiles.image.GeoImage;
import dk.dma.embryo.tiles.image.ImageTypeFilter;
import dk.dma.embryo.tiles.model.TileSet;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;

import java.io.File;
import java.util.Arrays;
import java.util.Map;

/**
 * Created by Jesper Tejlgaard on 11/10/14.
 */
@Slf4j
public class DeleteGeoImageVisitor extends AbstractTileProviderVisitor {

    public DeleteGeoImageVisitor(DateTime limit, TileSetDao tileSetDao, EmbryoLogService embryoLogService) {
        super(limit, tileSetDao, embryoLogService);
    }

    @Override
    public void visit(Type type) {
        try {
            File directory = new File(type.getLocalDirectory());
            if (!directory.exists()) {
                log.info("Unexisting directory {} . Skipping ...", type.getLocalDirectory());
                return;
            }

            Map<String, TileSet> tileSets = getTileSets(type);

            File[] files = directory.listFiles(new ImageTypeFilter());
            if (files == null) {
                log.warn("Unable to delete images in folder '{}'", directory);
                return;
            }
            // TODO validate download of jpg, prj and jtw?
            log.debug("Files: " + Arrays.toString(files));
            for (File file : files) {
                TileSet tileSet = GeoImage.parse(file);
                if (tileSet.getTs().isBefore(limit)) {
                    if (!tileSets.containsKey(tileSet.getName()) || tileSets.get(tileSet.getName()).getStatus() != TileSet.Status.CONVERTING) {
                        try {
                            if (new GeoImage(file).delete()) {
                                result.deleted++;
                            } else {
                                String msg = "Failed deleting geo referenced image " + file.getAbsolutePath();
                                log.error(msg);
                                embryoLogService.error(msg);
                                result.errorCount++;
                            }
                        } catch (Exception e) {
                            String msg = "Fatal error deleting geo referenced image " + file.getAbsolutePath();
                            log.error(msg, e);
                            embryoLogService.error(msg, e);
                            result.errorCount++;
                        }
                    }
                }
            }
        } catch (Exception e) {
            String msg = "Fatal error deleting geo referenced image files for provider " + currentProvider.getShortName() + " and type " + type.getName();
            log.error(msg, e);
            embryoLogService.error(msg, e);
            result.errorCount++;
        }
    }
}
