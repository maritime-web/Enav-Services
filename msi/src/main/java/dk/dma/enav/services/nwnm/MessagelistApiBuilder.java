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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import dk.dma.enav.services.nwnm.api.MessagelistApi;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.io.IOException;
import java.util.Date;

/**
 * Add some customizations to the standard Swagger-generated API.
 * <p>
 * Specifically, we override the date-time handling to use Unix Epoch timestamps
 */
public class MessagelistApiBuilder {

    private String basePath;

    /**
     * Sets the base path for the NW-NM API
     * @param basePath the base path for the NW-NM API
     * @return this builder
     */
    public MessagelistApiBuilder basePath(String basePath) {
        this.basePath = basePath;
        return this;
    }


    /**
     * Creates an instance of the API, adjusted to properly process Niord NW-NM data
     * @return the NW-NM API
     */
    public MessagelistApi build() {

        ApiClient apiClient = new ApiClient();
        MessagelistApi nwNmApi;

        if (StringUtils.isNotBlank(basePath)) {
            apiClient.setBasePath(basePath);
            nwNmApi = new MessagelistApi(apiClient);
        } else {
            nwNmApi = new MessagelistApi();
        }

        // Override the date-time handling to use Unix Epoch timestamps
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, new DateAdapter(apiClient))
                .registerTypeAdapter(DateTime.class, new EpochDateTimeTypeAdapter())
                .registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter())
                .create();

        nwNmApi.getApiClient().getJSON().setGson(gson);

        return nwNmApi;
    }


    /**
     * Process data time as Unix Epoch timestamps
     */
    public static class EpochDateTimeTypeAdapter extends TypeAdapter<DateTime> {

        @Override
        public void write(JsonWriter out, DateTime date) throws IOException {
            if (date == null) {
                out.nullValue();
            } else {
                out.value(date.getMillis());
            }
        }

        @Override
        public DateTime read(JsonReader in) throws IOException {
            switch (in.peek()) {
                case NULL:
                    in.nextNull();
                    return null;
                default:
                    String date = in.nextString();
                    Long epoch = Long.valueOf(date);
                    return new DateTime(epoch);
            }
        }
    }
}
