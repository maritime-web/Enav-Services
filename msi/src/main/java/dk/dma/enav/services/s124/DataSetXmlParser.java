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

import org.geotools.xml.Parser;
import org.opengis.feature.simple.SimpleFeature;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 *
 */
public class DataSetXmlParser {

    public SimpleFeature parseDataSetXml(String xml) {
        return unmarshal(xml);
    }

    private InputSource createInputSource(String xml) {
        ByteArrayInputStream bis = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
        return new InputSource(bis);
    }

    private SimpleFeature unmarshal(String xml) {
        org.geotools.xml.Configuration config = new S124Configuration(S124XSD.getInstance());

        Parser parser = new Parser( config );
        parser.setStrict(true);


        try {
            return (SimpleFeature) parser.parse(createInputSource(xml));
        } catch (IOException | SAXException | ParserConfigurationException e) {
            throw new RuntimeException("Unable to unmarshal:\n" + xml, e);
        }

    }
}
