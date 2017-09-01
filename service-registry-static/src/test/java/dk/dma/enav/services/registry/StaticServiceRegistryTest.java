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

}
