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

import dk.dma.embryo.common.configuration.LogConfiguration;
import org.jglue.cdiunit.AdditionalClasses;
import org.jglue.cdiunit.CdiRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.net.URL;
import java.util.List;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

/**
 * Created by Steen on 04-05-2016.
 *
 */
@RunWith(CdiRunner.class)
@AdditionalClasses(value = {LogConfiguration.class})
public class LostServiceIT {

    @Inject
    private Logger logger;

    @Inject
    private LostService cut;

    @Test
    public void shouldFindAtleastOneService() throws Exception {
        List<ServiceInstanceMetadata> services = cut.findAllServices(55D, 11D);

        assertThat(services, is(not(empty())));

        System.out.println(services);
    }

    @Test
    public void testName() throws Exception {
        String u = "http:niord.e-navigation.net/rest/public/v1/messages";
        URL url = new URL(u);
        System.out.println("Host: '" + url.getHost() + "'");

    }
}