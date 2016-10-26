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

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKTReader;
import dk.dma.enav.services.registry.mc.model.Xml;
import org.efficiensea2.maritimecloud.serviceregistry.v1.CoverageArea;
import org.efficiensea2.maritimecloud.serviceregistry.v1.ServiceInstance;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 *
 */
public class InstanceXmlParserTest {

    @Test
    public void shouldValidateInstanceAgainstSchema() throws Exception {
        URL schemaUrl = InstanceXmlParserTest.class.getResource("/mc/ServiceInstanceSchema.xsd");

        Schema schema = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(schemaUrl);

        URL instanceUrl = InstanceXmlParserTest.class.getResource("/mc/instanceWithEmptyGeometryAsWKTTag.xml");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            schema.newValidator().validate(new StreamSource(new FileReader(new File(instanceUrl.toURI()))), new StreamResult(baos));
        } catch (SAXException e) {
            fail("Schema validation failed with message '"+e.getMessage()+"'");
        }
    }

    @Test
    public void shouldSetDefaultCoverageWhenXmlHasEmptyGeometryAsWKTTag() throws Exception {
        InstanceXmlParser cut = new InstanceXmlParser(new Base64Decoder());
        Xml xml = getXmlForFile("/mc/instanceWithEmptyGeometryAsWKTTag.xml");

        ServiceInstance serviceInstance = cut.parseInstanceXml(xml);

        CoverageArea coverageArea = serviceInstance.getCoversAreas().getCoversArea().get(0);

        assertThat(coverageArea.getGeometryAsWKT(), is(defaultGeometry()));
    }

    @Test
    public void shouldNotSetDefaultCoverageWhenXmlHasGeometryAsWKTTag() throws Exception {
        InstanceXmlParser cut = new InstanceXmlParser(new Base64Decoder());
        Xml xml = getXmlForFile("/mc/instanceWithNonEmptyGeometryAsWKTTag.xml");

        ServiceInstance serviceInstance = cut.parseInstanceXml(xml);

        CoverageArea coverageArea = serviceInstance.getCoversAreas().getCoversArea().get(0);
        assertThat(coverageArea.getGeometryAsWKT(), is(not(defaultGeometry())));
    }

    private Xml getXmlForFile(String path) throws URISyntaxException, IOException {
        URL instanceUrl = InstanceXmlParserTest.class.getResource(path);
        byte[] bytes = Files.readAllBytes(Paths.get(instanceUrl.toURI()));
        Xml xml = new Xml();
        xml.setContentContentType("application/xml");
        xml.setContent(Base64.getEncoder().encodeToString(bytes));

        return xml;
    }

    private String defaultGeometry() throws NoSuchFieldException {
        Field field = CoverageArea.class.getDeclaredField("geometryAsWKT");
        XmlElement annotation = field.getAnnotation(XmlElement.class);
        return annotation.defaultValue();
    }
}
