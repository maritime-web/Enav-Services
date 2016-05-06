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
package dk.dma.embryo.msi.rs;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dk.dma.enav.services.registry.LostService;
import dk.dma.enav.services.registry.ServiceInstanceMetadata;
import org.apache.commons.lang.StringUtils;
import org.jboss.resteasy.annotations.GZIP;
import org.jboss.resteasy.annotations.cache.NoCache;
import org.niord.model.vo.MessageVo;
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
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
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
 * Created by Steen on 03-05-2016.
 *
 */
@Singleton
@Startup
@Path("/nw-nm")
@Lock(LockType.READ)
public class MwNmRestService {
    @SuppressWarnings("CdiInjectionPointsInspection")
    @Inject
    private Logger logger;
    @Inject
    private LostService lostService;

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
            @QueryParam("lang") @DefaultValue("en") String lang,
            @QueryParam("wkt") String wkt)  {

        List<MessageVo> result = new CopyOnWriteArrayList<>();

        // Sanity check
        if (instanceIds.isEmpty()) {
            return result;
        }

        // Compose the parameters
        StringBuilder params = new StringBuilder();
        checkConcatParam(params, "lang", lang);
        checkConcatParam(params, "wkt", wkt);

        // Start the message download in parallel
        CompletionService<List<MessageVo>> compService = new ExecutorCompletionService<>(executor);
        int taskNo = 0;

        List<ServiceInstanceMetadata> serviceInstances = lostService.getServicesByIds(instanceIds);
        for (String instanceId : instanceIds) {

            // Find the service instance
            ServiceInstanceMetadata service = serviceInstances.stream()
                    .filter(s -> s.getInstanceId().equals(instanceId))
                    .findFirst()
                    .orElse(null);

            if (service != null) {
                // Create the URL
                String url = service.getUrl() + params;
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

        return result;
    }


    /** If defined, appends the given parameter */
    private StringBuilder checkConcatParam(StringBuilder params, String name, String value) {
        if (StringUtils.isNotBlank(value)) {
            try {
                params.append(params.length() == 0 ? "?" : "&");
                params.append(name).append("=").append(URLEncoder.encode(value, "UTF-8"));
            } catch (UnsupportedEncodingException ignored) {
            }
        }
        return params;
    }


    /**
     * Task that downloads messages from a given url
     */
    private static final class MessageLoaderTask implements Callable<List<MessageVo>> {

        String url;
        Logger logger = LoggerFactory.getLogger(MessageLoaderTask.class);

        /** Constructor */
        MessageLoaderTask(String url){
            this.url = Objects.requireNonNull(url);
        }

        /** Download the messages from the given service instance. */
        @Override
        public List<MessageVo> call() throws Exception {
            long t0 = System.currentTimeMillis();
            try {
                URLConnection con = new URL(url).openConnection();
                con.setConnectTimeout(5000); //  5 seconds
                con.setReadTimeout(10000);   // 10 seconds

                try (InputStream is = con.getInputStream()) {
                    ObjectMapper mapper = new ObjectMapper();
                    List<MessageVo> messages = mapper.readValue(is, new TypeReference<List<MessageVo>>(){});

                    logger.info(String.format(
                            "Loaded %d NW-NM messages in %s ms",
                            messages.size(),
                            System.currentTimeMillis() - t0));

                    return messages;
                }

            } catch (Exception e) {
                logger.error("Failed loading NW-NM messages: " + e.getMessage());
                throw new WebApplicationException("Failed loading NW-NM messages: " + e.getMessage(), 500);
            }
        }
    }


}
