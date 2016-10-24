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

import dk.dma.enav.services.registry.api.Error;
import dk.dma.enav.services.registry.api.InstanceMetadata;
import dk.dma.enav.services.registry.mc.model.Instance;

import javax.inject.Inject;
import java.util.List;

/**
 *
 */
public class InstanceMapper {
    private final InstanceXmlParser instanceXmlParser;

    @Inject
    InstanceMapper(InstanceXmlParser instanceXmlParser) {
        this.instanceXmlParser = instanceXmlParser;
    }

    InstanceMetadata toMetaData(Instance instance) {
        InstanceMetadata result = new InstanceMetadata(instance.getInstanceId())
                .withInstanceName(instance.getName());

        InstanceDetails details = getDetails(instance);
        result
                .withUrl(details.getUrl())
                .withTechnicalDesignId(details.getDesignId());
        try {
            result.withBoundary(details.getCoverage());
        } catch (IllegalArgumentException e) {
            result.addError(new Error(e));
        }

        List<Error> validationErrors = result.validate();
        result.addAllErrors(validationErrors);

        return result;
    }

    private InstanceDetails getDetails(Instance instance) {
        try {
            return instanceXmlParser.parseInstanceXml(instance.getInstanceAsXml());
        } catch (Exception e) {
            return new InstanceDetails();
        }
    }
}
