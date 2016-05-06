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
package dk.dma.enav.services.registry;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;

import java.util.Locale;

import static dk.dma.enav.hamcrest.matchers.EnavMatchers.hasXPath;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by Steen on 02-05-2016.
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class ListServicesByLocationRequestFactoryTest {
    @Mock
    private Logger logger;
    @InjectMocks
    private ListServicesByLocationRequestFactory cut;

    @Test
    public void shouldContainListServicesByLocationAsRootElement() throws Exception {
        String request = cut.createRequest(1D, 2D);

        assertThat(request, hasXPath("/listServicesByLocation"));
    }

    @Test
    public void shouldHaveGeodetic2dAsProfileValue() throws Exception {
        String request = cut.createRequest(1D, 2D);

        assertThat(request, hasXPath("/listServicesByLocation/location/@profile", equalTo("geodetic-2d")));
    }

    @Test
    public void shouldHaveRecursiveSetToTrue() throws Exception {
        String request = cut.createRequest(1D, 2D);

        assertThat(request, hasXPath("/listServicesByLocation/@recursive", is("true")));
    }

    @Test
    public void shouldContainParametersAsPointCoordinates() throws Exception {
        String request = cut.createRequest(1.1, 2.2);

//        assertThat(request, hasXPath("/listServicesByLocation/location/Point", containsString("1.1 2.2")));
    }

    @Test
    public void rsAsPointCoordinates() throws Exception {
        String pointTemplate = "<p2:Point id=\"point1\" srsName=\"urn:ogc:def:crs:EPSG::4326\" xmlns:p2=\"http://www.opengis.net/gml\"><p2:pos>%1$f %2$f</p2:pos></p2:Point>";
        System.out.printf(String.format(Locale.ENGLISH, pointTemplate, 55.0, 11.0));

    }
}
