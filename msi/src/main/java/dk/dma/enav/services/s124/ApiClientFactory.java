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
package dk.dma.enav.services.s124;

import dk.dma.enav.services.s124.api.PullApi;

public class ApiClientFactory {

    public PullApi create(String url) {
        ApiClient apiClient = new ApiClient();
        apiClient.setBasePath(url);
        apiClient.setConnectTimeout(15000);
        apiClient.setReadTimeout(15000);
        apiClient.setDebugging(true);

        return new PullApi(apiClient);
//        return new DummyPullApi();//TODO temporary implementation. Delete when real s-124 service is up and running
    }
}
