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

import dk.dma.enav.services.registry.ErrorDescription;
import dk.dma.enav.services.registry.Errors;
import ietf.lost1.BasicException;
import ietf.lost1.ExceptionContainer;
import ietf.lost1.LocationProfileUnrecognized;

import javax.inject.Inject;
import javax.xml.bind.JAXBElement;
import java.util.List;
import java.util.stream.Collectors;

import static dk.dma.enav.services.registry.lost.ErrorType.LOCATION_PROFILE_UNRECOGNIZED;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

/**
 * Created by Steen on 23-05-2016.
 *
 */
class ErrorConverter {

    @Inject
    public ErrorConverter() {
    }

    List<Errors> convert(List<ExceptionContainer> errors) {
        return errors.stream().map(this::convert).collect(Collectors.toList());
    }

    Errors convert(Object unmarshalledResponse) {
        if (unmarshalledResponse instanceof JAXBElement) {
            return convert((JAXBElement)unmarshalledResponse);
        } else if (unmarshalledResponse instanceof LocationProfileUnrecognized) {
            return convert((LocationProfileUnrecognized)unmarshalledResponse);
        } else {
            throw new RuntimeException("Unrecognized element: "+ unmarshalledResponse.getClass().getName());
        }
    }

    private Errors convert(JAXBElement errorElement) {
        Object value = errorElement.getValue();
        if (ExceptionContainer.class.isInstance(value)) {
            return convert((ExceptionContainer)value);
        } else {
            return new Errors("Unknown source", createErrorDescription(errorElement));
        }
    }

    private Errors convert(ExceptionContainer e) {
        return new Errors(e.getSource(), createDescriptions(e));
    }

    private List<ErrorDescription> createDescriptions(ExceptionContainer e) {
        return e.getBadRequestOrInternalErrorOrServiceSubstitution().stream().map(this::createErrorDescription).collect(toList());
    }

    private ErrorDescription createErrorDescription(Object o) {
        ErrorDescription res = null;
        if (LocationProfileUnrecognized.class.isInstance(o)) {
            LocationProfileUnrecognized desc = (LocationProfileUnrecognized) o;
            res = new ErrorDescription(LOCATION_PROFILE_UNRECOGNIZED.getName(), desc.getMessage(), desc.getUnsupportedProfiles().stream().collect(joining(", ")));
        } else if (JAXBElement.class.isInstance(o)) {
            String errorName = ((JAXBElement)o).getName().getLocalPart();
            BasicException desc = (BasicException)  ((JAXBElement)o).getValue();
            res = new ErrorDescription(errorName, desc.getMessage(), null);
        }
        return res;
    }

    private Errors convert(LocationProfileUnrecognized rawResponse) {
        return new Errors("Unknown source", createErrorDescription(rawResponse));
    }

}
