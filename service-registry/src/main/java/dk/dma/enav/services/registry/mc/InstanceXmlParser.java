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

import dk.dma.enav.services.registry.mc.model.Xml;
import org.efficiensea2.maritimecloud.serviceregistry.v1.ObjectFactory;
import org.efficiensea2.maritimecloud.serviceregistry.v1.ServiceInstance;
import org.xml.sax.InputSource;

import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 *
 */
public class InstanceXmlParser {
    private final Base64Decoder decoder;
    private JAXBContext jaxbContext;

    @Inject
    public InstanceXmlParser(Base64Decoder decoder) {
        this.decoder = decoder;
    }

    ServiceInstance parseInstanceXml(Xml xml) {
        byte[] decodedInstance = decoder.decode(xml);
        return unmarshal(decodedInstance, ServiceInstance.class);
    }

    private InputSource createInputSource(byte[] xmlBytes) {
        return new InputSource(new InputStreamReader(new ByteArrayInputStream(xmlBytes), StandardCharsets.UTF_8));
    }

    private <E> E unmarshal(byte[] xmlBytes, Class<E> expectedType) {
        try {
            Unmarshaller unmarshaller = getUnmarshaller();
            Object unmarshalledResponse = unmarshaller.unmarshal(createInputSource(xmlBytes));

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
            throw new RuntimeException("Unable to unmarshal:\n" + new String(xmlBytes), e);
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
