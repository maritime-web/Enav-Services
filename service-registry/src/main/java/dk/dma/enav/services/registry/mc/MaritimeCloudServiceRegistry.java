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

import dk.dma.enav.services.registry.api.EnavServiceRegister;
import dk.dma.enav.services.registry.api.InstanceMetadata;
import dk.dma.enav.services.registry.api.TechnicalDesignId;
import dk.dma.enav.services.registry.mc.model.Instance;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Steen on 12-10-2016.
 *
 */
public class MaritimeCloudServiceRegistry implements EnavServiceRegister {
    private final ApiFactory apiFactory;
    private final InstanceMapper mapper;

    @Inject
    public MaritimeCloudServiceRegistry(ApiFactory apiFactory, InstanceMapper mapper) {
        this.apiFactory = apiFactory;
        this.mapper = mapper;
    }

    @Override
    public List<InstanceMetadata> getServiceInstances(TechnicalDesignId id, String wktLocationFilter) {
        try {
            List<Instance> instances = apiFactory.createServiceinstanceresourceApi().getAllInstancesUsingGET(null, null, null);
            return instances.stream()
                    .map(mapper::toMetaData)
                    .filter(instance -> instance.getTechnicalDesignId() != null)
                    .filter(instance -> instance.getTechnicalDesignId().equals(id))
                    .filter(instance -> instance.intersects(wktLocationFilter))
                    .collect(Collectors.toList());
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<InstanceMetadata> getServiceInstances(List<String> instanceIds) {
        try {
            List<Instance> instances = apiFactory.createServiceinstanceresourceApi().getAllInstancesUsingGET(null, null, null);
            return instances.stream()
                    .filter(instance -> instanceIds.contains(instance.getInstanceId()))
                    .map(mapper::toMetaData)
                    .collect(Collectors.toList());
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
    }

}
