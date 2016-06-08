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
import ietf.lost1.ObjectFactory;

import javax.inject.Inject;

/**
 * Created by Steen on 03-05-2016.
 *
 */
class FindServiceRequestFactory {
    private final JaxbAdapter jaxbAdapter;
    private final LocationFactory locationFactory;

    @Inject
    FindServiceRequestFactory(JaxbAdapter jaxbAdapter, LocationFactory locationFactory) {
        this.jaxbAdapter = jaxbAdapter;
        this.locationFactory = locationFactory;
    }

    String create(double p1, double p2, String serviceId) {
        ObjectFactory objectFactory = new ObjectFactory();
        FindService findServiceRequest = objectFactory.createFindService();
        findServiceRequest.setRecursive(true);
        findServiceRequest.setService(serviceId);
        findServiceRequest.setServiceBoundary("value");

        findServiceRequest.getLocation().add(locationFactory.createLocation(p1, p2));

        return jaxbAdapter.marshal(findServiceRequest);
    }
}