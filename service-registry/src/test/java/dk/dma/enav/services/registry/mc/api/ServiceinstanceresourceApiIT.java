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
import org.junit.Ignore;
import org.junit.Test;

import java.util.Iterator;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

/**
 * API tests for ServiceinstanceresourceApi
 */
public class ServiceinstanceresourceApiIT {

    private final ApiClient apiClient = new ApiFactory("http://sr-test.maritimecloud.net:8080", 2000).createApiClient();
    private final ServiceinstanceresourceApi api = new ServiceinstanceresourceApi(apiClient);

    @Test
    public void getAllInstancesUsingGETTest() throws ApiException {
        Integer page = null;
        Integer size = null;
        String includeDoc = null;
        String authentication = null;
        List<String> sort = null;
        List<Instance> response = api.getAllInstancesUsingGET(page, size, includeDoc, authentication, sort);
        System.out.println("Services = " + response.size());
        for (Iterator<Instance> i = response.iterator( ); i.hasNext(); ) {
            Instance instance = i.next();
            System.out.println("Id = " + instance.getInstanceId() + " ver. "  + instance.getVersion());
            
        }
        Iterator<Instance> iterator = response.iterator();
        System.out.println(response);
        assertThat(response.size(), is(greaterThan(0)));
    }
    
    @Test
    // The service's Id are not visible on test-management.maritimecloud.net, so you have to call the getAllServices API to get the id
    @Ignore("sr-test has a service with 8, but for some reason it is not returned.")
    public void getInstanceByIdAndVersion() throws ApiException {
        int id = 8;
        String version = "0.1";
        ApiResponse<Instance> response = api.getInstanceUsingGETWithHttpInfo(String.valueOf(id), version, null, null);
        System.out.println(response.getData());
        //assertThat(response.getData().getStatus()!= null);
    }

}
