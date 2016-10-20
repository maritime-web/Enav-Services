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

import dk.dma.enav.services.registry.mc.ApiClient;
import dk.dma.enav.services.registry.mc.ApiException;
import dk.dma.enav.services.registry.mc.ApiResponse;
import dk.dma.enav.services.registry.mc.model.Instance;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
class InstanceIterator implements Iterator<List<Instance>> {
    private static final Pattern LINK_PATTERN = Pattern.compile(".*page=(?<page>\\d+).*size=(?<size>\\d+)");
    private ServiceinstanceresourceApi api;
    private Optional<Link> next;

    InstanceIterator(ApiClient apiClient, int pageSize) {
        api = new ServiceinstanceresourceApi(apiClient);
        next = Optional.of(new Link(0, pageSize));
    }

    @Override
    public boolean hasNext() {
        return next.isPresent();
    }

    @Override
    public List<Instance> next() {
        Link link = next.orElseThrow(() -> new RuntimeException(""));
        ApiResponse<List<Instance>> response = call(link.page, link.size);
        next = createNextLink(getLinkHeader(response));
        return response.getData();
    }

    private ApiResponse<List<Instance>> call(int pageNumber, int pageSize) {
        try {
            return api.getAllInstancesUsingGETWithHttpInfo(pageNumber, pageSize, null);
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
}
