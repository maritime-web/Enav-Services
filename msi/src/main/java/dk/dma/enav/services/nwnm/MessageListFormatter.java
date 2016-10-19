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
