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

import dk.dma.embryo.common.configuration.Provider;
import dk.dma.embryo.common.configuration.Type;
import dk.dma.embryo.common.log.EmbryoLogService;
import dk.dma.embryo.tiles.model.TileSet;
import org.joda.time.DateTime;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.unitils.reflectionassert.ReflectionAssert;

import java.util.ArrayList;
import java.util.List;

import static dk.dma.embryo.tiles.model.TileSet.Status.CONVERTING;
import static dk.dma.embryo.tiles.model.TileSet.Status.DELETING;
import static dk.dma.embryo.tiles.model.TileSet.Status.FAILED;
import static dk.dma.embryo.tiles.model.TileSet.Status.SUCCESS;
import static dk.dma.embryo.tiles.model.TileSet.Status.UNCONVERTED;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Jesper Tejlgaard on 10/02/14
 */

public class MarkUnfinishedTiling4DeletionVisitorTest {

    private TileSet tileSet(String name, TileSet.Status status, DateTime ts) {
        return new TileSet(name, "Provider", "Source", "Type", status, ts, "satellite-ice");
    }

    private List<TileSet> expectedModified(TileSet.Status status, List<TileSet> tileSets, int... indexes) {
        List<TileSet> result = new ArrayList<>();
        for (int index : indexes) {
            TileSet tileSet = tileSets.get(index);
            tileSet.setStatus(status);
            result.add(tileSet);
        }
        return result;
    }

    @Test
    public void visit_nonExistingTileSet() throws Exception {
        EmbryoLogService logService = mock(EmbryoLogService.class);
        TileSetDao tileSetDao = mock(TileSetDao.class);
        DateTime limit = DateTime.parse("2014-11-08T00:00:00-00:00");

        List<TileSet> inDatabase = new ArrayList<>();
        inDatabase.add(tileSet("Source_Type_20141106-aqua-r01c02-250m", CONVERTING, DateTime.parse("2014-11-08T12:00:00-00:00")));
        inDatabase.add(tileSet("Source_Type_20141107-aqua-r01c02-250m", CONVERTING, DateTime.parse("2014-11-07T23:59:00-00:00")));

        when(tileSetDao.listByProviderAndTypeAndStatus("TestProvider", "satellite-ice", TileSet.Status.CONVERTING)).thenReturn(inDatabase);

        MarkUnfinishedTiling4DeletionVisitor visitor = new MarkUnfinishedTiling4DeletionVisitor(limit, tileSetDao, logService);
        // Execute
        visitor.visit(new Provider("TestProvider", "test@test.dk"));
        visitor.visit(new Type("satellite-ice", "path"));

        assertEquals(1, visitor.getResult().deleted);
        assertEquals(0, visitor.getResult().errorCount);
        assertEquals(0, visitor.getResult().jobsStarted);

        ArgumentCaptor<TileSet> captor = ArgumentCaptor.forClass(TileSet.class);
        verify(tileSetDao, times(1)).saveEntity(captor.capture());
        List<TileSet> expected = expectedModified(DELETING, inDatabase, 1);

        ReflectionAssert.assertLenientEquals(expected, captor.getAllValues());
    }

}
