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
import dk.dma.enav.services.nwnm.api.MessagelistApi;
import dk.dma.enav.services.nwnm.model.Message;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

/**
 * Tests for the NW-NM maritime cloud test.
 */
public class NwNmServiceTest {

    private Logger log = LoggerFactory.getLogger(NwNmServiceTest.class);


    @Ignore
    @Test
    public void searchNwNmServiceTest() {

        MessagelistApi nwNmApi = new MessagelistApiBuilder()
                .basePath("https://niord.e-navigation.net/rest")
                .build();
        try {

            long t0 = System.currentTimeMillis();
            List<Message> messages = nwNmApi.search("en", null, null, Collections.singletonList("NW"), null);
            log.info("NW-NM search found " + messages.size() + " messages in "
                    + (System.currentTimeMillis() - t0) + " ms");

            new ObjectMapper().writerWithDefaultPrettyPrinter().writeValue(System.out, messages);

        } catch (Exception e) {
            log.error("Error fetching NW-NM messages from " + nwNmApi.getApiClient().getBasePath()
                    + ": " + e.getMessage(), e);
        }


    }

}
