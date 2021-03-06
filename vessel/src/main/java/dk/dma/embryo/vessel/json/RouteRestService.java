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

import dk.dma.embryo.vessel.component.RouteDecorator;
import dk.dma.embryo.vessel.component.WaypointDecorator;
import dk.dma.embryo.vessel.service.ScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.annotations.GZIP;
import org.jboss.resteasy.annotations.cache.NoCache;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 
 * @author Jesper Tejlgaard
 */
@Path("/route")
@Slf4j
public class RouteRestService {

    @Inject
    private ScheduleService scheduleService;

    public RouteRestService() {
    }

    @GET
    @Path("/{id}")
    @Produces("application/json")
    @GZIP
    @NoCache
    public Route getRoute(@PathParam("id") String id) {
        log.debug("getRoute({})", id);
        
        dk.dma.embryo.vessel.model.Route route = scheduleService.getRouteByEnavId(id);
        
        Route result = null;
        if(route != null){
            Date departure = route.getVoyage().getDeparture() == null ? null : route.getVoyage().getDeparture().toDate();
            RouteDecorator decorator = new RouteDecorator(route.toEnavModel(), departure);

            result = new Route(route.getEnavId());
            result.setDes(route.getDestination());
            result.setEtaDep(departure);
            result.setDep(route.getOrigin());
            result.setEta(decorator.getEta());
            result.setName(route.getName());
            
            // Should be empty, however.
            result.getWps().clear();
            ArrayList<dk.dma.embryo.vessel.json.Waypoint> jsonWaypoints = new ArrayList<>();
            for (WaypointDecorator decoratorWayPoint : decorator.getWaypoints()) {
                dk.dma.embryo.vessel.json.Waypoint jsonWaypoint = new dk.dma.embryo.vessel.json.Waypoint(
                            decoratorWayPoint.getName(),
                            decoratorWayPoint.getLatitude(),
                            decoratorWayPoint.getLongitude(),
                            decoratorWayPoint.getRouteLeg().getSpeed(),
                            decoratorWayPoint.getRouteLeg().getHeading(),
                            decoratorWayPoint.getRot(),
                            decoratorWayPoint.getTurnRad(),
                            decoratorWayPoint.getEta()
                        );
                
                jsonWaypoints.add(jsonWaypoint);
            } 
            
            result.getWps().addAll(jsonWaypoints);
        }
        
        return result;
    }

    
    @GET
    @Path("/list/{ids}")
    @Produces("application/json")
    @GZIP
    @NoCache
    public List<Route> getRoutes(@PathParam("ids") String ids) {
        log.debug("getRoutes({})", ids);
        
        String[] idsArr = ids.split(":");
        List<Route> result = new ArrayList<>(idsArr.length);
        
        for(String id : idsArr){
            dk.dma.embryo.vessel.model.Route route = scheduleService.getRouteByEnavId(id);
            if(route != null){
                result.add(route.toJsonModel());
            }else{
                log.info("No route found for id: {}", id);
            }
        }

        log.debug("getRoutes({}) : {}", ids, result);
        return result;
    }

    @GET
    @Path("/active/meta/{mmsi}")
    @Produces("application/json")
    @GZIP
    @NoCache
    public Route getActiveMeta(@PathParam("mmsi") String mmsi) {
        log.debug("getActiveMeta({})", mmsi);

        Route result = null;
        dk.dma.embryo.vessel.model.Route route;

        route = scheduleService.getActiveRoute(Long.valueOf(mmsi));
        
        if(route != null){
            RouteDecorator decorator = new RouteDecorator(route.toEnavModel());
            Date departure = route.getVoyage().getDeparture() == null ? null : route.getVoyage().getDeparture().toDate();
            
            result = new Route(route.getEnavId());
            result.setDes(route.getDestination());
            result.setEtaDep(departure);
            result.setDep(route.getOrigin());
            result.setEta(decorator.getEta());
            result.getWps().clear();
        }

        log.debug("getActiveMeta({}) : {}", mmsi, result);
        return result;
    }

    @PUT
    @Path("/save")
    @Consumes("application/json")
    public void save(SaveRouteRequest request) {
        log.debug("save({})", request);

        dk.dma.embryo.vessel.model.Route toBeSaved = dk.dma.embryo.vessel.model.Route.fromJsonModel(request.getRoute());
        scheduleService.saveRoute(toBeSaved, request.getVoyageId(), false);
    }

    @PUT
    @Path("/save/activate")
    @Consumes("application/json")
    public void saveAndActivate(SaveRouteRequest request) {
        log.debug("saveAndActivate({})", request);

        dk.dma.embryo.vessel.model.Route toBeSaved = dk.dma.embryo.vessel.model.Route.fromJsonModel(request.getRoute());
        scheduleService.saveRoute(toBeSaved, request.getVoyageId(), true);
    }

    @PUT
    @Path("/activate")
    @Consumes("application/json")
    public void activate(ActiveRoute activeRoute) {
        log.debug("Activating route: {}", activeRoute);

        scheduleService.activateRoute(activeRoute.getRouteId(), activeRoute.getActive());
    }

}
