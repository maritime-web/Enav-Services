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
package dk.dma.embryo.metoc.json.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;

public interface DmiSejlRuteService {
    SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.000Z");

    @POST()
    @Path("/SR")
    SejlRuteResponse sejlRute(@FormParam("req") SejlRuteRequest request);

    @Getter
    @Setter
    class ForecastValue {
        private double forecast;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    @Setter
    class Forecast {
        private double lat;
        private double lon;
        private String time;
        
        @JsonProperty("wind-dir")
        private ForecastValue windDir;
        @JsonProperty("wind-speed")
        private ForecastValue windSpeed;
        @JsonProperty("current-dir")
        private ForecastValue currentDir;
        @JsonProperty("current-speed")
        private ForecastValue currentSpeed;
        @JsonProperty("wave-dir")
        private ForecastValue waveDir;
        @JsonProperty("wave-height")
        private ForecastValue waveHeight;
        @JsonProperty("wave-period")
        private ForecastValue wavePeriod;
        @JsonProperty("sealevel")
        private ForecastValue sealevel;
    }

    @Getter
    @Setter
    class MetocForecast {
        private String created;
        private Forecast[] forecasts;
    }

    /**
     * The following combinations of values have been reported from DMI to be possible:
     * <ul>
     * <li>OK(0, "All ok")</li>
     * <li>WAYPOINTS(1, "Too few waypoints")</li>
     * <li>PARAM(2, "Invalid parameter")</li>
     * <li>DIRECTION(3, "Invalid direction")</li>
     * <li>DATE(5, "Missing or invalid dates")</li>
     * <li>PATH(6, "Invalid path")</li>
     * <li>DATE_INVALID(7, "Invalid dates")</li>
     * <li>DATATYPES(8, "Missing or invalid datatypes")</li>
     * <li>UNKNOWN(9, "Unknown error - sorry!")</li>
     * <li>DELTAT(10, "Problem with delta-T (dt)")</li>
     * </ul>
     */
    @Getter
    @Setter
    class SejlRuteResponse {
        private int error;
        private String errorMsg;
        private MetocForecast metocForecast;

        public String toString() {
            ObjectMapper mapper = new ObjectMapper();
            try {
                return mapper.writeValueAsString(this);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Getter
    @Setter
    @EqualsAndHashCode
    class Waypoint {
        private String eta;
        private String heading;
        private double lat;
        private double lon;
    }

    @Getter
    @Setter
    @EqualsAndHashCode
    class SejlRuteRequest {
        private long mssi;
        private String[] datatypes;
        private int dt;
        private Waypoint[] waypoints;

        public String toString() {
            return MetocJsonClientFactory.asJson(this);
        }
    }
    
    
}
