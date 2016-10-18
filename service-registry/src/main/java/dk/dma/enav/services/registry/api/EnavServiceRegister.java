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
package dk.dma.enav.services.registry.api;

import java.util.List;

/**
 * Enav service api for looking up service instance information from the Maritime cloud service registry.
 *
 */
public interface EnavServiceRegister {
    /**
     * Get all service instance descriptions which implements the given technical design and are valid in the
     * given area.
     * @param id identifies a technical design.
     * @param wktLocationFilter wkt describing the area of interest
     * @return instance describtions
     */
    List<InstanceMetadata> getServiceInstances(TechnicalDesignId id, String wktLocationFilter);

    /**
     * Find information for service instances identified by the given instance id's. This method will always
     * get the latest version of the required service instances.
     *
     * @param instanceIds list of instance id's retrieve infomation for
     * @return information for the required service instances
     */
    List<InstanceMetadata> getServiceInstances(List<String> instanceIds);
}
