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
package dk.dma.enav.services.nwnm;

import dk.dma.embryo.common.log.EmbryoLogService;
import dk.dma.enav.services.nwnm.MessageLoaderTask.MessageLoaderTaskBuilder;
import dk.dma.enav.services.registry.api.EnavServiceRegister;
import dk.dma.enav.services.registry.api.InstanceMetadata;
import lombok.extern.slf4j.Slf4j;
import net.jodah.expiringmap.ExpiringMap;
import org.niord.model.message.MessageVo;

import javax.annotation.PreDestroy;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Service for searching NW-NM services as defined in the Maritime Cloud Service Registry.
 * <p>
 * Fetching messages is tied to a list of service instance ID's, which are then queried in parallel.
 * <p>
 * If fetching messages yields an error, an empty result set will be cached for 1 minute,
 * rather than throwing an exception.
 */
@Singleton
@Startup
@Lock(LockType.READ)
@Slf4j
public class NwNmService {

    @Inject
    private EnavServiceRegister enavServiceRegister;

    @Inject
    private EmbryoLogService embryoLogService;

    @Inject
    private NwNmConnectionManager connectionManager;

    private final ExecutorService executor = Executors.newFixedThreadPool(4);

    /**
     * A cache that contains the entire set of published messages per service instance ID
     */
    ExpiringMap<String, NwNmServiceInstanceData> instanceMessageCache
            = ExpiringMap.builder()
                .variableExpiration()
                .build();

    @PreDestroy
    void destroy() {
        executor.shutdown();
        if (connectionManager != null) {
            connectionManager.shutdown();
        }
    }


    /**
     * Fetches the published messages from the given service instances in parallel
     * @param instanceIds the MC Service Registry instance IDs to fetch messages from
     * @param lang the ISO-2 language code, e.g. "en"
     * @param wkt optionally a WKT for the geometry extent of the area
     * @return the list of messages for the given parameters
     */
    public List<MessageVo> getNwNmMessages(
            List<String> instanceIds,
            String mainType,
            String lang,
            String wkt) {

        List<MessageVo> messages = new CopyOnWriteArrayList<>();

        // Sanity check
        if (instanceIds != null && !instanceIds.isEmpty()) {

            // Start the message download in parallel
            CompletionService<List<MessageVo>> compService = new ExecutorCompletionService<>(executor);
            int taskNo = 0;

            List<InstanceMetadata> serviceInstances = enavServiceRegister.getServiceInstances(instanceIds);
            for (String instanceId : instanceIds) {

                // Find the service instance
                InstanceMetadata serviceInstance = serviceInstances.stream()
                        .filter(s -> s.getInstanceId().equals(instanceId))
                        .findFirst()
                        .orElse(null);

                if (serviceInstance != null) {

                    // Construct a task for fetching messages
                    MessageLoaderTask task = new MessageLoaderTaskBuilder(embryoLogService, connectionManager)
                            .instanceMessageCache(instanceMessageCache)
                            .serviceInstance(serviceInstance)
                            .mainType(mainType)
                            .lang(lang)
                            .wkt(wkt)
                            .build();

                    // Start the download
                    compService.submit(task);
                    taskNo++;
                }
            }

            // Collect the results
            for (int x = 0; x < taskNo; x++) {
                try {
                    Future<List<MessageVo>> future = compService.take();
                    messages.addAll(future.get());
                } catch (Exception e) {
                    log.error("Error loading messages", e);
                    // Do not re-throw exception, since others may succeed
                }
            }
        }

        return messages;
    }

}
