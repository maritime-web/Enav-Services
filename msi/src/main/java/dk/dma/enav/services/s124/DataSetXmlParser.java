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
package dk.dma.enav.services.s124;

import _int.iho.s124.gml.cs0._0.DatasetType;
import _int.iho.s124.gml.cs0._0.ObjectFactory;
import org.xml.sax.InputSource;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;

/**
 *
 */
public class DataSetXmlParser {
    private JAXBContext jaxbContext;

    public DatasetType parseDataSetXml(String xml) {
        return unmarshal(xml);
    }

    private InputSource createInputSource(String xml) {
        return new InputSource(new StringReader(xml));
    }

    private DatasetType unmarshal(String xml) {
        try {
            Unmarshaller unmarshaller = getUnmarshaller();
            return (DatasetType) ((JAXBElement)unmarshaller.unmarshal(createInputSource(xml))).getValue();

        } catch (JAXBException e) {
            throw new RuntimeException("Unable to unmarshal:\n" + xml, e);
        }
    }

    private Unmarshaller getUnmarshaller() throws JAXBException {
        return getContext().createUnmarshaller();
    }

    private JAXBContext getContext() {
        if (jaxbContext == null) {
            String contextPath = "_int.iho.s124.gml.cs0._0";
            try {
                jaxbContext = JAXBContext.newInstance(contextPath, ObjectFactory.class.getClassLoader());
            } catch (JAXBException e) {
                throw new RuntimeException("Unable to create JAXBContext for context path \"" + contextPath + "\"", e);
            }
        }

        return jaxbContext;
    }

}
