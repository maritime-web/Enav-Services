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

package dk.dma.enav.services.vtsreport.service;
import dk.dma.embryo.common.configuration.PropertiesReader;
import dk.dma.embryo.common.configuration.PropertyFileService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Properties;
import javax.inject.Inject;
//
import org.junit.BeforeClass;

/**
 * Created by rob on 6/13/17.
 */
@Slf4j
public class VtsServiceTest {


    @Inject
    private PropertyFileService propertyFileService;

    @Test
    public void test() throws IOException, URISyntaxException {

        PropertiesReader reader = new PropertiesReader();
        Properties properties = reader.read();
//        log.info("Reading configuration from");

        // VERIFY: default-configuration.properties
        Assert.assertEquals("dma.vts.test@gmail.com", properties.get("vtsservice.emailaddress.by.vtsshortname.BELTREP"));
        Assert.assertEquals("false", properties.get("embryo.notification.mail.enabled"));
        Assert.assertEquals("localhost", properties.get("embryo.notification.mail.smtp.host"));
    }

}

