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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.joda.time.DateTime;

import java.io.IOException;

/**
 * Creates a custom ObjectMapper that formats joda TimeStamps as Epoch timestamps.
 * This is used to handle serialization of the Swagger-generated NW-NM classes.
 * <p>
 * NB: The currently used version of Jackson (Wildfly 8) is too old to register a JodaModule :-(
 * <p>
 * Also, we do not dare to register a @Provider that changes RestEasy behaviour globally.
 */
public class MessageListFormatter {

    /**
     * Creates a custom ObjectMapper that formats joda TimeStamps as Epoch timestamps.
     * @return a custom ObjectMapper that formats joda TimeStamps as Epoch timestamps.
     */
    public static ObjectMapper createMessageObjectMappper() {
        SimpleModule module = new SimpleModule();
        module.addSerializer(DateTime.class, new EpochDateTimeSerializer());

        return new ObjectMapper()
                .registerModule(module)
                .setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
    }

    /**
     * Serializes the joda date time as Epoch timestamps
     */
    public static class EpochDateTimeSerializer extends JsonSerializer<DateTime> {

        @Override
        public void serialize(DateTime value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
            if (value == null) {
                jgen.writeNull();
            } else {
                jgen.writeNumber(value.getMillis());
            }
        }
    }

}
