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

import ietf.lost1.ObjectFactory;
import org.xml.sax.InputSource;

import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;

/**
 * Created by Steen on 18-05-2016.
 *
 */
class JaxbAdapter {
    private JAXBContext jaxbContext;

    String marshal(Object jaxbElement) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            Marshaller jaxbMarshaller = getMarshaller();
            jaxbMarshaller.marshal(jaxbElement, os);

            return os.toString("UTF-8");
        } catch (JAXBException | IOException e) {
            throw new RuntimeException("Unable to marshal \""+ jaxbElement.getClass().getName()+"\"", e);
        }
    }

    <E> E unmarshal(String xmlString, Class<E> expectedType) {
        try {
            Unmarshaller unmarshaller = getUnmarshaller();
            Object unmarshalledResponse = unmarshaller.unmarshal(new InputSource(new StringReader(xmlString)));

            if (expectedType.isInstance(unmarshalledResponse)) {
                //noinspection unchecked
                return (E) unmarshalledResponse;
            } else {
                throw new LostErrorResponseException(unmarshalledResponse);
            }
        } catch (JAXBException e) {
            throw new RuntimeException("Unable to unmarshal:\n" + xmlString, e);
        }
    }

    private Unmarshaller getUnmarshaller() throws JAXBException {
        return getContext().createUnmarshaller();
    }

    private Marshaller getMarshaller() throws JAXBException {
        Marshaller jaxbMarshaller = getContext().createMarshaller();
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        return jaxbMarshaller;
    }

    private JAXBContext getContext() {
        if (jaxbContext == null) {
            String contextPath = "ietf.lost1";
            try {
                jaxbContext = JAXBContext.newInstance(contextPath, ObjectFactory.class.getClassLoader());
            } catch (JAXBException e) {
                throw new RuntimeException("Unable to create JAXBContext for context path \""+contextPath+"\"", e);
            }
        }

        return jaxbContext;
    }

}
