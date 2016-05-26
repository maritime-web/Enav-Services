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

import dk.dma.enav.services.registry.ServiceInstanceMetadata;
import dk.dma.enav.services.registry.ServiceLookupService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;

/**
 * Created by Steen on 28-04-2016.
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class LostServiceTest {
    @Mock
    private LostHttpClient client;
    @Mock
    private ListServicesByLocationRequestFactory listServicesByLocationRequestFactory;
    @Mock
    private FindServiceRequestFactory findServiceRequestFactory;
    @Mock
    private FindServiceResponseParser responseParser;
    @InjectMocks
    private LostService cut;

    @Test
    public void shouldUseRequestCoordinatesToObtainResponse() throws Exception {
        double p1 = 55D;
        double p2 = 11D;
/*
        List<String> serviceIds = Collections.singletonList("urn:a:service");
        ServiceInstanceMetadata serviceDescription = new ServiceInstanceMetadata("", "", "", "", "");
        when(listServicesByLocationRequestFactory.createt(p1, p2)).thenReturn("a service request");
        when(client.post("a service request")).thenReturn("service list response");
        when(responseParser.getServiceIds("service list response")).thenReturn(serviceIds);
        when(findServiceRequestFactory.create(p1, p2, serviceIds.get(0))).thenReturn("a find service request");
        when(client.post("a find service request")).thenReturn("a find service response");
        when(responseParser.parseFindServiceResponse("a find service response")).thenReturn(serviceDescription);

        List<ServiceInstanceMetadata> serviceData = cut.getServiceInstancesForService(55D, 11D);

        assertThat(serviceData, hasSize(1));
        assertThat(serviceData, hasItem(serviceDescription));
*/
    }
}
