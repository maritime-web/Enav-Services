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

import java.util.ArrayList;
import java.util.List;

import dk.dma.enav.services.registry.mc.ApiClient;
import dk.dma.enav.services.registry.mc.model.Instance;

/**
 * Decorating @link{dk.dma.enav.services.registry.mc.api.ServiceinstanceresourceApi}
 */
public class InstanceResourceApi {
    private static final int PAGE_SIZE = 7;
    private final ApiClient apiClient;

    public InstanceResourceApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public List<Instance> getAll() {
        List<Instance> result = new ArrayList<>();
        new InstanceIterator(apiClient, PAGE_SIZE).forEachRemaining(result::addAll);
        return result;
    }

    public List<Instance> searchByGeometry(String query, String wktLocationFilter) {
        List<Instance> result = new ArrayList<>();
        new InstanceIterator(apiClient, PAGE_SIZE, InstanceIterator.createSearchDataLoader(query, wktLocationFilter)).forEachRemaining(result::addAll);
        return result;
    }

}
