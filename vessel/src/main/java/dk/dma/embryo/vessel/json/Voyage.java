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

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;

/**
 * 
 * @author Jesper Tejlgaard
 */
@Data
public class Voyage {

    // Properties relevant for current functionality. Extra can be added.
    private String maritimeId;
    private String location;
    private Double latitude;
    private Double longitude;
    private Date arrival;
    private Date departure;
    private Integer crew;
    private Integer passengers;
    private Boolean doctor;
    private RouteOverview route;

    // //////////////////////////////////////////////////////////////////////
    // Constructors
    // //////////////////////////////////////////////////////////////////////
    public Voyage() {
        super();
    }

    public Voyage(
            String maritimeId, 
            String location, 
            Double latitude, 
            Double longitude, 
            Date arrival, 
            Date departure, 
            Integer crew,
            Integer passengers) {
        
        super();
        this.maritimeId = maritimeId;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.arrival = arrival;
        this.departure = departure;
        this.crew = crew;
        this.passengers = passengers;
    }

    public Voyage(
            String maritimeId, 
            String location, 
            Double latitude, 
            Double longitude, 
            Date arrival, 
            Date departure, 
            Integer crew,
            Integer passengers, 
            Boolean doctor) {
        
        super();
        this.maritimeId = maritimeId;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.arrival = arrival;
        this.departure = departure;
        this.crew = crew;
        this.passengers = passengers;
        this.doctor = doctor;
    }

    @AllArgsConstructor
    @EqualsAndHashCode
    @Getter
    public static class RouteOverview {
        private String id;
        private String name;
        private String dep;
        private String des;
        private Integer waypointCount;
    }
}
