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
import org.joda.time.DateTime;

import java.util.List;

/**
 * Created by Jesper Tejlgaard on 11/10/14.
 */
@Slf4j
public class DeleteTileSetVisitor extends AbstractTileProviderVisitor {

    public DeleteTileSetVisitor(DateTime limit, TileSetDao tileSetDao, EmbryoLogService embryoLogService) {
        super(limit, tileSetDao, embryoLogService);
    }

    @Override
    public void visit(Type type) {
        try {
            List<TileSet> images = tileSetDao.listByProviderAndTypeAndStatus(currentProvider.getShortName(), type.getName(), TileSet.Status.DELETING);
            for (TileSet tileSet : images) {
                if (tileSet.getTs().isBefore(limit)) {
                    try {
                        tileSetDao.remove(tileSet);
                        result.deleted++;
                    } catch (Exception e) {
                        String msg = "Fatal error deleting tile set entry " + tileSet.getName() + " from the database";
                        log.error(msg, e);
                        embryoLogService.error(msg, e);
                        result.errorCount++;
                    }
                }
            }
        } catch (Exception e) {
            String msg = "Fatal error deleting tile sets from database for provider " + currentProvider.getShortName() + " and type " + type.getName();
            log.error(msg, e);
            embryoLogService.error(msg, e);
            result.errorCount++;
        }
    }
}
