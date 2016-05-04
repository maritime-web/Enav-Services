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
package dk.dma.enav.services.registry;

import ietf.lost1.ExceptionContainer;
import ietf.lost1.FindServiceResponse;
import ietf.lost1.ListServicesByLocationResponse;
import ietf.lost1.LocationInformation;
import ietf.lost1.Mapping;
import org.slf4j.Logger;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Created by Steen on 28-04-2016.
 *
 */
public class LostResponseParser {

    @Inject
    private Logger logger;

    public List<String> getServiceIds(String listServiceResponse) {
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

    public ServiceInstanceMetadata parseFindServiceResponse(String findServiceResponse) {
        JAXBContext jaxbContext;
        try {
            jaxbContext = JAXBContext.newInstance(FindServiceResponse.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            Object rawResponse = unmarshaller.unmarshal(new InputSource(new StringReader(findServiceResponse)));

            FindServiceResponse response;
            if (rawResponse instanceof FindServiceResponse) {
                response = (FindServiceResponse) rawResponse;
            } else {
                throw new RuntimeException("Unrecognized response type\n" + findServiceResponse);
            }
            Mapping m = chooseMapping(response.getMapping());
            if (m == null) {
                return null;
            }
            String name = m.getDisplayName().size() > 0 ? m.getDisplayName().get(0).getValue() : "Has no name";

            return new ServiceInstanceMetadata(m.getService(), m.getService(), name, getBoundary(m), m.getUri().get(0));

        } catch (JAXBException e) {
            throw new RuntimeException("");
        }
    }

    private String getBoundary(Mapping m) {
        String res;
        LocationInformation locationInformation = m.getServiceBoundary().get(0);

        Element element = locationInformation.getExtensionPoint().get(0);
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(element), new StreamResult(writer));
            res = writer.toString();
        } catch (TransformerException e) {
            throw new RuntimeException();
        }
        return res;
    }

    private Mapping chooseMapping(List<Mapping> mapping) {
        for (Mapping m : mapping) {
            for (String uri : m.getUri()) {
                if (isValid(uri)) {
                    return m;
                }
            }
        }
        return null;
    }

    private boolean isValid(String uri) {
        try {
            URL url = new URL(uri);
            return  url.getHost() != null && !url.getHost().isEmpty();
        } catch (MalformedURLException e) {
            return false;
        }
    }
}
