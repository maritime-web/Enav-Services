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

import ietf.lost1.ExceptionContainer;

import javax.xml.bind.JAXBElement;
import java.util.List;

/**
 * Created by Steen on 30-05-2016.
 *
 */
class ErrorAnalyzer {

    boolean isNoServiceFoundError(Object unmarshalledResponse) {
        if (unmarshalledResponse instanceof JAXBElement) {
            return isNoServiceFoundError((JAXBElement) unmarshalledResponse);
        }
        return false;
    }

    boolean isNoServiceFoundError(JAXBElement unmarshalledResponse) {
        Object errorElement = unmarshalledResponse.getValue();
        if (errorElement instanceof ExceptionContainer) {
            return containsNoServiceFoundError((ExceptionContainer) errorElement);
        }
        return false;
    }

    private boolean containsNoServiceFoundError(ExceptionContainer errorElement) {
        List<Object> errors = errorElement.getBadRequestOrInternalErrorOrServiceSubstitution();

        return false;
    }
}
