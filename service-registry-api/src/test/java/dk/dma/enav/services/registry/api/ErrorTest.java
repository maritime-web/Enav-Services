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
package dk.dma.enav.services.registry.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;


/**
 * Created by Steen on 31-05-2016.
 *
 */
public class ErrorTest {
    @Test
    public void shouldBeJsonSerializable() throws Exception {
        Error cut = new Error(ErrorId.MISSING_URL, ErrorType.INVALID_DATA, null);

        ObjectMapper mapper = new ObjectMapper();

        assertThat(mapper.canSerialize(Error.class), is(true));
        assertThat(mapper.writeValueAsString(cut), containsString("id"));
        assertThat(mapper.writeValueAsString(cut), containsString("errorType"));
        assertThat(mapper.writeValueAsString(cut), containsString("description"));
        assertThat(mapper.writeValueAsString(cut), containsString(ErrorId.MISSING_URL.toString()));
    }
}
