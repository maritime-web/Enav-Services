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
package dk.dma.enav.services.nwnm;

import dk.dma.embryo.common.log.EmbryoLogService;
import dk.dma.enav.services.nwnm.MessageLoaderTask.MessageLoaderTaskBuilder;
import dk.dma.enav.services.registry.api.InstanceMetadata;
import org.apache.http.conn.ClientConnectionManager;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test loading NW-NM messages
 */
public class NwNmTest {

    /** Test message loading **/
    @Ignore
    @Test
    public void messageLoaderTest() throws Exception {
        EmbryoLogService embryoLogService = Mockito.mock(EmbryoLogService.class);

        // Check the redirect works
        String url = "https://niord.e-navigation.net/rest/";

        NwNmConnectionManager connectionManager =
                new NwNmConnectionManager();

        try {
            InstanceMetadata instance = new InstanceMetadata("test-nwnm", "1.0", 1)
                    .setUrl(url);

            MessageLoaderTask task =
                    new MessageLoaderTaskBuilder(embryoLogService, connectionManager)
                            .serviceInstance(instance)
                            .mainType("NW")
                            .lang("en")
                            .wkt("POLYGON((7 54, 7 57, 13 56, 13 57, 7 54))")
                            .build();

            System.out.println("URL " + url + " returned " + task.call().size() + " messages");
        } catch (Exception e) {
            connectionManager.shutdown();
            e.printStackTrace();
        }

    }

}
