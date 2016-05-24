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
import ietf.lost1.ListServicesByLocationResponse;
import org.slf4j.Logger;
import org.xml.sax.InputSource;

import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.util.List;

/**
 * Created by Steen on 18-05-2016.
 *
 */
class ListServicesByLocationResponseParser {
    @Inject
    private Logger logger;

    List<String> getServiceIds(String listServiceResponse) {
        JAXBContext jaxbContext;
        try {
            jaxbContext = JAXBContext.newInstance(ListServicesByLocationResponse.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            Object response = unmarshaller.unmarshal(new InputSource(new StringReader(listServiceResponse)));
            if (response instanceof ListServicesByLocationResponse) {
                return ((ListServicesByLocationResponse)response).getServiceList();
            } else if (response instanceof ExceptionContainer) {
                throw new RuntimeException("Server returned errror\n" + listServiceResponse);
            } else {
                throw new RuntimeException("Unrecognized response type\n" + listServiceResponse);
            }
        } catch (JAXBException e) {
            throw new RuntimeException("", e);
        }
    }
}
