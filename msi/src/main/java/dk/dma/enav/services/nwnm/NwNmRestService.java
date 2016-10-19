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

import dk.dma.enav.services.nwnm.api.MessagelistApi;
import dk.dma.enav.services.nwnm.model.Message;
import dk.dma.enav.services.registry.api.EnavServiceRegister;
import dk.dma.enav.services.registry.api.InstanceMetadata;
import org.jboss.resteasy.annotations.GZIP;
import org.jboss.resteasy.annotations.cache.NoCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * REST endpoint for searching NW-NM services as defined in the Maritime Cloud Service Registry.
 * <p>
 * The endpoint is called with a list of service instance ID's, which are then queried in parallel.
 * <p>
 * TODO: The service should be called with a configurable list of NW-NM domains. For now we fetch NW's.
 */
@Singleton
@Startup
@Path("/nw-nm")
@Lock(LockType.READ)
public class NwNmRestService {
    @SuppressWarnings("CdiInjectionPointsInspection")
    @Inject
    private Logger logger;
    @Inject
    private EnavServiceRegister enavServiceRegister;

    private ExecutorService executor;

    @PostConstruct
    void init() {
        executor = Executors.newFixedThreadPool(4);
    }

    @PreDestroy
    void destroy() {
        executor.shutdown();
    }


    /**
     * Fetches the published messages from the given service instances in parallel
     * @param instanceIds the MC Service Registry instance IDs to fetch messages from
     * @param lang the ISO-2 language code, e.g. "en"
     * @param wkt optionally a WKT for the geometry extent of the area
     */
    @GET
    @Path("/messages")
    @Produces("application/json;charset=UTF-8")
    @GZIP
    @NoCache
    public List<Message> getNwNmMessages(
            @QueryParam("instanceId") List<String> instanceIds,
            @QueryParam("lang") @DefaultValue("en") String lang,
            @QueryParam("wkt") String wkt)  {

        List<Message> result = new CopyOnWriteArrayList<>();

        // Sanity check
        if (instanceIds == null || instanceIds.isEmpty()) {
            return result;
        }

        // Start the message download in parallel
        CompletionService<List<Message>> compService = new ExecutorCompletionService<>(executor);
        int taskNo = 0;

        List<InstanceMetadata> serviceInstances = enavServiceRegister.getServiceInstances(instanceIds);
        for (String instanceId : instanceIds) {

            // Find the service instance
            InstanceMetadata serviceInstance = serviceInstances.stream()
                    .filter(s -> s.getInstanceId().equals(instanceId))
                    .findFirst()
                    .orElse(null);

            if (serviceInstance != null) {
                MessagelistApi nwNmApi = new MessagelistApiBuilder()
                        .basePath(serviceInstance.getUrl())
                        .build();

                // Start the download
                compService.submit(new MessageLoaderTask(nwNmApi, lang, wkt));
                taskNo++;
            }
        }

        // Collect the results
        for (int x = 0; x < taskNo; x++) {
            try {
                Future<List<Message>> future = compService.take();
                result.addAll(future.get());
            } catch (Exception e) {
                logger.error("Error loading messages", e);
                // Do not re-throw exception, since others may succeed
            }
        }

        return result;
    }


    /**
     * Task that fetches messages from a given Maritime Cloud NW-NM service instance
     */
    private static final class MessageLoaderTask implements Callable<List<Message>> {

        final MessagelistApi nwNmApi;
        final String lang;
        final String wkt;

        Logger logger = LoggerFactory.getLogger(MessageLoaderTask.class);

        /** Constructor */
        MessageLoaderTask(MessagelistApi nwNmApi, String lang, String wkt)  {
            this.nwNmApi = Objects.requireNonNull(nwNmApi);
            this.lang = lang;
            this.wkt = wkt;
        }

        /** Fetch the messages from the given service instance. */
        @Override
        public List<Message> call() throws Exception {
            try {
                long t0 = System.currentTimeMillis();

                // TODO: Should be called with a configurable list of domains. For now we just fetch NW's

                List<Message> messages = nwNmApi.search(lang, null, null, Collections.singletonList("NW"), wkt);
                logger.info("NW-NM search found " + messages.size() + " messages in "
                        + (System.currentTimeMillis() - t0) + " ms");
                return messages;

            } catch (Exception e) {
                logger.error("Error fetching NW-NM messages from " + nwNmApi.getApiClient().getBasePath()
                        + ": " + e.getMessage());
                throw new WebApplicationException("Failed loading NW-NM messages: " + e.getMessage(), 500);
            }
        }
    }
}
