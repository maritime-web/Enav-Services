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
package dk.dma.enav.services.registry.mc.api;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Lists;
import dk.dma.enav.services.registry.mc.ApiClient;
import dk.dma.enav.services.registry.mc.ApiException;
import dk.dma.enav.services.registry.mc.ApiResponse;
import dk.dma.enav.services.registry.mc.model.Instance;

/**
 * An iterator for looping through instances returned by a service.
 * Pagination information is encoded in the link header, and must be read from there
 */
class InstanceIterator implements Iterator<List<Instance>> {

    public final static DataLoader ALL_DATA_LOADER =  new DataLoader() {
        @Override
        public ApiResponse<List<Instance>> fetchData(ServiceinstanceresourceApi api, int pageNumber, int pageSize) throws ApiException {
            return api.getAllInstancesUsingGETWithHttpInfo(pageNumber, pageSize, null, null, Lists.newArrayList());
        }
    };

    public static DataLoader createSearchDataLoader(String query, String wktLocation) {
        return new DataLoader() {
            @Override
            public ApiResponse<List<Instance>> fetchData(ServiceinstanceresourceApi api, int pageNumber, int pageSize) throws ApiException {
                return api.searchInstancesByGeometryWKTUsingGETWithHttpInfo(wktLocation, query, pageNumber, pageSize, null, null, Lists.newArrayList());
            }
        };
    };

    private static final Pattern LINK_PATTERN = Pattern.compile(".*page=(?<page>\\d+).*size=(?<size>\\d+)");
    private final ServiceinstanceresourceApi api;
    private final DataLoader dataLoader;
    private Optional<Link> next;

    public InstanceIterator(ApiClient apiClient, int pageSize) {
        this(apiClient, pageSize, ALL_DATA_LOADER);
    }

    public InstanceIterator(ApiClient apiClient, int pageSize, DataLoader dataLoader) {
        api = new ServiceinstanceresourceApi(apiClient);
        next = Optional.of(new Link(0, pageSize));
        this.dataLoader = dataLoader;

    }

    @Override
    public boolean hasNext() {
        return next.isPresent();
    }

    @Override
    public List<Instance> next() {
        Link link = next.get();
        ApiResponse<List<Instance>> response = call(link.page, link.size);
        next = createNextLink(getLinkHeader(response));
        return response.getData();
    }

    private ApiResponse<List<Instance>> call(int pageNumber, int pageSize) {
        try {
            return dataLoader.fetchData(api, pageNumber, pageSize);
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
    }

    private Optional<Link> createNextLink(String linkHeader) {
        return Arrays.stream(linkHeader.split(","))
                .filter(s -> s.contains("rel=\"next\""))
                .map(this::linkFromString)
                .findFirst();
    }

    private String getLinkHeader(ApiResponse<List<Instance>> response) {
        List<String> links = response.getHeaders().get("Link");
        if (links != null && links.size() > 0) {
            return links.get(0);
        }
        return "";
    }

    private Link linkFromString(String s) {
        //[</api/serviceInstance?page=1&size=3>; rel="next"]
        Matcher matcher = LINK_PATTERN.matcher(s);
        if (!matcher.find()) {
            throw new RuntimeException("Unable to create link from '" + s + "'");
        }
        String page = matcher.group("page");
        String size = matcher.group("size");
        return new Link(Integer.valueOf(page), Integer.valueOf(size));
    }

    private class Link {
        int page;
        int size;

        Link(int page, int size) {
            this.page = page;
            this.size = size;
        }
    }
    public interface DataLoader {
        ApiResponse<List<Instance>> fetchData(ServiceinstanceresourceApi api, int pageNumber, int pageSize) throws ApiException;
    }
}
