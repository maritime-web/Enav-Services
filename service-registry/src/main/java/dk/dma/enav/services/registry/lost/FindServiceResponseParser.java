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
package dk.dma.enav.services.registry.lost;

import dk.dma.enav.services.registry.ServiceInstanceMetadata;
import ietf.lost1.DisplayName;
import ietf.lost1.ExceptionContainer;
import ietf.lost1.FindServiceResponse;
import ietf.lost1.LocationInformation;
import ietf.lost1.Mapping;
import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.Logger;
import org.w3c.dom.Element;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by Steen on 28-04-2016.
 *
 */
class FindServiceResponseParser {
    private static final Mapping EMPTY_MAPPING = new Mapping();
    private final LostUnmarshalAdapter lostUnmarshalAdapter;
    private final DomUtil domUtil;
    private final ErrorConverter errorConverter;
    private final Logger logger;

    @SuppressWarnings("CdiInjectionPointsInspection")
    @Inject
    FindServiceResponseParser(LostUnmarshalAdapter lostUnmarshalAdapter, DomUtil domUtil, ErrorConverter errorConverter, Logger logger) {
        this.lostUnmarshalAdapter = lostUnmarshalAdapter;
        this.domUtil = domUtil;
        this.errorConverter = errorConverter;
        this.logger = logger;
    }

    ServiceInstanceMetadata parseFindServiceResponse(String findServiceResponse) {
        FindServiceResponse response = lostUnmarshalAdapter.unmarshal(findServiceResponse, FindServiceResponse.class);
        Mapping m = chooseMapping(response.getMapping()).orElse(EMPTY_MAPPING);

        return convert(m, response);
    }

    private Optional<Mapping> chooseMapping(List<Mapping> mapping) {
        return mapping.stream().filter(this::isValid).findFirst();
    }

    private boolean isValid(Mapping m) {
        boolean res = true;
        String service = m.getService();
        String source = m.getSource();
        String sourceId = m.getSourceId();

        if (!containsValidUri(m.getUri())) {
            logger.warn("Invalid mapping for service: {}. No valid uri's for mapping identified by source: {} and sourceId: {}. Uri's found {}", service, source, sourceId, m.getUri().stream().collect(Collectors.joining(", ")));
            res = false;
        }

        return res;
    }

    private boolean containsValidUri(List<String> uris) {
        return uris.stream().anyMatch(this::isValid);
    }

    private boolean isValid(String uri) {
        return UrlValidator.getInstance().isValid(uri);
    }

    private ServiceInstanceMetadata convert(Mapping m, FindServiceResponse response) {
        String name = m.getDisplayName().stream().filter(d -> "en".equals(d.getLang())).findFirst().orElse(new DisplayName()).getValue();

        ServiceInstanceMetadata res = new ServiceInstanceMetadata(m.getService(), m.getService(), name, getBoundary(m), getFirstValidUri(m.getUri()));
        res.setWarnings(errorConverter.convert(response.getWarnings()));
        return res;
    }

    private String getBoundary(Mapping m) {
        Optional<LocationInformation> locationInformation = m.getServiceBoundary().stream().findFirst();

        if (locationInformation.isPresent()) {
            Element element = locationInformation.get().getExtensionPoint().get(0);
            return domUtil.toString(element);
        }

        return null;
    }

    private String getFirstValidUri(List<String> uris) {
        //noinspection OptionalGetWithoutIsPresent
        return uris.stream().filter(this::isValid).findFirst().orElse(null);
    }
}
