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
package dk.dma.embryo.vessel.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
public class VesselOverview {

    private Double angle;
    private Double x;
    private Double y;
    private String name;
    private String type;
    private long mmsi;
    private String callSign;
    private boolean moored;
    private boolean inAW;

    /**
     * Only one of "sog", "ssog" or "awsog" is set at a time.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Double sog;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Double ssog;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Double awsog;
}
