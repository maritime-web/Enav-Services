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

import java.util.List;

/**
 * Created by Steen on 18-05-2016.
 *
 */
public interface ServiceLookupService {
    List<ServiceInstanceMetadata> findAllServices(double p1, double p2);

    List<ServiceInstanceMetadata> getServicesByIds(List<String> ids);

    List<ServiceInstanceMetadata> getServicesByIdsAndLocation(List<String> ids, double p1, double p2);

    ServiceInstanceMetadata getServiceByIdAndLocation(String id, double p1, double p2);
}
