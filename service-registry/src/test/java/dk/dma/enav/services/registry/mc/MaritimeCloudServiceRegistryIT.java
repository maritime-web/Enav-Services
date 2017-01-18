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
import dk.dma.enav.services.registry.api.NoServicesFoundException;
import dk.dma.enav.services.registry.api.TechnicalDesignId;
import dk.dma.enav.services.registry.mc.api.ServiceinstanceresourceApi;
import dk.dma.enav.services.registry.mc.model.Instance;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

/**
 * Created by Steen on 13-10-2016.
 *
 */
public class MaritimeCloudServiceRegistryIT {
    private static InstanceRepository repository;
    private MaritimeCloudServiceRegistry cut;

    @BeforeClass
    public static void setUpClass() {
        ApiFactory apiFactory = new ApiFactory("http://195.34.146.186:8080/", 2000);
        InstanceMapper mapper = new InstanceMapper(new InstanceXmlParser(new Base64Decoder()));
        repository = new InstanceRepository(apiFactory, mapper, 5);
    }

    @Before
    public void setUp() throws Exception {
        cut = new MaritimeCloudServiceRegistry(repository);
    }

    @Test
    public void shouldBeAbleToMapAllInstancesToInstanceMetaData() throws Exception {
        ServiceinstanceresourceApi api = new ServiceinstanceresourceApi();
        List<String> ids = api.getAllInstancesUsingGET(null, null, null).stream().map(Instance::getInstanceId).collect(Collectors.toList());

        List<InstanceMetadata> serviceInstances = cut.getServiceInstances(ids);

        assertThat(serviceInstances.size(), is(ids.size()));
    }

    @Test
    public void shouldGetTheNwNmService() throws Exception {
        List<InstanceMetadata> res = cut.getServiceInstances(new TechnicalDesignId("urn:mrn:mcl:service:design:dma:nw-nm:rest", "0.3"), "POLYGON((-180 -90, 180 -90, 180 90, -180 90, -180 -90))");

        assertThat(res.size(), is(greaterThan(0)));
    }

    @Test
    public void shouldGetTheNwNmServiceWhenWKTLocationFilterIsNull() throws Exception {
        List<InstanceMetadata> res = cut.getServiceInstances(new TechnicalDesignId("urn:mrn:mcl:service:design:dma:nw-nm:rest", "0.3"), null);

        assertThat(res.size(), is(greaterThan(0)));
    }

    @Test(expected = NoServicesFoundException.class)
    public void shouldNotGetTheNwNmServiceWhenTechnicalDesignIdIsNull() throws Exception {
        cut.getServiceInstances(null, null);
    }

    @Test(expected = NoServicesFoundException.class)
    public void shouldNotGetTheNwNmServiceWhenTechnicalDesignIdVersionIsNull() throws Exception {
        cut.getServiceInstances(new TechnicalDesignId("urn:mrnx:mcl:service:dma:nw-nm:rest", null), null);
    }

    @Test(expected = NoServicesFoundException.class)
    public void shouldThrowNoServicesFoundExceptionWhenSearchingForUnkownId() throws Exception {
        cut.getServiceInstances(new TechnicalDesignId("urn:mrnx:mcl:service:dma:non:existing:service", "1.0"), null);
    }
}
