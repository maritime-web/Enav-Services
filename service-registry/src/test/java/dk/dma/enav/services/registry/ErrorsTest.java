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

import com.fasterxml.jackson.databind.ObjectMapper;
import dk.dma.enav.services.registry.api.ErrorDescription;
import dk.dma.enav.services.registry.api.Errors;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;


/**
 * Created by Steen on 31-05-2016.
 *
 */
public class ErrorsTest {
    @Test
    public void shouldBeJsonSerializable() throws Exception {
        Errors cut = new Errors("AnId", (ErrorDescription) null);

        ObjectMapper mapper = new ObjectMapper();

        assertThat(mapper.canSerialize(Errors.class), is(true));
        assertThat(mapper.writeValueAsString(cut), containsString("serviceRegistryId"));
        assertThat(mapper.writeValueAsString(cut), containsString("descriptions"));
        assertThat(mapper.writeValueAsString(cut), containsString("AnId"));
    }
}
