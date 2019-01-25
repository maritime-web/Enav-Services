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

import com.fasterxml.jackson.databind.ObjectMapper;
import dk.dma.embryo.common.log.EmbryoLogService;
import dk.dma.enav.services.nwnm.NwNmMessageLoaderTask.MessageLoaderTaskBuilder;
import dk.dma.enav.services.registry.api.InstanceMetadata;
import dk.dma.enav.services.registry.api.VendorInfo;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.niord.model.message.MessageVo;

import java.util.List;

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
        String url = "https://niord.dma.dk/rest/";

        NwNmConnectionManager connectionManager =
                new NwNmConnectionManager();

        try {
            InstanceMetadata instance = new InstanceMetadata("NWNM", "1.0", 1L);
            instance
                    .setDescription("Arctic specific service registry providing access to the NW-NM service")
                    .setName("NWNM Service Endpoint")
                    .setProducedBy(new VendorInfo("DMA"))
                    .setProvidedBy(new VendorInfo("DMA"))
                    .setUrl(url);

            NwNmMessageLoaderTask task =
                    new MessageLoaderTaskBuilder(embryoLogService, connectionManager)
                            .serviceInstance(instance)
                            .mainType("NW")
                            .lang("en")
                            .wkt("POLYGON((7 54, 7 57, 13 57, 13 54, 7 54))")
                            .build();

            List<MessageVo> voList = task.call();
            ObjectMapper mapper = new ObjectMapper();
            System.out.println(mapper.writeValueAsString(voList));
            System.out.println("URL " + url + " returned " + voList.size() + " messages");
        } catch (Exception e) {
            connectionManager.shutdown();
            e.printStackTrace();
        }

    }

}
