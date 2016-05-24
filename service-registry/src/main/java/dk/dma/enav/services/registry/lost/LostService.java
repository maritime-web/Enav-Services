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

import dk.dma.enav.services.registry.ServiceInstanceMetadata;
import dk.dma.enav.services.registry.ServiceLookupService;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Steen on 28-04-2016.
 *
 */
public class LostService implements ServiceLookupService {
    @Inject
    private LostHttpClient client;
    @Inject
    private ListServicesByLocationRequestFactory listServicesByLocationRequestFactory;
    @Inject
    private FindServiceRequestFactory findServiceRequestFactory;
    @Inject
    private ListServicesByLocationResponseParser listServicesByLocationResponseParser;
    @Inject
    private FindServiceResponseParser findServiceResponseParser;
    @Inject
    private ErrorConverter errorConverter;

    @Override
    public List<ServiceInstanceMetadata> findAllServices(double p1, double p2) {
        //dummy search coordinates
        p1 = p1 < 0.001 ? 55 : p1;
        p2 = p2 < 0.001 ? 11 : p2;
        String request = listServicesByLocationRequestFactory.create(p1, p2);
        String response = client.post(request);
        List<String> serviceIds = listServicesByLocationResponseParser.getServiceIds(response);

        return getServicesByIdsAndLocation(serviceIds, p1, p2).stream().filter(s -> s != null).collect(Collectors.toList());
    }

    @Override
    public List<ServiceInstanceMetadata> getServicesByIds(List<String> ids) {
        //dummy search coordinates
        double p1 = 55;
        double p2 = 11;
        return getServicesByIdsAndLocation(ids, p1, p2);
    }

    @Override
    public List<ServiceInstanceMetadata> getServicesByIdsAndLocation(List<String> ids, double p1, double p2) {
        return ids.stream().map(id -> getServiceByIdAndLocation(id, p1, p2)).collect(Collectors.toList());
    }

    @Override
    public ServiceInstanceMetadata getServiceByIdAndLocation(String id, double p1, double p2) {
        String request = findServiceRequestFactory.create(p1, p2, id);
        String response = client.post(request);

        try {
            return findServiceResponseParser.parseFindServiceResponse(response);
        } catch (LostErrorResponseException e) {
            return new ServiceInstanceMetadata(id, errorConverter.convert(e.getUnmarshalledResponse()));
        }
    }
}
