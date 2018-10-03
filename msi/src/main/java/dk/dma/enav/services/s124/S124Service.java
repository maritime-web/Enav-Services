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
import dk.dma.enav.services.MessageLoaderTaskExecuter;
import dk.dma.enav.services.MessageLoaderTaskFactory;
import dk.dma.enav.services.s124.S124MessageLoaderTask.S124MessageLoaderTaskBuilder;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PreDestroy;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Singleton
@Startup
@Lock(LockType.READ)
@Slf4j
public class S124Service {
    @Inject
    private ApiClientFactory apiClientFactory;

    @Inject
    private EmbryoLogService embryoLogService;

    @Inject
    private MessageLoaderTaskExecuter<String> messageLoaderTaskExecuter;

    @PreDestroy
    void destroy() {
        messageLoaderTaskExecuter.shutdown();
    }

    List<String> getMessages(List<String> instanceIds, Integer id, Integer status, String wkt) {
        // Sanity check
        if (instanceIds != null && !instanceIds.isEmpty()) {

            MessageLoaderTaskFactory<String> messageLoaderTaskFactory;
            messageLoaderTaskFactory = serviceInstance -> new S124MessageLoaderTaskBuilder(embryoLogService, apiClientFactory)
                    .serviceInstance(serviceInstance)
                    .id(id)
                    .status(status)
                    .wkt(wkt)
                    .build();

            return messageLoaderTaskExecuter.getMessages(instanceIds, messageLoaderTaskFactory);
        }
        return new ArrayList<>();
    }
}
