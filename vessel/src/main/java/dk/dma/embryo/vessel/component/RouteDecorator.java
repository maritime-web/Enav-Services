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


import java.util.ArrayList;
import java.util.Date;

import dk.dma.embryo.common.util.Converter;
import dk.dma.enav.model.geometry.Position;
import dk.dma.enav.model.voyage.Route;
import dk.dma.enav.model.voyage.RouteLeg.Heading;

public class RouteDecorator {

    private Route route;
    // todo this code is not done, looks like it was stoppe in themiddle of a feature
    private Long estimatedTotalTime;

    private ArrayList<WaypointDecorator> waypoints = new ArrayList<>();
    
    // //////////////////////////////////////////////////////////////////////
    // Constructors
    // //////////////////////////////////////////////////////////////////////

    public RouteDecorator(Route route, Date departure) {
        
        super();
        
        if(departure != null && route.getWaypoints() != null && route.getWaypoints().size() > 0) {
            route.getWaypoints().get(0).setEta(departure);
        }

        initData(route);
    }
    
    public RouteDecorator(Route route) {
        super();
        
        initData(route);
    }
    
    private void initData(Route route){
        
        this.route = route;

        WaypointDecorator lastWp = null;
        for(dk.dma.enav.model.voyage.Waypoint wp : route.getWaypoints()){
            WaypointDecorator waypoint = new WaypointDecorator(wp);
            waypoints.add(waypoint);
            if(lastWp != null){
                lastWp.getRouteLeg().setTo(waypoint);
            }
            lastWp = waypoint;
        }
        
        // Reuse reference
        lastWp = null;
        
        this.estimatedTotalTime = 0L;
        
        for(WaypointDecorator wp : waypoints){
            if(lastWp != null){
                if(lastWp.getEta() != null  && lastWp.getRouteLeg().getSpeed() != null && wp.getEta() == null){
                    long eta = lastWp.getEta().getTime() + lastWp.getRouteLeg().calcTtg();
                    wp.setEta(new Date(eta));
                    estimatedTotalTime += eta;
                }else{
                    
                }
                
            }
            lastWp = wp;
        }
        
    }

    // //////////////////////////////////////////////////////////////////////
    // Property methods
    // //////////////////////////////////////////////////////////////////////
    public String getDeparture() {
        return route.getDeparture();
    }

    public String getDestination() {
        return route.getDestination();
    }

    public Date getEta() {
        if(waypoints.size() == 0){
            return null;
        }
        return waypoints.get(waypoints.size() - 1).getEta();
    }

    public String getName() {
        return route.getName();
    }

    public void setDeparture(String departure) {
        route.setDeparture(departure);
    }

    public void setDestination(String destination) {
        route.setDestination(destination);
    }

    public void setName(String name) {
        route.setName(name);
    }

    public ArrayList<WaypointDecorator> getWaypoints() {
        return waypoints;
    }


}
