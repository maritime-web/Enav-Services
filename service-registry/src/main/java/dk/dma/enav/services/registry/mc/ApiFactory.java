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

import com.google.common.io.ByteStreams;
import dk.dma.embryo.common.configuration.Property;
import dk.dma.enav.services.registry.mc.api.InstanceResourceApi;
import dk.dma.enav.services.registry.mc.api.ServiceinstanceresourceApi;
import dk.dma.enav.services.registry.mc.api.TechnicaldesignresourceApi;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * Created by Steen on 17-10-2016.
 *
 */
public class ApiFactory {
    private static final String CERTIFICATE_PATH = "/DST Root CA X3.cer";
    private final String url;
    private final int connectionTimeout;
    private final byte[] certificateBytes;

    @Inject

    public ApiFactory(@Property("enav-service.service-registry.mc.endpoint.url") String url,
                      @Property("enav-service.service-registry.mc.connect.timeout") int connectionTimeout) {
        this.url = url;
        this.connectionTimeout = connectionTimeout;
        InputStream certificateStream = getClass().getResourceAsStream(CERTIFICATE_PATH);
        if (certificateStream == null) {
            throw new IllegalStateException("Missing certificate in classpath '" + CERTIFICATE_PATH + "'");
        }
        try {
            certificateBytes = ByteStreams.toByteArray(certificateStream);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read certificate from classpath '" + CERTIFICATE_PATH + "'", e);
        }
    }

    InstanceResourceApi createInstanceResourceApi() {
        return new InstanceResourceApi(createApiClient());
    }

    ServiceinstanceresourceApi createServiceinstanceresourceApi() {
        return new ServiceinstanceresourceApi(createApiClient());
    }

    TechnicaldesignresourceApi createTechnicaldesignresourceApi() {return new TechnicaldesignresourceApi(createApiClient());}

    // todo should this really be created multiple times ?
    public ApiClient createApiClient() {
        ApiClient apiClient = new ApiClient();
        apiClient.setBasePath(url);
        apiClient.setSslCaCert(new ByteArrayInputStream(certificateBytes));
        apiClient.setConnectTimeout(connectionTimeout);
        return apiClient;
    }
}
