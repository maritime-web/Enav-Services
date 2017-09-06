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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.xml.XMLConstants;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import dk.dma.enav.services.registry.mc.model.Xml;
import org.efficiensea2.maritimecloud.serviceregistry.v1.CoverageArea;
import org.efficiensea2.maritimecloud.serviceregistry.v1.ServiceInstance;
import org.junit.Ignore;
import org.junit.Test;
import org.xml.sax.SAXException;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 *
 */
public class InstanceXmlParserTest {

    @Test
    @Ignore("We can't load xsd from the classpath, because they have filesystem relative references to imported xsd, if we use a file reference we still can't validate" +
            "because we can't define elements defined in the included ServiceBaseTypesSchema.xsd, and we don't know why.")
    public void shouldValidateInstanceAgainstSchema() throws Exception {
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

        Schema schema = schemaFactory.newSchema(new File("/Users/kg/work/Enav-services/service-registry/src/test/resources/mc/ServiceInstanceSchema.xsd"));
        URL instanceUrl = InstanceXmlParserTest.class.getResource("/mc/instanceWithEmptyGeometryAsWKTTag.xml");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            schema.newValidator().validate(new StreamSource(new FileReader(new File(instanceUrl.toURI()))), new StreamResult(baos));
        } catch (SAXException e) {
            fail("Schema validation failed with message '" + e.getMessage() + "'");
        }
    }

    @Test
    @Ignore("This tests fails because Jaxb default value substitution only works if the field is missing or nil, not if the field is empty (classical empty vs null problem with xml).")
    public void shouldSetDefaultCoverageWhenXmlHasEmptyGeometryAsWKTTag() throws Exception {
        checkDefaultGeomety(getXmlForFile("/mc/instanceWithEmptyGeometryAsWKTTag.xml"));
    }

    @Test
    public void shouldNotSetDefaultCoverageWhenXmlHasGeometryAsWKTTag() throws Exception {
        checkDefaultGeomety(getXmlForFile("/mc/instanceWithNonEmptyGeometryAsWKTTag.xml"));
    }

    private void checkDefaultGeomety( Xml xml) throws NoSuchFieldException {
        InstanceXmlParser parser = new InstanceXmlParser();
        ServiceInstance serviceInstance = parser.parseInstanceXml(xml);

        CoverageArea coverageArea = serviceInstance.getCoversAreas().getCoversArea().get(0);
        assertThat(coverageArea.getGeometryAsWKT(), is(not(defaultGeometry())));
    }

    private Xml getXmlForFile(String path) throws URISyntaxException, IOException {
        URL instanceUrl = InstanceXmlParserTest.class.getResource(path);
        byte[] bytes = Files.readAllBytes(Paths.get(instanceUrl.toURI()));
        Xml xml = new Xml();
        xml.setContentContentType("application/xml");
        xml.setContent(new String(bytes, StandardCharsets.UTF_8));

        return xml;
    }

    private String defaultGeometry() throws NoSuchFieldException {
        Field field = CoverageArea.class.getDeclaredField("geometryAsWKT");
        XmlElement annotation = field.getAnnotation(XmlElement.class);
        return annotation.defaultValue();
    }
}
