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

import dk.dma.enav.services.registry.api.ErrorDescription;
import dk.dma.enav.services.registry.api.Errors;
import dk.dma.enav.services.registry.api.InstanceMetadata;
import dk.dma.enav.services.registry.api.TechnicalDesignId;
import dk.dma.enav.services.registry.mc.model.Instance;

import javax.inject.Inject;

/**
 * Created by Steen on 17-10-2016.
 *
 */
public class InstanceMapper {
    private final InstanceXmlParser instanceXmlParser;

    @Inject
    InstanceMapper(InstanceXmlParser instanceXmlParser) {
        this.instanceXmlParser = instanceXmlParser;
    }

    InstanceMetadata toMetaData(Instance instance) {
        System.out.println(instance);
        String instanceId = instance.getInstanceId();
        String name = instance.getName();
        String boundary;
        String url;
        String designId;
        String designVersion;
        try {
            InstanceDetails details = getDetails(instance);
            boundary = details.getCoverage();
            url = details.getUrl();
            designId = details.getDesignId();
            designVersion = details.getDesignVersion();
        } catch (IllegalArgumentException e) {
            return new InstanceMetadata(instanceId, new Errors("No details", new ErrorDescription("No details", e.getMessage(), null)));
        }

        return new InstanceMetadata(instanceId, name, new TechnicalDesignId(designId, designVersion), boundary, url);
    }

    private InstanceDetails getDetails(Instance instance) {
        return instanceXmlParser.parseInstanceXml(instance.getInstanceAsXml());
    }
}
