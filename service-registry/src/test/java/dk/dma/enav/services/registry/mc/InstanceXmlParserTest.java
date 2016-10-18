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

import org.junit.Test;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.FileReader;
import java.net.URL;

/**
 *
 */
public class InstanceXmlParserTest {

    @Test
    public void name() throws Exception {
        URL schemaUrl = InstanceXmlParserTest.class.getResource("/mc/ServiceInstanceSchema.xsd");
        System.out.println(schemaUrl);

        Schema schema = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(schemaUrl);

        URL instanceUrl = InstanceXmlParserTest.class.getResource("/mc/instance.xml");
        System.out.println(instanceUrl);
        schema.newValidator().validate(new StreamSource(new FileReader(new File(instanceUrl.toURI()))), new StreamResult(System.out));
    }
}
