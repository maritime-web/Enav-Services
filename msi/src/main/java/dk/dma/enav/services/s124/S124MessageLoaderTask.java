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

import dk.dma.embryo.common.log.EmbryoLogService;
import dk.dma.enav.services.registry.api.InstanceMetadata;
import dk.dma.enav.services.s124.api.PullApi;
import dk.dma.enav.services.s124.model.GetMessageResponseObject;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

@Slf4j
public final class S124MessageLoaderTask implements Callable<List<String>> {

    private final EmbryoLogService embryoLogService;
    private final ApiClientFactory apiClientFactory;
    private final InstanceMetadata serviceInstance;
    private final Integer id;
    private final Integer status;
    private final String wkt;

    private S124MessageLoaderTask(EmbryoLogService embryoLogService, ApiClientFactory apiClientFactory, InstanceMetadata serviceInstance, Integer id, Integer status, String wkt) {

        this.embryoLogService = embryoLogService;
        this.apiClientFactory = apiClientFactory;
        this.serviceInstance = serviceInstance;
        this.id = id;
        this.status = status;
        this.wkt = wkt;
    }

    @Override
    public List<String> call() throws Exception {
        List<String> messages;

        try {
            messages = fetchS124Messages();
            embryoLogService.info("Loaded " + messages.size() + " messages from " + serviceInstance.getInstanceId());
        } catch (Exception e) {
            log.error("Failed loading S-124 messages for instance "
                    + serviceInstance.getInstanceId() + " : " + e.getMessage(), e);
            embryoLogService.error("Failed loading S-124 messages for instance "
                    + serviceInstance.getInstanceId(), e);
            messages = new ArrayList<>();
        }


        log.info(String.format("Search for id=%s, status=%s, wkt=%s -> returning %d messages",
                id, status, wkt, messages.size()));


        return messages;
    }

    private List<String> fetchS124Messages() throws ApiException {
        long t0 = System.currentTimeMillis();

        PullApi pullApi = apiClientFactory.create(serviceInstance.getUrl());

        GetMessageResponseObject getMessageResponseObject = pullApi.s124Get(id, status, wkt);

        log.info(String.format(
                "Loaded %d S-124 messages in %s ms",
                getMessageResponseObject.getMessages().size(),
                System.currentTimeMillis() - t0));

        return getMessageResponseObject.getMessages();
    }

    /**
     * Builder class for the S124MessageLoaderTask class
     **/
    @Setter
    @Accessors(chain = true, fluent = true)
    public static class S124MessageLoaderTaskBuilder {
        private final EmbryoLogService embryoLogService;
        private final ApiClientFactory apiClientFactory;
        private InstanceMetadata serviceInstance;
        private Integer id;
        private Integer status;
        private String wkt;

        S124MessageLoaderTaskBuilder(EmbryoLogService embryoLogService, ApiClientFactory apiClientFactory) {
            this.embryoLogService = embryoLogService;
            this.apiClientFactory = apiClientFactory;
        }

        public S124MessageLoaderTask build() {
            return new S124MessageLoaderTask(embryoLogService, apiClientFactory, serviceInstance, id, status, wkt);
        }
    }
}
