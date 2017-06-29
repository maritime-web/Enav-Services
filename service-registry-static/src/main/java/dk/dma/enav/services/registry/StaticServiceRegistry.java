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

import dk.dma.embryo.common.configuration.Property;
import dk.dma.enav.services.registry.api.EnavServiceRegister;
import dk.dma.enav.services.registry.api.InstanceMetadata;
import dk.dma.enav.services.registry.api.TechnicalDesignId;
import dk.dma.enav.services.registry.api.VendorInfo;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

public class StaticServiceRegistry implements EnavServiceRegister {
    private final String url;

    @Inject
    public StaticServiceRegistry(@Property("enav-service.service-registry-static.nwnm.endpoint.url") String url) {
        this.url = url;
    }

    @Override
    public List<InstanceMetadata> getServiceInstances(TechnicalDesignId id, String wktLocationFilter) {
        throw new UnsupportedOperationException("");
    }

    @Override
    public List<InstanceMetadata> getServiceInstances(String wktLocationFilter) {
        InstanceMetadata res = new InstanceMetadata("NWNM", "1.0", 1L);
        res
                .setDescription("ArcticWeb specific service registry providing access to the NW-NM service")
                .setName("NWNM Service Endpoint")
                .setProducedBy(new VendorInfo("DMA"))
                .setProvidedBy(new VendorInfo("DMA"))
                .setUrl(url);
        return Collections.singletonList(res);
    }

    @Override
    public List<InstanceMetadata> getServiceInstances(List<String> instanceIds) {
        InstanceMetadata res = new InstanceMetadata("NWNM", "1.0", 1L);
        res
                .setDescription("ArcticWeb specific service registry providing access to the NW-NM service")
                .setName("NWNM Service Endpoint")
                .setProducedBy(new VendorInfo("DMA"))
                .setProvidedBy(new VendorInfo("DMA"))
                .setUrl(url);
        return Collections.singletonList(res);
    }
}
