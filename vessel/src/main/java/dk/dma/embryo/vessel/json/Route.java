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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.ToString;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import dk.dma.enav.model.MaritimeId;
@ToString
// todo Setters and getter do not have standard names, maybe we should rename them and add Json serialization information
public class Route {

    private final List<Waypoint> waypoints = new ArrayList<>();
    private String name;
    private String destination;
    private String departure;
    private Date etaDep;
    private Date etaDes;

    /** Should this be implemented as a {@link MaritimeId} ? */
    private String id;

    // //////////////////////////////////////////////////////////////////////
    // Constructors
    // //////////////////////////////////////////////////////////////////////
    public Route() {
    }

    public Route(String id) {
        this.id = id;
    }

    // //////////////////////////////////////////////////////////////////////
    // Property methods
    // //////////////////////////////////////////////////////////////////////
    public String getDep() {
        return departure;
    }

    public String getDes() {
        return destination;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public List<Waypoint> getWps() {
        return waypoints;
    }

    public void setDep(String departure) {
        this.departure = departure;
    }

    public void setDes(String destination) {
        this.destination = destination;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getEtaDep() {
        return etaDep;
    }

    public void setEtaDep(Date eta) {
        this.etaDep = eta;
    }

    public Date getEta() {
        return etaDes;
    }

    public void setEta(Date eta) {
        this.etaDes = eta;
    }

}
