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

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimaps;
import dk.dma.enav.services.registry.api.InstanceMetadata;
import dk.dma.enav.services.registry.api.NoServicesFoundException;
import dk.dma.enav.services.registry.api.TechnicalDesignId;
import dk.dma.enav.services.registry.mc.api.ServiceinstanceresourceApi;
import dk.dma.enav.services.registry.mc.model.Instance;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;

/**
 * Created by Steen on 13-10-2016.
 *
 */
public class MaritimeCloudServiceRegistryIT {
    private MaritimeCloudServiceRegistry cut;
    private ApiFactory apiFactory;
    private InstanceRepository repository;

    @Before
    public void setUp() throws Exception {
        apiFactory = new ApiFactory("http://sr-test.maritimecloud.net:8080", 2000);
        InstanceMapper mapper = new InstanceMapper(new InstanceXmlParser());
        repository = new InstanceRepository(apiFactory, mapper, 5);
        cut = new MaritimeCloudServiceRegistry(repository);
    }

    @Test
    public void shouldBeAbleToMapAllInstancesToInstanceMetaData() throws Exception {
        ServiceinstanceresourceApi api = apiFactory.createServiceinstanceresourceApi();
        List<String> ids = api.getAllInstancesUsingGET(null, null, null, null, Lists.newArrayList()).stream().map(Instance::getInstanceId).collect(Collectors.toList());

        List<InstanceMetadata> allInstances = repository.getAllInstances();

        // use a multimap to group by instance Id, since you can have multiple services with the same instanceId
        ImmutableListMultimap<String, InstanceMetadata> multimap = Multimaps.index(allInstances, i -> i.getInstanceId());
        int versionWithSameInstanceId = 0;
        for (Collection<InstanceMetadata> collection : multimap.asMap().values()) {
            versionWithSameInstanceId += collection.size()-1;
        }

        List<InstanceMetadata> byInstanceId = cut.getServiceInstances(ids);
        assertEquals(byInstanceId.size() , ids.size() + versionWithSameInstanceId);
    }

    @Test
    public void shouldGetTheNwNmService() throws Exception {
        List<InstanceMetadata> res = cut.getServiceInstances(new TechnicalDesignId("urn:mrn:mcl:service:design:dma:nw-nm-rest", "0.3"), "POLYGON((-180 -90, 180 -90, 180 90, -180 90, -180 -90))");
        assertThat(res.size(), is(greaterThan(0)));
    }
    @Test
    @Ignore("Service does not exist on sr-test")
    public void shouldGetSatelitteService() throws Exception {
        List<InstanceMetadata> res = cut.getServiceInstances(new TechnicalDesignId("urn:mrn:mcl:service:technical:dma:tiles-service", "0.2"), "POLYGON((-180 -90, 180 -90, 180 90, -180 90, -180 -90))");
        assertThat(res.size(), is(greaterThan(0)));
    }

    @Test
    public void shouldGetTheNwNmServiceWhenWKTLocationFilterIsNull() throws Exception {
        List<InstanceMetadata> res = cut.getServiceInstances(new TechnicalDesignId("urn:mrn:mcl:service:design:dma:nw-nm-rest", "0.3"), null);

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

    @Test(expected = NoServicesFoundException.class)
    public void emptyResultforWKTLocationSearch() throws Exception {
        cut.findServiceByWKTLocation("serviceType:VTS", "POLYGON((65 81,-118 81,-108 -66,81 -61,65 81))");
    }

}
