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
package dk.dma.embryo.dataformats.json;

import dk.dma.embryo.dataformats.model.ShapeFileMeasurement;
import dk.dma.embryo.dataformats.model.factory.ShapeFileNameParserFactory;
import dk.dma.embryo.dataformats.persistence.ShapeFileMeasurementDao;
import dk.dma.embryo.dataformats.service.ShapeFileService;
import dk.dma.embryo.dataformats.service.ShapeFileService.Shape;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Test;
import org.unitils.reflectionassert.ReflectionAssert;

import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Jesper Tejlgaard
 */
public class ShapeFileRestServiceTest {

    ShapeFileService service;
    ShapeFileMeasurementDao dao;
    ShapeFileNameParserFactory factory;
    ShapeFileRestService jsonService;

    Request request;
    ResponseBuilder responseBuilder;

    @Before
    public void setup() {
        service = mock(ShapeFileService.class);
        dao = mock(ShapeFileMeasurementDao.class);
        factory = mock(ShapeFileNameParserFactory.class);

        request = mock(Request.class);
        responseBuilder = mock(ResponseBuilder.class);

        jsonService = new ShapeFileRestService(service, dao, factory);
    }

    @Test
    public void testCachedRetrieval() throws IOException {
        DateTime now = DateTime.now(DateTimeZone.UTC);

        ShapeFileMeasurement measurement = new ShapeFileMeasurement("iceChart", "dmi", "MyRegion", 12, 0);
        measurement.setCreated(now);
        when(dao.lookup("MyRegion", "iceChart", "dmi")).thenReturn(measurement);
        when(request.evaluatePreconditions(now.toDate())).thenReturn(Response.notModified());

        Response response = jsonService.getSingleFile("iceChart-dmi.MyRegion", 0, "", true, 0, 0, request);

        assertEquals(304, response.getStatus());

        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(900);
        cacheControl.setNoTransform(false);

        assertEquals(1, response.getMetadata().get("Cache-Control").size());
        ReflectionAssert.assertLenientEquals(cacheControl, response.getMetadata().get("Cache-Control").get(0));
        assertNull(response.getEntity());
    }

    @Test
    public void testNonCachedRetrieval() throws IOException {
        DateTime now = DateTime.now(DateTimeZone.UTC);

        ShapeFileMeasurement measurement = new ShapeFileMeasurement("iceChart", "dmi", "MyRegion", 12, 0);
        measurement.setCreated(now);
        when(dao.lookup("MyRegion", "iceChart", "dmi")).thenReturn(measurement);
        when(request.evaluatePreconditions(now.toDate())).thenReturn(null);

        Map<String, Object> map = new HashMap<>();
        map.put("test", "test");
        Shape shape = new Shape(map, new ArrayList<>(), 2);

        when(service.readSingleFile("iceChart-dmi.MyRegion", 0, "", true, 0, 0)).thenReturn(shape);

        Response response = jsonService.getSingleFile("iceChart-dmi.MyRegion", 0, "", true, 0, 0, request);

        assertEquals(200, response.getStatus());

        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(900);
        cacheControl.setNoTransform(false);

        assertEquals(1, response.getMetadata().get("Cache-Control").size());
        ReflectionAssert.assertLenientEquals(cacheControl, response.getMetadata().get("Cache-Control").get(0));
        assertNotNull(response.getEntity());
        assertEquals(shape, response.getEntity());
    }

    @Test
    public void testUpdated() throws IOException {
        DateTime now = DateTime.now(DateTimeZone.UTC);

        ShapeFileMeasurement measurement = new ShapeFileMeasurement("iceChart", "dmi", "MyRegion", 12, 2);
        measurement.setCreated(now);
        when(dao.lookup("MyRegion", "iceChart", "dmi")).thenReturn(measurement);
        when(request.evaluatePreconditions(now.toDate())).thenReturn(null);

        Map<String, Object> map = new HashMap<>();
        map.put("test", "test");
        Shape shape = new Shape(map, new ArrayList<>(), 2);

        when(service.readSingleFile("iceChart-dmi.MyRegion_v2", 0, "", true, 0, 0)).thenReturn(shape);

        Response response = jsonService.getSingleFile("iceChart-dmi.MyRegion", 0, "", true, 0, 0, request);

        assertEquals(200, response.getStatus());

        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(900);
        cacheControl.setNoTransform(false);

        assertEquals(1, response.getMetadata().get("Cache-Control").size());
        ReflectionAssert.assertLenientEquals(cacheControl, response.getMetadata().get("Cache-Control").get(0));
        assertNotNull(response.getEntity());
        assertEquals(shape, response.getEntity());
    }

}
