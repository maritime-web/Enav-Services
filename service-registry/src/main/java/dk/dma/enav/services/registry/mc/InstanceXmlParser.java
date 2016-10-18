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
import org.xml.sax.InputSource;

import javax.inject.Inject;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Created by Steen on 17-10-2016.
 *
 */
public class InstanceXmlParser {
    private final Base64Decoder decoder;

    @Inject
    public InstanceXmlParser(Base64Decoder decoder) {
        this.decoder = decoder;
    }

    InstanceDetails parseInstanceXml(Xml xml) {
        InstanceDetails res;
        try {
            byte[] decodedInstance = decoder.decode(xml);
            System.out.println(new String(decodedInstance, StandardCharsets.UTF_8));
            XPath xPath = XPathFactory.newInstance().newXPath();
            xPath.setNamespaceContext(ServiceRegisterNamespaceContext.getInstance());
            String url = xPath.evaluate("//s:serviceInstance/URL", createInputSource(decodedInstance));
            String coverage = xPath.evaluate("//s:serviceInstance/coversAreas/coversArea/geometryAsWKT", createInputSource(decodedInstance));
            String designId = xPath.evaluate("//s:serviceInstance/implementsServiceDesign/id", createInputSource(decodedInstance));
            String designVersion = xPath.evaluate("//s:serviceInstance/implementsServiceDesign/version", createInputSource(decodedInstance));
            res = new InstanceDetails(url)
                    .withCoverage(coverage)
                    .withDesignId(designId)
                    .withDesignVersion(designVersion);
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        }
        return res;
    }

    private InputSource createInputSource(byte[] xmlBytes) {
        return new InputSource(new InputStreamReader(new ByteArrayInputStream(xmlBytes), StandardCharsets.UTF_8));
    }
}
