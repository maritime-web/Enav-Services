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

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import dk.dma.embryo.common.configuration.Property;
import dk.dma.enav.services.registry.api.InstanceMetadata;
import dk.dma.enav.services.registry.mc.model.Instance;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 *
 */
public class InstanceRepository {
    private final ApiFactory apiFactory;
    private final InstanceMapper mapper;
    private final long expireTime;
    private Cache<InstanceMetadata, InstanceMetadata> instanceCache;

    @Inject
    public InstanceRepository(ApiFactory apiFactory,
                              InstanceMapper mapper,
                              @Property("enav-service.service-registry.cache.expire.time") int expireTime
    ) {
        this.apiFactory = apiFactory;
        this.mapper = mapper;
        this.expireTime = expireTime;
    }

    List<InstanceMetadata> getAllInstances() {
        List<InstanceMetadata> res = getAllInstancesFromCache();
        if (res.isEmpty()) {
            res = getAllInstancesFromMcServiceRegistry();
            updateCache(res);
        }
        return res;
    }

    private List<InstanceMetadata> getAllInstancesFromCache() {
        return new ArrayList<>(getCache().asMap().values());
    }

    private Cache<InstanceMetadata, InstanceMetadata> getCache() {
        if (instanceCache == null) {
            instanceCache = CacheBuilder.newBuilder().expireAfterWrite(expireTime, TimeUnit.SECONDS).build();
        }
        return instanceCache;
    }

    private List<InstanceMetadata> getAllInstancesFromMcServiceRegistry() {
        List<Instance> instances = apiFactory.createInstanceResourceApi().getAll();
        return instances.stream().map(mapper::toMetaData).collect(Collectors.toList());
    }

    private void updateCache(List<InstanceMetadata> instances) {
        Map<InstanceMetadata, InstanceMetadata> data = new HashMap<>();
        instances.forEach(instance -> data.put(instance, instance));
        getCache().putAll(data);
    }

}
