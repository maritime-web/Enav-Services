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

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dk.dma.enav.services.registry.api.EnavServiceRegister;
import dk.dma.enav.services.registry.api.InstanceMetadata;
import dk.dma.enav.services.registry.api.NoServicesFoundException;
import dk.dma.enav.services.registry.api.TechnicalDesignId;

/**
 *
 */
public class MaritimeCloudServiceRegistry implements EnavServiceRegister {

    private final InstanceRepository instanceRepository;

    @Inject
    public MaritimeCloudServiceRegistry(InstanceRepository instanceRepository) {
        this.instanceRepository = instanceRepository;
    }

    @Override
    public List<InstanceMetadata> getServiceInstances(TechnicalDesignId id, String wktLocationFilter) {
        List<InstanceMetadata> result = instanceRepository.getAllInstances().stream()
                .filter(instance -> instance.getTechnicalDesignId() != null)
                .filter(instance -> instance.getTechnicalDesignId().equals(id))
                .filter(instance -> wktLocationFilter == null || instance.intersects(wktLocationFilter))
                .collect(Collectors.toList());

        if (result.isEmpty()) {
            throw new NoServicesFoundException();
        }

        return result;
    }

    @Override
    public List<InstanceMetadata> getServiceInstances(String wktLocationFilter) {
        List<InstanceMetadata> result = instanceRepository.getAllInstances().stream()
                .filter(instance -> wktLocationFilter == null || instance.intersects(wktLocationFilter))
                .collect(Collectors.toList());

        if (result.isEmpty()) {
            throw new NoServicesFoundException();
        }

        return result;
    }


    @Override
    public List<InstanceMetadata> getServiceInstances(List<String> instanceIds) {

        return instanceRepository.getAllInstances().stream()
                .filter(instance -> instanceIds.contains(instance.getInstanceId()))
                .collect(Collectors.toList());
    }

    /**
     * Seaches the service registry for a service or a wktLocation (boundry)
     * @param query Id of the service type
     * @param wktLocationFilter a wkt location that the service intersects with
     * @return a list of service that matched the given query
     */
    public List<InstanceMetadata> findServiceByWKTLocation(String query, String wktLocationFilter) {
        return instanceRepository.findServiceByWKTLocation(query, wktLocationFilter);
    }


}
