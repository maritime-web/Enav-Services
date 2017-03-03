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

import javax.inject.Inject;

import dk.dma.embryo.common.configuration.Property;
import dk.dma.enav.services.registry.mc.api.InstanceResourceApi;
import dk.dma.enav.services.registry.mc.api.ServiceinstanceresourceApi;
import dk.dma.enav.services.registry.mc.api.TechnicaldesignresourceApi;

/**
 * Created by Steen on 17-10-2016.
 *
 */
public class ApiFactory {
    private final String url;
    private final int connectionTimeout;

    @Inject
    public ApiFactory(@Property("enav-service.service-registry.mc.endpoint.url") String url,
                      @Property("enav-service.service-registry.mc.connect.timeout") int connectionTimeout) {
        this.url = url;
        this.connectionTimeout = connectionTimeout;
    }

    InstanceResourceApi createInstanceResourceApi() {
        return new InstanceResourceApi(createApiClient());
    }

    ServiceinstanceresourceApi createServiceinstanceresourceApi() {
        return new ServiceinstanceresourceApi(createApiClient());
    }

    TechnicaldesignresourceApi createTechnicaldesignresourceApi() {return new TechnicaldesignresourceApi(createApiClient());}

    public ApiClient createApiClient() {
        ApiClient apiClient = new ApiClient();
        apiClient.setBasePath(url);
        apiClient.setVerifyingSsl(false);   // temp workaround because the JVM does not include the LetsEncrypt root certificate
        apiClient.setConnectTimeout(connectionTimeout);
        return apiClient;
    }
}
