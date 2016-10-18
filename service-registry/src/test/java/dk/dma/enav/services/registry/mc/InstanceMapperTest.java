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
package dk.dma.enav.services.registry.mc;

import dk.dma.enav.services.registry.api.InstanceMetadata;
import dk.dma.enav.services.registry.mc.model.Instance;
import dk.dma.enav.services.registry.mc.model.Xml;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class InstanceMapperTest {
    @Mock
    private InstanceXmlParser instanceXmlParser;
    @InjectMocks
    private InstanceMapper cut;

    @Test
    public void shouldReturnMetaDataWithErrorInstanceDetailsCantBeObtained() throws Exception {
        when(instanceXmlParser.parseInstanceXml(any(Xml.class))).thenThrow(new IllegalArgumentException("Nothing works"));

        InstanceMetadata instanceMetadata = cut.toMetaData(new Instance());

        assertThat(instanceMetadata.getErrors(), is(not(nullValue())));
    }
}
