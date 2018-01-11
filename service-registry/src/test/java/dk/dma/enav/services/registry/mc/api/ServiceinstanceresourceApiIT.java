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

package dk.dma.enav.services.registry.mc.api;

import dk.dma.enav.services.registry.mc.ApiClient;
import dk.dma.enav.services.registry.mc.ApiException;
import dk.dma.enav.services.registry.mc.ApiFactory;
import dk.dma.enav.services.registry.mc.ApiResponse;
import dk.dma.enav.services.registry.mc.model.Instance;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

/**
 * API tests for ServiceinstanceresourceApi
 */
public class ServiceinstanceresourceApiIT {

    private final ApiClient apiClient = new ApiFactory("https://sr.maritimecloud.net", 2000).createApiClient();
    private final ServiceinstanceresourceApi api = new ServiceinstanceresourceApi(apiClient);

    @Test
    public void getAllInstancesUsingGETTest() throws ApiException {
        String includeDoc = "false";
        List<Instance> response = api.getAllInstancesUsingGET(includeDoc, null, null, null);
        assertThat(response.size(), is(greaterThan(0)));
    }
    
    @Test
    public void getInstanceByIdAndVersion() throws ApiException {
        String id = "urn:mrn:mcl:service:instance:dma:nw-nm-test";
        String version = "0.4";
        ApiResponse<Instance> response = api.getInstanceUsingGETWithHttpInfo(id, version, null, null, null);
        assertThat(response.getData().getStatus(), notNullValue());
    }

}
