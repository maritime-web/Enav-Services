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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

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
    private static final int CACHE_TIMEOUT_MINUTES = 3;

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
    public Response getNwNmMessages(
            @QueryParam("instanceId") List<String> instanceIds,
            @QueryParam("mainType") String mainType,
            @QueryParam("lang") @DefaultValue("en") String lang,
            @QueryParam("wkt") String wkt,
            @Context Request request,
            @Context UriInfo uriInfo) throws Exception {

        List<MessageVo> messages = new CopyOnWriteArrayList<>();


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
                    messages.addAll(future.get());
                } catch (Exception e) {
                    logger.error("Error loading messages", e);
                    // Do not re-throw exception, since others may succeed
                }
            }
        }

        // Compute caching expiration
        Date expirationDate = new Date(System.currentTimeMillis() + 1000L * 60L * CACHE_TIMEOUT_MINUTES);

        // Check for an ETag match
        EntityTag etag = new EntityTag(etagForMessages(instanceIds, mainType, lang, messages), true);
        Response.ResponseBuilder responseBuilder = request.evaluatePreconditions(etag);
        if (responseBuilder != null) {
            // ETag match
            return responseBuilder
                    .expires(expirationDate)
                    .build();
        }

        return Response
                .ok(messages)
                .expires(expirationDate)
                .tag(etag)
                .build();
    }


    /** Computes a E-tag value for the message list **/
    private String etagForMessages(List<String> instanceIds, String mainType, String lang, List<MessageVo> messages) {
        // We create an etag value that excludes the WKT but include all other parameters and the
        // accumulated update timestamp of the messages.
        // Should allow us to return a match if the bounding box changes, but the search result is the same


        AtomicLong ts = new AtomicLong();
        instanceIds.forEach(i -> ts.addAndGet(i.hashCode()));
        messages.forEach(m -> ts.addAndGet(m.getUpdated().getTime()));
        return messages.size() + "_"+ ts + "_" + mainType + "_" + lang;
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
