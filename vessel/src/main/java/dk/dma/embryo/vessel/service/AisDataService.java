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
package dk.dma.embryo.vessel.service;

import dk.dma.embryo.vessel.integration.AisVessel;
import dk.dma.embryo.vessel.json.TrackPos;

import java.util.List;

public interface AisDataService {
   
    List<AisVessel> getAisVessels();

   // List<AisVessel> getAisVesselsBBOX(double ne_lat, double ne_lot, double sw_lat, double sw_lot );
    List<AisVessel> getAisVesselsBBOX(String specificAreaFilter);

    AisVessel getAisVesselByMmsi(Long mmsi);

    List<AisVessel> getAisVesselsByMmsi(List<Long> mmsiNumbers);

    boolean isHistoricalTrackAllowed(AisVessel vessel);

    List<TrackPos> historicalTrack(Long mmsi);



}
