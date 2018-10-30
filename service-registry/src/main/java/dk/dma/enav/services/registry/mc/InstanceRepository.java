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
package dk.dma.enav.services.registry.mc;

import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import dk.dma.embryo.common.configuration.Property;
import dk.dma.enav.services.registry.api.InstanceMetadata;
import dk.dma.enav.services.registry.api.NoServicesFoundException;
import dk.dma.enav.services.registry.mc.model.Instance;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Caches
 */
@SuppressWarnings("WeakerAccess")
@ApplicationScoped
public class InstanceRepository {
    private static final String ALL_CACHE_KEY = "ALL_CACHE_KEY";
    private ApiFactory apiFactory;
    private InstanceMapper mapper;
    private Cache<String, List<InstanceMetadata>> instanceCache;

    //Required by CDI
    @SuppressWarnings("unused")
    public InstanceRepository() {
    }

    @Inject
    public InstanceRepository(ApiFactory apiFactory,
                              InstanceMapper mapper,
                              @Property("enav-service.service-registry.cache.expire.time") int expireTime
    ) {
        this.apiFactory = apiFactory;
        this.mapper = mapper;
        instanceCache = CacheBuilder.newBuilder().expireAfterWrite(expireTime, TimeUnit.SECONDS).build();
    }

    public List<InstanceMetadata> getAllInstances() {
        try {
            return instanceCache.get(ALL_CACHE_KEY, this::getAllInstancesFromMcServiceRegistry);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * searches the service registry for a service or a wktLocation (boundry)
     * @param query Id of the service type
     * @param wktLocationFilter a wkt location that the service intersects with
     * @return a list of service that matched the given query
     */
    public List<InstanceMetadata> findServiceByWKTLocation(String query, String wktLocationFilter) {
        Preconditions.checkNotNull(query, "Query must be provided.");
        Preconditions.checkNotNull(wktLocationFilter, "WKT location must be provided.");

        // since you can query by organizationId, designId etc, we can't use the cache unless we want to run through all values
        List<Instance> instances = apiFactory.createInstanceResourceApi().searchByGeometry(query, wktLocationFilter);
        if (instances.isEmpty()) {
            throw new NoServicesFoundException();
        }

        return mapToInstanceMetadata(instances);
    }

    private List<InstanceMetadata> getAllInstancesFromMcServiceRegistry() {
        List<Instance> instances = apiFactory.createInstanceResourceApi().getAll();
        return mapToInstanceMetadata(instances);
    }

    private ArrayList<InstanceMetadata> mapToInstanceMetadata(List<Instance> instances) {
        return instances.stream().map(mapper::toMetaData).collect(Collectors.toCollection(ArrayList::new));
    }
}
