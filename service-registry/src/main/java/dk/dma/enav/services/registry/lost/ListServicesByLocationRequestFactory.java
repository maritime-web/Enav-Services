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

import ietf.lost1.ListServicesByLocation;
import ietf.lost1.ObjectFactory;

import javax.inject.Inject;

/**
 * Created by Steen on 28-04-2016.
 *
 */
class ListServicesByLocationRequestFactory {
    private final JaxbAdapter jaxbAdapter;
    private final LocationFactory locationFactory;

    @Inject
    public ListServicesByLocationRequestFactory(JaxbAdapter jaxbAdapter, LocationFactory locationFactory) {
        this.jaxbAdapter = jaxbAdapter;
        this.locationFactory = locationFactory;
    }

    String create(double p1, double p2, String service) {
        ObjectFactory objectFactory = new ObjectFactory();
        ListServicesByLocation listServicesByLocationRequest = objectFactory.createListServicesByLocation();
        listServicesByLocationRequest.setRecursive(true);
        listServicesByLocationRequest.getLocation().add(locationFactory.createLocation(p1, p2));
        listServicesByLocationRequest.setService(service);

        return jaxbAdapter.marshal(listServicesByLocationRequest);
    }
}
