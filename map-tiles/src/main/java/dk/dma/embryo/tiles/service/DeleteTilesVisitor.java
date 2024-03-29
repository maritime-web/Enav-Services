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
import dk.dma.embryo.tiles.model.TileSet;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.Arrays;
import java.util.Map;

/**
 * Created by Jesper Tejlgaard on 11/10/14.
 */
@Slf4j
public class DeleteTilesVisitor extends AbstractTileProviderVisitor {

    private final String tileDirectory;

    public DeleteTilesVisitor(TileSetDao tileSetDao, EmbryoLogService embryoLogService, String tileDirectory) {
        super(null, tileSetDao, embryoLogService);
        this.tileDirectory = tileDirectory;
    }

    @Override
    public void visit(Type type) {
        try {
            File directory = new File(tileDirectory);
            if (!directory.exists()) {
                return;
            }

            Map<String, TileSet> imageMap = getTileSets(type);

            File[] files = directory.listFiles();
            if (files == null)  {
                log.error("Unable to delete files in " + tileDirectory + " listFiles() returned null.");
                return;
            }
            log.debug("Files: " + Arrays.toString(files));

            for (File file : files) {
                String tileSetName = file.getName().replaceAll(".mbtiles", "");
                if (!imageMap.containsKey(tileSetName)) {
                    if (FileUtils.deleteQuietly(file)) {
                        result.deleted++;
                    } else {
                        String msg = "Failed deleting tiles " + file.getAbsolutePath();
                        log.error(msg);
                        embryoLogService.error(msg);
                        result.errorCount++;
                    }
                }
            }
        } catch (Exception e) {
            String msg = "Fatal error deleting tiles for provider " + currentProvider.getShortName() + " and type " + type.getName();
            log.error(msg, e);
            embryoLogService.error(msg, e);
            result.errorCount++;
        }
    }
}
