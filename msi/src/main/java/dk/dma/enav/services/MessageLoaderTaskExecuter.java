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
package dk.dma.enav.services;

import dk.dma.enav.services.registry.api.EnavServiceRegister;
import dk.dma.enav.services.registry.api.InstanceMetadata;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
public class MessageLoaderTaskExecuter<E> {
    @Inject
    private EnavServiceRegister enavServiceRegister;

    private final ExecutorService executor = Executors.newFixedThreadPool(4);

    public void shutdown() {
        executor.shutdown();
    }

    public List<E> getMessages(List<String> instanceIds, MessageLoaderTaskFactory<E> messageLoaderTaskFactory) {
        List<E> messages = new CopyOnWriteArrayList<>();

        // Start the message download in parallel
        CompletionService<List<E>> compService = new ExecutorCompletionService<>(executor);
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
                Callable<List<E>> task = messageLoaderTaskFactory.createMessageLoaderTask(serviceInstance);
                // Start the download
                compService.submit(task);
                taskNo++;
            }
        }

        // Collect the results
        for (int x = 0; x < taskNo; x++) {
            try {
                Future<List<E>> future = compService.take();
                messages.addAll(future.get());
            } catch (Exception e) {
                log.error("Error loading messages", e);
                // Do not re-throw exception, since others may succeed
            }
        }

        return messages;
    }
}
