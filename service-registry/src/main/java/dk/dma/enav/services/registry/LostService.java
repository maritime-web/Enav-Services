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

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Steen on 28-04-2016.
 *
 */
public class LostService {
    @Inject
    private LostServiceClient client;
    @Inject
    private ListServicesByLocationRequestFactory listServicesByLocationRequestFactory;
    @Inject
    private FindServiceRequestFactory findServiceRequestFactory;
    @Inject
    private LostResponseParser responseParser;

    public List<ServiceInstanceMetadata> findAllServices(double p1, double p2) {
        //dummy search coordinates
        p1 = p1 < 0.001 ? 55 : p1;
        p2 = p2 < 0.001 ? 11 : p2;
        String request = listServicesByLocationRequestFactory.createRequest(p1, p2);
        String response = client.post(request);
        List<String> serviceIds = responseParser.getServiceIds(response);

        return getServicesByIdsAndLocation(serviceIds, p1, p2).stream().filter(s -> s != null).collect(Collectors.toList());
    }

    public List<ServiceInstanceMetadata> getServicesByIds(List<String> ids) {
        //dummy search coordinates
        double p1 = 55;
        double p2 = 11;
        return getServicesByIdsAndLocation(ids, p1, p2);
    }

    public List<ServiceInstanceMetadata> getServicesByIdsAndLocation(List<String> ids, double p1, double p2) {
        return ids.stream().map(id -> getServiceByIdAndLocation(id, p1, p2)).collect(Collectors.toList());
    }

    public ServiceInstanceMetadata getServiceByIdAndLocation(String id, double p1, double p2) {
        String request = findServiceRequestFactory.createFindServiceRequest(p1, p2, id);
        String response = client.post(request);

        return responseParser.parseFindServiceResponse(response);
    }
}
