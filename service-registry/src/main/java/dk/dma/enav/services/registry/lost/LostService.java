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
import org.slf4j.Logger;

import javax.inject.Inject;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Created by Steen on 28-04-2016.
 *
 */
public class LostService implements ServiceLookupService {
    @SuppressWarnings("CdiInjectionPointsInspection")
    @Inject
    private Logger logger;
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
    public List<ServiceInstanceMetadata> getServiceInstancesForService(String serviceTechnicalDesignId, double p1, double p2) {
        String lostServiceId = mapToLostServiceId(serviceTechnicalDesignId);
        String request = listServicesByLocationRequestFactory.create(p1, p2, lostServiceId);
        String response = client.post(request);
        List<String> serviceIds = listServicesByLocationResponseParser.getServiceIds(response);

        return getServiceInstances(serviceIds, p1, p2).stream()
                .filter(s -> s != null && s.getInstanceId() != null)
                .map(s -> {s.setServiceId(serviceTechnicalDesignId); return s;})
                .collect(toList());
    }

    private String mapToLostServiceId(String serviceTechnicalDesignId) {
        String res = serviceTechnicalDesignId;
        if (serviceTechnicalDesignId.equals("urn:mrnx:mcl:service:dma:nw-nm:rest")) {
            res = "urn:mrnx:mcl:service";
        }

        return res;
    }

    @Override
    public List<ServiceInstanceMetadata> getServiceInstances(List<String> instanceIds, double p1, double p2) {
        return instanceIds.stream().map(id -> getServiceInstance(id, p1, p2)).collect(toList());
    }

    @Override
    public ServiceInstanceMetadata getServiceInstance(String instanceId, double p1, double p2) {
        String request = findServiceRequestFactory.create(p1, p2, instanceId);
        String response = client.post(request);

        try {
            return findServiceResponseParser.parseFindServiceResponse(response);
        } catch (LostErrorResponseException e) {
            return new ServiceInstanceMetadata(instanceId, errorConverter.convert(e.getUnmarshalledResponse()));
        }
    }
}
