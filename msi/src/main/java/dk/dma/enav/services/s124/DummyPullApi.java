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
package dk.dma.enav.services.s124;

import dk.dma.enav.services.s124.api.PullApi;
import dk.dma.enav.services.s124.model.GetMessageResponseObject;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.UUID;

//TODO temporary implementation. Delete when real s-124 service is up and running
public class DummyPullApi extends PullApi {
    @Override
    public GetMessageResponseObject s124Get(Integer id, Integer status, String wkt) throws ApiException {
        GetMessageResponseObject res = new GetMessageResponseObject();
        res.setMessageId(UUID.randomUUID());
        res.setTimestamp(OffsetDateTime.now());

        String dataSet;
        try {
            InputStream resource = getClass().getClassLoader().getResourceAsStream("S124-test-dummy.xml");
            InputStreamReader reader = new InputStreamReader(resource, StandardCharsets.UTF_8);
            dataSet = IOUtils.toString(reader);

        } catch (IOException e) {
            e.printStackTrace();
            throw new ApiException(e);
        }

        ArrayList<String> messages = new ArrayList<>();
        messages.add(dataSet);


        res.setMessages(messages);
        return res;
    }
}
