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

import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.google.common.base.Strings;
import dk.dma.enav.services.registry.mc.model.Xml;
import org.efficiensea2.maritimecloud.serviceregistry.v1.ObjectFactory;
import org.efficiensea2.maritimecloud.serviceregistry.v1.ServiceInstance;
import org.xml.sax.InputSource;

/**
 *
 */
public class InstanceXmlParser {
    private JAXBContext jaxbContext;

    ServiceInstance parseInstanceXml(Xml xml) {
        return unmarshal(xml, ServiceInstance.class);
    }

    private InputSource createInputSource(Xml xml) {
        return new InputSource(new StringReader(getContext(xml)));
    }

    private String getContext(Xml xml) {
        String content = xml.getContent();
        if (Strings.isNullOrEmpty(content)) {
            throw new IllegalArgumentException("No XML context: " + xml);
        }
        return content;
    }

    private <E> E unmarshal(Xml xml, Class<E> expectedType) {
        try {
            Unmarshaller unmarshaller = getUnmarshaller();
            Object unmarshalledResponse = unmarshaller.unmarshal(createInputSource(xml));

            if (expectedType.isInstance(unmarshalledResponse)) {
                //noinspection unchecked
                return (E) unmarshalledResponse;
            } else if (unmarshalledResponse instanceof JAXBElement && ((JAXBElement) unmarshalledResponse).getDeclaredType().equals(expectedType)) {
                //noinspection unchecked
                return (E) ((JAXBElement) unmarshalledResponse).getValue();
            } else {
                throw new RuntimeException("unexpected unmarshal class: " + unmarshalledResponse.getClass().getName() + " expected: " + expectedType.getName());
            }
        } catch (JAXBException e) {
            throw new RuntimeException("Unable to unmarshal:\n" + xml, e);
        }
    }

    private Unmarshaller getUnmarshaller() throws JAXBException {
        return getContext().createUnmarshaller();
    }

    private JAXBContext getContext() {
        if (jaxbContext == null) {
            String contextPath = "org.efficiensea2.maritimecloud.serviceregistry.v1";
            try {
                jaxbContext = JAXBContext.newInstance(contextPath, ObjectFactory.class.getClassLoader());
            } catch (JAXBException e) {
                throw new RuntimeException("Unable to create JAXBContext for context path \"" + contextPath + "\"", e);
            }
        }

        return jaxbContext;
    }
}
