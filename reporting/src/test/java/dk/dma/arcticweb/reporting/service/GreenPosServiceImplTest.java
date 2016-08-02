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
package dk.dma.arcticweb.reporting.service;

import dk.dma.arcticweb.reporting.model.GreenposMinimal;
import dk.dma.arcticweb.reporting.persistence.GreenPosDao;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jesper Tejlgaard
 */
public class GreenPosServiceImplTest {

    private GreenPosService greenPosService;
    private GreenPosDao greenPosDao;

    @Before
    public void setup() {
        greenPosDao = Mockito.mock(GreenPosDao.class);
        greenPosService = new GreenPosServiceImpl(greenPosDao, null, null, null, null, null);
    }

    @Test
    public void testGetLatest() {
        List<GreenposMinimal> fromDb = new ArrayList<>();
        fromDb.add(new GreenposMinimal("MyShip", 123456789L, "SP", DateTime.parse("2016-10-07T18:00").toDate()));
        fromDb.add(new GreenposMinimal("MyShip", 123456789L, "PR", DateTime.parse("2016-10-07T12:00").toDate()));
        fromDb.add(new GreenposMinimal("MyVessel", 999999999L, "SP", DateTime.parse("2016-10-07T12:00").toDate()));
        fromDb.add(new GreenposMinimal("MyVessel", 999999999L, "FR", DateTime.parse("2016-10-08T06:00").toDate()));
        fromDb.add(new GreenposMinimal("MyVessel", 999999999L, "PR", DateTime.parse("2016-10-07T18:00").toDate()));

        Mockito.when(greenPosDao.getFromLast7Days()).thenReturn(fromDb);

        // Execute //
        List<GreenposMinimal> result = greenPosService.getLatest();

        // Expectations
        Assert.assertEquals(2, result.size());

        GreenposMinimal report = result.get(0);
        Assert.assertEquals("MyVessel", report.getName());
        Assert.assertEquals(Long.valueOf(999999999L), report.getMmsi());
        Assert.assertEquals("FR", report.getType());
        Assert.assertEquals(DateTime.parse("2016-10-08T06:00").toDate(), report.getTs());

        report = result.get(1);
        Assert.assertEquals("MyShip", report.getName());
        Assert.assertEquals(Long.valueOf(123456789L), report.getMmsi());
        Assert.assertEquals("SP", report.getType());
        Assert.assertEquals(DateTime.parse("2016-10-07T18:00").toDate(), report.getTs());
    }

}
