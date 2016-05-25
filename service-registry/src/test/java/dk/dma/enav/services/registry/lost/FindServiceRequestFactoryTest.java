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

import ietf.lost1.FindService;
import ietf.lost1.Location;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Steen on 24-05-2016.
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class FindServiceRequestFactoryTest {
    @Captor
    private ArgumentCaptor<FindService> request;

    @Mock
    private JaxbAdapter jaxbAdapter;
    @Mock
    private LocationFactory locationFactory;

    @InjectMocks
    private FindServiceRequestFactory cut;

    @Before
    public void setUp() throws Exception {
        when(jaxbAdapter.marshal(request.capture())).thenReturn("marshalled request");
    }

    @Test
    public void shouldSetServiceBoundaryAttributeToValueInRequest() throws Exception {
        cut.create(11D, 22D, "urn:tada:tada");

        assertThat(request.getValue().getServiceBoundary(), equalTo("value"));
    }

    @Test
    public void shouldUseServiceIdParameterAsService() throws Exception {
        String serviceId = "urn:tada:tada";
        cut.create(11.0001234, 22.0005678, serviceId);

        assertThat(request.getValue().getService(), equalTo(serviceId));
    }

    @Test
    public void shouldCallLocationFactoryWithParametersP1AndP2() throws Exception {
        double p1 = 11.0001234;
        double p2 = 22.0005678;
        cut.create(p1, p2, "urn:tada:tada");

        verify(locationFactory).createLocation(eq(p1), eq(p2));
    }

    @Test
    public void shouldAddCreatedLocationToRequest() throws Exception {
        Location createdLocation = new Location();
        when(locationFactory.createLocation(anyDouble(), anyDouble())).thenReturn(createdLocation);

        cut.create(11D, 22D, "urn:tada:tada");

        assertThat(request.getValue().getLocation(), contains(createdLocation));
    }
}
