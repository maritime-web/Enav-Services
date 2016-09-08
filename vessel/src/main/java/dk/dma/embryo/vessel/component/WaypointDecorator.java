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
package dk.dma.embryo.vessel.component;

import java.util.Date;

// //////////////////////////////////////////////////////////////////////
// Inner classes
// //////////////////////////////////////////////////////////////////////
public class WaypointDecorator {
    private dk.dma.enav.model.voyage.Waypoint waypoint;
    private RouteLegDecorator routeLeg;

    WaypointDecorator(dk.dma.enav.model.voyage.Waypoint wp) {
        this.routeLeg = new RouteLegDecorator(wp.getRouteLeg(), this);
        this.waypoint = wp;
    }


    // //////////////////////////////////////////////////////////////////////
    // Inner class Property methods
    // //////////////////////////////////////////////////////////////////////
    public Date getEta() {
        return waypoint.getEta();
    }

    public double getLatitude() {
        return waypoint.getLatitude();
    }

    public double getLongitude() {
        return waypoint.getLongitude();
    }

    public String getName() {
        return waypoint.getName();
    }

    public Double getRot() {
        return waypoint.getRot();
    }

    public RouteLegDecorator getRouteLeg() {
        return routeLeg;
    }

    public Double getTurnRad() {
        return waypoint.getTurnRad();
    }

    public void setEta(Date eta) {
        waypoint.setEta(eta);
    }

    public void setLatitude(double latitude) {
        waypoint.setLatitude(latitude);
    }

    public void setLongitude(double longitude) {
        waypoint.setLongitude(longitude);
    }

    public void setName(String name) {
        waypoint.setName(name);
    }

    public void setRot(Double rot) {
        waypoint.setRot(rot);
    }

//        public void setRouteLeg(RouteLeg routeLeg) {
//            waypoint.setRouteLeg(routeLeg);
//        }

    public void setTurnRad(Double turnRad) {
        waypoint.setTurnRad(turnRad);
    }
}
