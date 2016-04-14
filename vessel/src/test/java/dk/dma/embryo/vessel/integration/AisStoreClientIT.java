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
package dk.dma.embryo.vessel.integration;


import dk.dma.embryo.common.configuration.LogConfiguration;
import dk.dma.embryo.common.configuration.Property;
import dk.dma.embryo.common.configuration.PropertyFileService;
import dk.dma.embryo.vessel.integration.AisStoreClient.TrackPosition;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jglue.cdiunit.AdditionalClasses;
import org.jglue.cdiunit.CdiRunner;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.ws.rs.ext.RuntimeDelegate;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;

/**
 * @author Jesper Tejlgaard
 */
@RunWith(CdiRunner.class)
@AdditionalClasses(value = {AisClientFactory.class, PropertyFileService.class, LogConfiguration.class})
public class AisStoreClientIT {

    @Inject
    private AisStoreClient aisStoreClient;

    @Inject
    @Property("embryo.aisstore.server.url")
    private String aisStoreUrl;

    @Inject
    @Property("embryo.aisstore.server.user")
    private String aisStoreUser;

    @BeforeClass
    public static void setUpClass() throws Exception {
        RuntimeDelegate.setInstance(new ResteasyProviderFactory());
    }

    @Test
    public void testPastTrack() {
        List<TrackPosition> trackPositions = this.aisStoreClient.pastTrack(220443000L, "s.region!=802,808", "PT48H");

        assertThat(trackPositions, is(not(empty())));
    }
}
