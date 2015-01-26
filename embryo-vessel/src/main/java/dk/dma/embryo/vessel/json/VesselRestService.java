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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.jboss.resteasy.annotations.GZIP;
import org.jboss.resteasy.annotations.cache.NoCache;
import org.slf4j.Logger;

import com.google.common.collect.Collections2;

import dk.dma.embryo.vessel.job.AisDataService;
import dk.dma.embryo.vessel.job.AisReplicatorJob;
import dk.dma.embryo.vessel.job.ShipTypeCargo.ShipType;
import dk.dma.embryo.vessel.job.ShipTypeMapper;
import dk.dma.embryo.vessel.job.filter.UserSelectionGroupsFilter;
import dk.dma.embryo.vessel.json.client.AisViewServiceAllAisData;
import dk.dma.embryo.vessel.json.client.AisViewServiceAllAisData.HistoricalTrack;
import dk.dma.embryo.vessel.model.Route;
import dk.dma.embryo.vessel.model.Vessel;
import dk.dma.embryo.vessel.model.Voyage;
import dk.dma.embryo.vessel.persistence.VesselDao;
import dk.dma.embryo.vessel.service.ScheduleService;
import dk.dma.embryo.vessel.service.VesselService;

@Path("/vessel")
@RequestScoped
public class VesselRestService {
    
    @Inject
    private AisViewServiceAllAisData historicalTrackAisViewService;
    
    @Inject
    private AisDataService aisDataService;

    @Inject
    private Logger logger;

    @Inject
    private VesselService vesselService;

    @Inject
    private ScheduleService scheduleService;

    @Inject
    private VesselDao vesselDao;

    @Inject
    private AisReplicatorJob aisReplicatorJob;

    @Inject
    private UserSelectionGroupsFilter userSelectionGroupsFilter;

    @GET
    @Path("/historical-track")
    @Produces("application/json")
    @GZIP
    @NoCache
    public List<HistoricalTrack> historicalTrack(@QueryParam("mmsi") long mmsi) {
        
        return historicalTrackAisViewService.historicalTrack(mmsi, 500, AisViewServiceAllAisData.LOOK_BACK_PT12H);
    }

    @GET
    @Path("/list")
    @Produces("application/json")
    @GZIP
    @NoCache
    public List<VesselOverview> list() {

        List<Vessel> allArcticWebVessels = vesselDao.getAll(Vessel.class);

        List<VesselOverview> result = new ArrayList<VesselOverview>();
        
        if (userHasAnyActiveSelectionGroups()) {
            
            List<VesselOverview> allAllowedAisVessels = this.mapAisVessels(aisDataService.getVesselsAllowed());
            
            // OBS: this filter removes the ArcticWeb vessels if they got a position outside the Selection Groups.
            result.addAll(Collections2.filter(allAllowedAisVessels, userSelectionGroupsFilter));
            
            for (Vessel vesselFromDatabase : allArcticWebVessels) {
                
                VesselOverview vesselToAdd = findVesselByMmsi(allAllowedAisVessels, vesselFromDatabase.getMmsi());
                
                setIsArcticWebFlagAndAddToResult(result, vesselFromDatabase, vesselToAdd);
            }
            
        } else /* Go default */ {
            
            result = this.mapAisVessels(aisDataService.getVesselsOnMap());
            
            for (Vessel vesselFromDatabase : allArcticWebVessels) {
                
                VesselOverview vesselToAdd = findVesselByMmsi(result, vesselFromDatabase.getMmsi());
                
                setIsArcticWebFlagAndAddToResult(result, vesselFromDatabase, vesselToAdd);
            }
        }
        
        return result;
    }

    private void setIsArcticWebFlagAndAddToResult(List<VesselOverview> result, Vessel vesselFromDatabase, VesselOverview aisVesselOverview) {
       
        if (bothArcticWebAndAisVessel(aisVesselOverview)) {
            
            aisVesselOverview.setInAW(true);
            
            // Important check otherwise the result will either contain duplicates or miss some vessels.
            if(userHasAnyActiveSelectionGroups()) {
                result.add(aisVesselOverview);
            }
        } else /* only database vessel then create new DTO and add to list */ {
            
            result.add(createVesselOverview(vesselFromDatabase));
        }
    }

    private boolean userHasAnyActiveSelectionGroups() {
        return this.userSelectionGroupsFilter.loggedOnUserHasSelectionGroups();
    }

    private VesselOverview findVesselByMmsi(List<VesselOverview> allAllowedAisVesselsAsDTO, Long mmsi) {
       
        for (VesselOverview vesselOverview : allAllowedAisVesselsAsDTO) {
            if(vesselOverview.getMmsi().longValue() == mmsi.longValue()) {
                return vesselOverview;
            }
        }
        
        return null;
    }

    private VesselOverview createVesselOverview(Vessel vesselFromDatabase) {
        VesselOverview arcticWebVesselOnly = new VesselOverview();
        arcticWebVesselOnly.setInAW(true);
        arcticWebVesselOnly.setCallSign(vesselFromDatabase.getAisData().getCallsign());
        arcticWebVesselOnly.setName(vesselFromDatabase.getAisData().getName());
        arcticWebVesselOnly.setMmsi(vesselFromDatabase.getMmsi());
        return arcticWebVesselOnly;
    }

    private boolean bothArcticWebAndAisVessel(VesselOverview vesselOverview) {
        return vesselOverview != null;
    }

    private List<VesselOverview> mapAisVessels(List<AisViewServiceAllAisData.Vessel> vessels) {

        List<VesselOverview> vesselOverviewsResponse = new ArrayList<VesselOverview>();

        for (AisViewServiceAllAisData.Vessel vessel : vessels) {

            VesselOverview vesselOverview = new VesselOverview();
            Long mmsi = vessel.getMmsi();

            vesselOverview.setX(vessel.getLon());
            vesselOverview.setY(vessel.getLat());
            vesselOverview.setAngle(vessel.getCog() != null ? vessel.getCog() : 0);
            vesselOverview.setMmsi(mmsi);
            vesselOverview.setName(vessel.getName());
            vesselOverview.setCallSign(vessel.getCallsign());
            vesselOverview.setMoored(vessel.getMoored() != null ? vessel.getMoored() : false);

            ShipType shipTypeFromSubType = ShipType.getShipTypeFromSubType(vessel.getVesselType());
            String type = ShipTypeMapper.getInstance().getColor(shipTypeFromSubType).ordinal() + "";
            vesselOverview.setType(type);

            vesselOverview.setInAW(false);
            vesselOverview.setMsog(vessel.getMaxSpeed());
            
            vesselOverviewsResponse.add(vesselOverview);
        }

        return vesselOverviewsResponse;
    }

    @GET
    @Path("/details")
    @Produces("application/json")
    @GZIP
    @NoCache
    @Details
    public VesselDetails details(@QueryParam("mmsi") long mmsi) {
        logger.debug("details({})", mmsi);

        try {

            logger.info("details method called with MMSI -> " + mmsi);
            dk.dma.embryo.vessel.json.client.AisViewServiceAllAisData.Vessel aisVessel = this.aisDataService.getAisVesselByMmsi(mmsi);

            boolean historicalTrack = false;
            Double lat = null;
            Double lon = null;

            if (aisVessel != null) {
                lat = aisVessel.getLat();
                lon = aisVessel.getLon();
            }

            if (lat != null && lon != null) {
                historicalTrack = aisDataService.isAllowed(lat);
            }

            VesselDetails details;
            Vessel vessel = vesselService.getVessel(mmsi);
            List<Voyage> schedule = null;
            Route route = null;

            if (vessel != null) {
                route = scheduleService.getActiveRoute(mmsi);
                details = vessel.toJsonModel();
                schedule = scheduleService.getSchedule(mmsi);
                details.setAisVessel(aisVessel);
            } else {
                details = new VesselDetails();
                details.setAisVessel(aisVessel);
            }

            Map<String, Object> additionalInformation = new HashMap<>();
            additionalInformation.put("historicalTrack", historicalTrack);
            additionalInformation.put("routeId", route != null ? route.getEnavId() : null);
            additionalInformation.put("schedule", schedule != null && schedule.size() > 0);

            details.setAdditionalInformation(additionalInformation);

            return details;

        } catch (Throwable t) {

            logger.info("Ignoring exception " + t, t);

            // fallback on database only

            Vessel vessel = vesselService.getVessel(mmsi);

            if (vessel != null) {
                VesselDetails details;
                Route route = scheduleService.getActiveRoute(mmsi);
                List<Voyage> schedule = scheduleService.getSchedule(mmsi);

                details = vessel.toJsonModel();

                AisViewServiceAllAisData.Vessel aisVessel = new AisViewServiceAllAisData.Vessel();
                aisVessel.setCallsign(vessel.getAisData().getCallsign());
                aisVessel.setImoNo(vessel.getAisData().getImoNo());
                aisVessel.setMmsi(vessel.getMmsi());
                aisVessel.setName(vessel.getAisData().getName());

                Map<String, Object> additionalInformation = new HashMap<>();
                additionalInformation.put("routeId", route != null ? route.getEnavId() : null);
                additionalInformation.put("historicalTrack", false);
                additionalInformation.put("schedule", schedule != null && schedule.size() > 0);
                details.setAdditionalInformation(additionalInformation);

                return details;
            } else {
                throw new RuntimeException("No vessel details available for " + mmsi + " caused by " + t);
            }
        }
    }

    @POST
    @Path("/save-details")
    @Consumes("application/json")
    @GZIP
    public void saveDetails(VesselDetails details) {
        logger.debug("save({})", details);
        vesselService.save(Vessel.fromJsonModel(details));
    }

    @PUT
    @Path("/update/ais")
    @Consumes("application/json")
    public void updateAis() {
        logger.debug("updateAis()");
        aisReplicatorJob.replicate();
    }
}
