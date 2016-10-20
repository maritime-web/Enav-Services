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

import dk.dma.enav.services.registry.api.EnavServiceRegister;
import dk.dma.enav.services.registry.api.InstanceMetadata;
import org.apache.commons.lang.StringUtils;
import org.jboss.resteasy.annotations.GZIP;
import org.jboss.resteasy.annotations.cache.NoCache;
import org.niord.model.message.MessageVo;
import org.slf4j.Logger;

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
import java.net.URLEncoder;
import java.util.List;
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

    public static final String NW_NM_API = "public/v1/messages";

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
    public List<MessageVo> getNwNmMessages(
            @QueryParam("instanceId") List<String> instanceIds,
            @QueryParam("mainType") String mainType,
            @QueryParam("lang") @DefaultValue("en") String lang,
            @QueryParam("wkt") String wkt) throws Exception {

        List<MessageVo> result = new CopyOnWriteArrayList<>();


        // Compose the parameters
        StringBuilder params = new StringBuilder();
        checkConcatParam(params, "mainType", mainType);
        checkConcatParam(params, "lang", lang);
        checkConcatParam(params, "wkt", wkt);


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
                    // Create the URL
                    String url = serviceInstance.getUrl();
                    if (!url.endsWith("/"))  {
                        url += "/";
                    }
                    url += NW_NM_API + params;

                    // Start the download
                    compService.submit(new MessageLoaderTask(url));
                    taskNo++;
                }
            }

            // Collect the results
            for (int x = 0; x < taskNo; x++) {
                try {
                    Future<List<MessageVo>> future = compService.take();
                    result.addAll(future.get());
                } catch (Exception e) {
                    logger.error("Error loading messages", e);
                    // Do not re-throw exception, since others may succeed
                }
            }
        }


        return result;
    }


    /** If defined, appends the given parameter */
    private StringBuilder checkConcatParam(StringBuilder params, String name, String value) {
        if (StringUtils.isNotBlank(value)) {
            try {
                params.append(params.length() == 0 ? "?" : "&");
                params.append(name).append("=").append(URLEncoder.encode(value, "UTF-8"));
            } catch (Exception ignored) {
            }
        }
        return params;
    }


}
