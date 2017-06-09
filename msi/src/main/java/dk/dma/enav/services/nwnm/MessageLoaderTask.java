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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vividsolutions.jts.geom.Geometry;
import dk.dma.embryo.common.log.EmbryoLogService;
import dk.dma.enav.services.registry.api.InstanceMetadata;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.niord.model.DataFilter;
import org.niord.model.message.MessageVo;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Task that fetches messages from a given Maritime Cloud NW-NM service instance.
 * <p>
 * If fetching messages yields an error, an empty result set will be cached for 1 minute,
 * rather than throwing an exception.
 */
@Slf4j
final class MessageLoaderTask implements Callable<List<MessageVo>> {

    public static final String NW_NM_API = "public/v1/messages";
    public static final DataFilter MESSAGE_DETAILS_FILTER =
            DataFilter.get().fields("Message.details", "Message.geometry", "Area.parent", "Category.parent");

    private final EmbryoLogService embryoLogService;
    private final HttpURLConnectionFactory httpURLConnectionFactory;
    private final ExpiringMap<String, NwNmServiceInstanceData> instanceMessageCache;
    private final InstanceMetadata serviceInstance;
    private final String mainType;
    private final String lang;
    private final String wkt;

    /** Constructor */
    private MessageLoaderTask(
            EmbryoLogService embryoLogService,
            HttpURLConnectionFactory httpURLConnectionFactory,
            ExpiringMap<String, NwNmServiceInstanceData> instanceMessageCache,
            InstanceMetadata serviceInstance,
            String mainType,
            String lang,
            String wkt)  {
        this.embryoLogService = embryoLogService;
        this.httpURLConnectionFactory = httpURLConnectionFactory;

        this.instanceMessageCache = instanceMessageCache;
        this.serviceInstance = Objects.requireNonNull(serviceInstance);
        this.mainType = mainType;
        this.lang = StringUtils.defaultIfBlank(lang, "en");
        this.wkt = wkt;
    }


    /** Fetch the messages from the given service instance. */
    @Override
    public List<MessageVo> call() throws Exception {

        // Check if messages are cached
        NwNmServiceInstanceData data = getCachedInstanceData();

        if (data == null) {
            try {
                // Load data and cache it
                List<MessageVo> messages = fetchNwNmMessages();
                Map<String, List<Geometry>> geometries = computeMessageGeometries(messages);
                data = new NwNmServiceInstanceData(messages, geometries);
                cachedInstanceData(data);
                embryoLogService.info("Loaded " + messages.size() + " messages from " + serviceInstance.getInstanceId());
            } catch (Exception e) {
                log.error("Failed loading NW-NM messages for instance "
                        + serviceInstance.getInstanceId() + " : " + e.getMessage());
                embryoLogService.error("Failed loading NW-NM messages for instance "
                        + serviceInstance.getInstanceId(), e);
                // If loading data causes an error, cache an empty result set for a short period of time
                data = new NwNmServiceInstanceData(new ArrayList<>(), new HashMap<>());
                cachedErrorInstanceData(data);
            }
        }

        DataFilter filter = MESSAGE_DETAILS_FILTER.lang(lang);
        Geometry geometry = StringUtils.isNotBlank(wkt)
                ? JtsConverter.wktToJts(wkt)
                : null;
        Map<String, List<Geometry>> messageGeometries = data.getGeometries();

                // Compute the filtered set of messages to return
        List<MessageVo> result = data.getMessages().stream()
                .filter(m -> filterByMainType(m, mainType))
                .filter(m -> filterByGeometry(messageGeometries.get(m.getId()), geometry))
                .map(m -> m.copy(filter))
                .collect(Collectors.toList());

        log.info(String.format("Search for language=%s, mainType=%s, wkt=%s -> returning %d messages",
                lang, mainType, wkt, result.size()));


        return result;

    }


    /** Returns the cached instance data **/
    private NwNmServiceInstanceData getCachedInstanceData() {
        return instanceMessageCache != null
                ? instanceMessageCache.get(serviceInstance.getInstanceId())
                : null;
    }


    /** Caches instance data for 3 minutes **/
    private void cachedInstanceData(NwNmServiceInstanceData data) {
        if (instanceMessageCache != null) {
            instanceMessageCache.put(
                    serviceInstance.getInstanceId(),
                    data,
                    ExpirationPolicy.CREATED,
                    3, TimeUnit.MINUTES);
        }
    }


    /** When an error has occurred, cache an empty result set for 1 minute **/
    private void cachedErrorInstanceData(NwNmServiceInstanceData data) {
        if (instanceMessageCache != null) {
            instanceMessageCache.put(
                    serviceInstance.getInstanceId(),
                    data,
                    ExpirationPolicy.CREATED,
                    1, TimeUnit.MINUTES);
        }
    }


    /**
     * Filters messages by their main type
     * @param message the message
     * @param mainType the valid main type. If mainType is not specified, every message is included
     * @return if the message is included by the filter
     */
    private boolean filterByMainType(MessageVo message, String mainType) {
        return StringUtils.isBlank(mainType) || mainType.equals(message.getMainType().name());
    }


    /**
     * Filters messages by their geometry boundary
     * @param messageGeometries cached message JTS geometries
     * @param geometry the JTS boundary that the message must be within
     * @return if the message is included by the filter
     */
    private boolean filterByGeometry(List<Geometry> messageGeometries, Geometry geometry) {
        if (geometry != null) {
            // NB: We include messages without any geometries
            if (messageGeometries != null) {
                return messageGeometries.stream()
                        .anyMatch(geometry::contains);
            }
        }
        return true;
    }


    /**
     * Fetches NW-NM messages from the service instance
     * @return the NW-NM messages from the service instance
     */
    private List<MessageVo> fetchNwNmMessages() throws Exception {
        long t0 = System.currentTimeMillis();

        // Create the URL
        String url = serviceInstance.getUrl();
        if (!url.endsWith("/"))  {
            url += "/";
        }
        url += NW_NM_API;

        HttpURLConnection con = httpURLConnectionFactory.newHttpUrlConnection(url);

        int status = con.getResponseCode();
        if (status == HttpURLConnection.HTTP_MOVED_TEMP
                || status == HttpURLConnection.HTTP_MOVED_PERM
                || status == HttpURLConnection.HTTP_SEE_OTHER) {

            // get redirect url from "location" header field
            String redirectUrl = con.getHeaderField("Location");

            // open the new connection again
            con = httpURLConnectionFactory.newHttpUrlConnection(redirectUrl);
        }

        try (InputStream is = con.getInputStream()) {

            String json = IOUtils.toString(is, "UTF-8");//Assuming UTF-8

            ObjectMapper mapper = new ObjectMapper();
            List<MessageVo> messages = mapper.readValue(json, new TypeReference<List<MessageVo>>(){});

            log.info(String.format(
                    "Loaded %d NW-NM messages in %s ms",
                    messages.size(),
                    System.currentTimeMillis() - t0));

            return messages;
        }
    }


    /**
     * Convert the message geometries to JTS geometries
     * This is a fairly expensive operation, so we only want to do it once and cache the result
     * @param messages the message to compute JTS geometries for
     * @return the Map from message IDs to JTS geometries
     */
    private Map<String, List<Geometry>> computeMessageGeometries(List<MessageVo> messages) {
        Map<String, List<Geometry>> geometries = new HashMap<>();
        messages.forEach(m -> {
            List<Geometry> messageGeometries = new ArrayList<>();
            if (m.getParts() != null) {
                m.getParts().stream()
                    .filter(p -> p.getGeometry() != null && p.getGeometry().getFeatures() != null)
                    .flatMap(p -> Arrays.stream(p.getGeometry().getFeatures()))
                    .filter(f -> f.getGeometry() != null)
                    .forEach(f -> {
                        try {
                            messageGeometries.add(JtsConverter.toJts(f.getGeometry()));
                        } catch (Exception ignored) {
                        }
                    });
            }
            if (!messageGeometries.isEmpty()) {
                geometries.put(m.getId(), messageGeometries);
            }
        });
        return geometries;
    }


    /** Buiilder class for the MessageLoaderTask class **/
    @Setter
    @Accessors(chain = true, fluent = true)
    public static class MessageLoaderTaskBuilder {

        private final EmbryoLogService embryoLogService;
        private final HttpURLConnectionFactory httpURLConnectionFactory;
        private ExpiringMap<String, NwNmServiceInstanceData> instanceMessageCache;
        private InstanceMetadata serviceInstance;
        private String mainType;
        private String lang;
        private String wkt;

        public MessageLoaderTaskBuilder(EmbryoLogService embryoLogService, HttpURLConnectionFactory httpURLConnectionFactory) {

            this.embryoLogService = embryoLogService;
            this.httpURLConnectionFactory = httpURLConnectionFactory;
        }

        /**
         * Constructs a new MessageLoaderTask instance
         * @return a new MessageLoaderTask instance
         */
        public MessageLoaderTask build() {
            // Validate data
            if (serviceInstance == null) {
                throw new IllegalArgumentException("Cannot construct a MessageLoaderTask without a serviceInstance");
            }

            return new MessageLoaderTask(embryoLogService, httpURLConnectionFactory, instanceMessageCache, serviceInstance, mainType, lang, wkt);
        }
    }
}
