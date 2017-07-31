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

import dk.dma.embryo.common.configuration.PropertyFileService;
import dk.dma.enav.services.registry.api.InstanceMetadata;
import dk.dma.enav.services.registry.api.TechnicalDesignId;
import org.jglue.cdiunit.AdditionalClasses;
import org.jglue.cdiunit.CdiRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.inject.Inject;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.BufferedReader;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

@RunWith(CdiRunner.class)
@AdditionalClasses(value = {PropertyFileService.class})
public class StaticServiceRegistryTest {
    @Inject
    private StaticServiceRegistry cut;

    @Test
    public void shouldUseInjectedPropertyAsUrlInInstanceMetaData() throws Exception {
        List<InstanceMetadata> instanceMetadata = cut.getServiceInstances("");

        assertThat(instanceMetadata.get(0).getUrl(), is(not(nullValue())));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldNotAcceptDesignIdAndWkt() throws Exception {
        cut.getServiceInstances(new TechnicalDesignId("", ""), "");
    }

    @Test
    public void name() throws Exception {
        String xpathExp = "//div[@class='scrollable']//tbody/tr/td[2]/text()";
        BufferedReader reader = com.google.common.io.Files.newReader(new File("C:\\dev\\projekter\\Enav-Services\\prod_emails.xml"), Charset.forName("UTF-8"));
        XPath xPath = XPathFactory.newInstance().newXPath();
        NodeList res = (NodeList) xPath.evaluate(xpathExp, new InputSource(reader), XPathConstants.NODESET);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < res.getLength(); i++) {
            sb.append(res.item(i).getNodeValue()).append(",");
        }
        System.out.println(sb);
    }

    @Test
    public void aaa() throws Exception {
/*
        double a = 3601/499364868000D;
        double b = 30390001/249682434000D;
        double c = 1858368270063083D/1857916052584778D;
*/
        double a = 3601/998729736000D;
        double b = 30390001/499364868000D;
        double c = 6690306659218421D/14863328420678224D;

        System.out.println("a: " + a);
        System.out.println("b: " + b);
        System.out.println("c: " + c);

        double[] resolutions = {2, 100, 500, 1000, 2000, 3000, 4000, 9000, 9800};

        for (double resolution : resolutions) {
            System.out.println("resolution: " + resolution + " => " + (a*resolution*resolution - b*resolution + c));
        }
    }
}
