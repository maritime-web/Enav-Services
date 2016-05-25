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

import ietf.lost1.ListServicesByLocationResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

/**
 * Created by Steen on 25-05-2016.
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class ListServicesByLocationResponseParserTest {
    @Mock
    private JaxbAdapter jaxbAdapter;

    @InjectMocks
    private ListServicesByLocationResponseParser cut;

    @Test
    public void shouldExtractServiceIdsFromResponse() throws Exception {
        String listServiceResponse = "<rawResponse></rawResponse>";
        String expectedServiceUrn = "urn:nice:service";
        ListServicesByLocationResponse marshalledResponse = new ListServicesByLocationResponse();
        marshalledResponse.getServiceList().add(expectedServiceUrn);
        when(jaxbAdapter.unmarshal(listServiceResponse, ListServicesByLocationResponse.class)).thenReturn(marshalledResponse);

        List<String> serviceIds = cut.getServiceIds(listServiceResponse);

        assertThat(serviceIds, contains(expectedServiceUrn));
    }
}
