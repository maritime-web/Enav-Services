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

import dk.dma.enav.services.registry.mc.ApiException;
import dk.dma.enav.services.registry.mc.model.Instance;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

/**
 * API tests for ServiceinstanceresourceApi
 */
public class ServiceinstanceresourceApiIT {

    private final ServiceinstanceresourceApi api = new ServiceinstanceresourceApi();

    /**
     * getAllInstances
     *
     * 
     *
     * @throws ApiException
     *          if the Api call fails
     */
    @Test
    public void getAllInstancesUsingGETTest() throws ApiException {
        Integer page = null;
        Integer size = null;
        List<String> sort = null;
        List<Instance> response = api.getAllInstancesUsingGET(page, size, sort);

        assertThat(response.size(), is(greaterThan(0)));
    }
    
    /**
     * getInstance
     *
     * 
     *
     * @throws ApiException
     *          if the Api call fails
     */
    @Test
    public void getInstanceUsingGETTest() throws ApiException {
        String id = null;
        String version = null;
        // Instance response = api.getInstanceUsingGET(id, version);

        // TODO: test validations
    }
    
    /**
     * searchInstancesByKeywords
     *
     * 
     *
     * @throws ApiException
     *          if the Api call fails
     */
    @Test
    public void searchInstancesByKeywordsUsingGETTest() throws ApiException {
        String query = null;
        Integer page = null;
        Integer size = null;
        List<String> sort = null;
        // List<Instance> response = api.searchInstancesByKeywordsUsingGET(query, page, size, sort);

        // TODO: test validations
    }
    
    /**
     * searchInstancesByLocation
     *
     * 
     *
     * @throws ApiException
     *          if the Api call fails
     */
    @Test
    public void searchInstancesByLocationUsingGETTest() throws ApiException {
        String latitude = null;
        String longitude = null;
        Integer page = null;
        Integer size = null;
        List<String> sort = null;
        // List<Instance> response = api.searchInstancesByLocationUsingGET(latitude, longitude, page, size, sort);

        // TODO: test validations
    }
    
    /**
     * searchInstances
     *
     * 
     *
     * @throws ApiException
     *          if the Api call fails
     */
    @Test
    public void searchInstancesUsingGETTest() throws ApiException {
        String query = null;
        Integer page = null;
        Integer size = null;
        List<String> sort = null;
        // List<Instance> response = api.searchInstancesUsingGET(query, page, size, sort);

        // TODO: test validations
    }
}
