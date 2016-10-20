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

import org.junit.Ignore;
import org.junit.Test;

import java.net.URLEncoder;

/**
 * Test loading NW-NM messages
 */
public class NwNmTest {

    /** Test message loading **/
    @Ignore
    @Test
    public void messageLoaderTest() throws Exception {

        // Check the redirect works
        String url = "http://niord.e-navigation.net/rest/"
                + NwNmRestService.NW_NM_API
                + "?lang=en&mainType=NW&wkt=" + URLEncoder.encode("POLYGON((7 54, 7 57, 13 56, 13 57, 7 54))", "UTF-8");

        try {
            MessageLoaderTask task = new MessageLoaderTask(url);
            System.out.println("URL " + url + " returned " + task.call().size() + " messages");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
