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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import dk.dma.embryo.vessel.integration.AisVessel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import java.util.Map;

/**
 * 
 * @author Jesper Tejlgaard
 */
@JsonIgnoreProperties(ignoreUnknown=true)
@ToString
@Getter
@Setter
@EqualsAndHashCode
public class VesselDetails {
    
    /** Ship name */
    private Long mmsi;

    private String maritimeId;

    /** Communication capabilities */
    private String commCapabilities;

    private Float maxSpeed;

    /** Gross tonnage */
    private Integer grossTon;

    /** Maximum Capacity for persons on board */
    private Integer maxPersons;

    private String iceClass;

    private Boolean helipad;

    private Map<String, Object> additionalInformation;
    
    private AisVessel aisVessel;
    
    private VesselOverview overview;

    // //////////////////////////////////////////////////////////////////////
    // Constructors
    // //////////////////////////////////////////////////////////////////////
    public VesselDetails() {
        super();
    }

    // //////////////////////////////////////////////////////////////////////
    // Business Logic
    // //////////////////////////////////////////////////////////////////////

    public Long getMmsiNumber() {
        if (getMmsi() != null) {
            return getMmsi();
        }

        if (getAisVessel() == null) {
            return null;
        }

        return getAisVessel().getMmsi();
    }
}
