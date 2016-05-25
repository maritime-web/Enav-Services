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
package dk.dma.enav.services.registry.lost;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.w3c.dom.Element;

import static dk.dma.enav.hamcrest.matchers.EnavMatchers.hasXPath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Steen on 25-05-2016.
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class Geodetic2DFactoryTest {
    @Captor
    private ArgumentCaptor<String> pointXml;

    @Mock
    private DomUtil domUtil;
    @InjectMocks
    private Geodetic2DFactory cut;

    @Before
    public void setUp() throws Exception {
        when(domUtil.createElement(pointXml.capture())).thenReturn(mock(Element.class));
    }

    @Test
    public void shouldRepresentPointCoordsWithAtLeast16Digits() throws Exception {
        cut.createPoint(11.0001234, 22.0005678);

        assertThat(pointXml.getValue(), hasXPath("//pos/text()", equalTo("11.0001234000000000 22.0005678000000000")));
    }

    @Test
    public void shouldBeAbleToRepresentNegativePointCoords() throws Exception {
        cut.createPoint(11.0001234, -22.0005678);

        assertThat(pointXml.getValue(), hasXPath("//pos/text()", equalTo("11.0001234000000000 -22.0005678000000000")));
    }
}
