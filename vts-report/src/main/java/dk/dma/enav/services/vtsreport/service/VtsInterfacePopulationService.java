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
package dk.dma.enav.services.vtsreport.service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


/**
 * Created by rob on 6/1/17.
 * Temporarily services the BalticWeb frontend questionaire interface with JS objects - possibly redundant when the
 * Swedish Maritime Authority launch their VTS service which should produce somewhat the same output.
 */

@Path("/vtsinterface")
public class VtsInterfacePopulationService {


    private static String TimeStamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss-yyyy/MM/dd");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(new Date());
    }
    private String timestamp = TimeStamp();


    @GET
    @Produces("application/json")
    public String getMessage() {
        int counter = 0;
        String VtsJsObjects = "["; //start as array

        VtsJsObjects += "{\"id\": "+(counter++)+",";
        VtsJsObjects += "\"shortname\": \"BELTREP\",";
        VtsJsObjects += "\"name\": \"Denmark - BELTREP - The Great Belt Vessel Traffic Service\",";
        VtsJsObjects += "\"areaWKT\":\"\",";
        VtsJsObjects += "\"callsign\":\"Great Belt Traffic\",";
        VtsJsObjects += "\"email\":\"vts@beltrep.org\",";
        VtsJsObjects += "\"telephone\":\"+45 58 37 68 68\",";
        VtsJsObjects += "\"telephone2\":\"\",";
        VtsJsObjects += "\"fax\":\"\",";
        VtsJsObjects += "\"vhfchannel1\":\"North 74\",";
        VtsJsObjects += "\"vhfchannel2\":\"South 11\",";
        VtsJsObjects += "\"vhfchannel3\":\"\",";
        VtsJsObjects += "\"vhfchannel4\":\"\",";
        VtsJsObjects += "\"vhfreservechannel1\":\"11\",";
        VtsJsObjects += "\"vhfreservechannel2\":\"\",";
        VtsJsObjects += "\"iconImage\":\"img/OpenPortGuideLogo_32.png\",";
        VtsJsObjects += "\"VTSGuideLink\":\"http://forsvaret.dk/VTSSTB/eng/Documents/BELTREP%20Information%20ver%200.pdf\",";
        VtsJsObjects += "\"showMaxDraught\":false,";
        VtsJsObjects += "\"showAirDraught\":true,";
        VtsJsObjects += "\"showFuelQuantity\":false,";
        VtsJsObjects += "\"showFuelDetails\":true,";
        VtsJsObjects += "\"showVesselType\":false,";
        VtsJsObjects += "\"showVesselLength\":false,";
        VtsJsObjects += "\"showDeadWeightTonnage\":true,";
        VtsJsObjects += "\"showGrossTonnage\":false,";
        VtsJsObjects += "\"showTrueCourse\":true,";
        VtsJsObjects += "\"showVesselCurrentPosition\":true,";
        VtsJsObjects += "\"deadWeightTonnageLimit\":1000,";//TODO: at which tonnage to display fueldetails - uses deadWeightTonnageMultiplier with cargoTypes
        VtsJsObjects += "\"sendSummaryTo\":\"email\"}";//TODO: sends summary as email to the registered email, can also send as JSON to service in future version, ex: "https://beltrep.org/services/VTSservice"

        VtsJsObjects += ","; //next item

        VtsJsObjects += "{\"id\": "+(counter++)+",";
        VtsJsObjects += "\"shortname\": \"SOUNDREP\",";
        VtsJsObjects += "\"name\": \"Sweden - SOUNDREP - Sound Vessel Traffic Service\",";
        VtsJsObjects += "\"areaWKT\":\"POLYGON((12.18333 56.11611, 12.18333 56.23333, 12.29417 56.30222, 12.45778 56.30222,12.8 56.13333, 13.13333 55.68333, 13.0425 55.39139, 12.91389 55.16667,12.45778 55.16667,12.45778 55.29556, 12.59806 55.55778))\",";
        VtsJsObjects += "\"callsign\":\"Sound VTS\",";
        VtsJsObjects += "\"email\":\"contact@soundvts.org\",";
        VtsJsObjects += "\"telephone\":\"+46 771-630600\",";
        VtsJsObjects += "\"telephone2\":\"\",";
        VtsJsObjects += "\"fax\":\"\",";
        VtsJsObjects += "\"vhfchannel1\":\"North 73\",";
        VtsJsObjects += "\"vhfchannel2\":\"South 71\",";
        VtsJsObjects += "\"vhfchannel3\":\"\",";
        VtsJsObjects += "\"vhfchannel4\":\"\",";
        VtsJsObjects += "\"vhfreservechannel1\":\"68\",";
        VtsJsObjects += "\"vhfreservechannel2\":\"79\",";
        VtsJsObjects += "\"iconImage\":\"img/logo_SOUNDREP.png\",";
        VtsJsObjects += "\"VTSGuideLink\":\"http://www.sjofartsverket.se/pages/32062/SoundVTS2011.pdf\",";
        VtsJsObjects += "\"showMaxDraught\":true,";
        VtsJsObjects += "\"showAirDraught\":true,";
        VtsJsObjects += "\"showFuelQuantity\":false,";
        VtsJsObjects += "\"showFuelDetails\":true,";
        VtsJsObjects += "\"showVesselType\":false,";
        VtsJsObjects += "\"showVesselLength\":false,";
        VtsJsObjects += "\"showDeadWeightTonnage\":false,";
        VtsJsObjects += "\"showGrossTonnage\":false,";
        VtsJsObjects += "\"showTrueCourse\":false,";
        VtsJsObjects += "\"showVesselCurrentPosition\":false,";
        VtsJsObjects += "\"deadWeightTonnageLimit\":1000,";
        VtsJsObjects += "\"sendSummaryTo\":\"email\"}";

        VtsJsObjects += ","; //next item

        VtsJsObjects += "{\"id\": "+(counter++)+",";
        VtsJsObjects += "\"shortname\": \"GOFREP-Helsinki\",";
        VtsJsObjects += "\"name\": \"Finland - GOFREP - Gulf Of Finland Vessel Traffic Service\",";
        VtsJsObjects += "\"areaWKT\":\"\",";
        VtsJsObjects += "\"callsign\":\"Helsinki Traffic\",";
        VtsJsObjects += "\"email\":\"gofrep@fta.fi\",";
        VtsJsObjects += "\"telephone\":\"+358 (0)204 48 5387\",";
        VtsJsObjects += "\"telephone2\":\"+358 (0)204 48 5388\",";
        VtsJsObjects += "\"fax\":\"+358 (0)204 48 5394\",";
        VtsJsObjects += "\"vhfchannel1\":\"60\",";
        VtsJsObjects += "\"vhfchannel2\":\"\",";
        VtsJsObjects += "\"vhfchannel3\":\"\",";
        VtsJsObjects += "\"vhfchannel4\":\"\",";
        VtsJsObjects += "\"vhfreservechannel1\":\"80\",";
        VtsJsObjects += "\"vhfreservechannel2\":\"\",";
        VtsJsObjects += "\"iconImage\":\"img/OpenPortGuideLogo_32.png\",";
        VtsJsObjects += "\"VTSGuideLink\":\"http://www.vta.ee/public/GOFREP_web.pdf\",";
        VtsJsObjects += "\"showMaxDraught\":true,";
        VtsJsObjects += "\"showAirDraught\":false,";
        VtsJsObjects += "\"showFuelQuantity\":true,";
        VtsJsObjects += "\"showFuelDetails\":false,";
        VtsJsObjects += "\"showVesselType\":true,";
        VtsJsObjects += "\"showVesselLength\":true,";
        VtsJsObjects += "\"showDeadWeightTonnage\":false,";
        VtsJsObjects += "\"showGrossTonnage\":false,";
        VtsJsObjects += "\"showTrueCourse\":true,";
        VtsJsObjects += "\"showVesselCurrentPosition\":true,";
        VtsJsObjects += "\"deadWeightTonnageLimit\":0,";
        VtsJsObjects += "\"sendSummaryTo\":\"email\"}";

        VtsJsObjects += ","; //next item

        VtsJsObjects += "{\"id\": "+(counter++)+",";
        VtsJsObjects += "\"shortname\": \"GOFREP-Tallinn\",";
        VtsJsObjects += "\"name\": \"Estonia - GOFREP Tallinn - Gulf Of Finland Vessel Traffic Service\",";
        VtsJsObjects += "\"areaWKT\":\"\",";
        VtsJsObjects += "\"callsign\":\"Tallinn Traffic\",";
        VtsJsObjects += "\"email\":\"gofrep@vta.ee\",";
        VtsJsObjects += "\"telephone\":\"+372 6 205 764\",";
        VtsJsObjects += "\"telephone2\":\"+372 6 205 777\",";
        VtsJsObjects += "\"fax\":\"+372 620 5766\",";
        VtsJsObjects += "\"vhfchannel1\":\"61\",";
        VtsJsObjects += "\"vhfchannel2\":\"\",";
        VtsJsObjects += "\"vhfchannel3\":\"\",";
        VtsJsObjects += "\"vhfchannel4\":\"\",";
        VtsJsObjects += "\"vhfreservechannel1\":\"81\",";
        VtsJsObjects += "\"vhfreservechannel2\":\"\",";
        VtsJsObjects += "\"iconImage\":\"img/OpenPortGuideLogo_32.png\",";
        VtsJsObjects += "\"VTSGuideLink\":\"http://www.vta.ee/public/GOFREP_web.pdf\",";
        VtsJsObjects += "\"showMaxDraught\":true,";
        VtsJsObjects += "\"showAirDraught\":false,";
        VtsJsObjects += "\"showFuelQuantity\":true,";
        VtsJsObjects += "\"showFuelDetails\":false,";
        VtsJsObjects += "\"showVesselType\":true,";
        VtsJsObjects += "\"showVesselLength\":true,";
        VtsJsObjects += "\"showDeadWeightTonnage\":false,";
        VtsJsObjects += "\"showGrossTonnage\":false,";
        VtsJsObjects += "\"showTrueCourse\":true,";
        VtsJsObjects += "\"showVesselCurrentPosition\":true,";
        VtsJsObjects += "\"deadWeightTonnageLimit\":0,";
        VtsJsObjects += "\"sendSummaryTo\":\"email\"}";

        VtsJsObjects += ","; //next item

        VtsJsObjects += "{\"id\": "+(counter++)+",";
        VtsJsObjects += "\"shortname\": \"GOFREP-St.Petersburg\",";
        VtsJsObjects += "\"name\": \"Russia - GOFREP St. Petersburg - Gulf Of Finland Vessel Traffic Service\",";
        VtsJsObjects += "\"areaWKT\":\"\",";
        VtsJsObjects += "\"callsign\":\"St. Peterburg Traffic\",";
        VtsJsObjects += "\"email\":\"gofrep@rsbm.ru\",";
        VtsJsObjects += "\"telephone\":\"+7 12 380 70 21\",";
        VtsJsObjects += "\"telephone2\":\"+7 812 380 70 81\",";
        VtsJsObjects += "\"fax\":\"+7 812 3880 70 20\",";
        VtsJsObjects += "\"vhfchannel1\":\"74\",";
        VtsJsObjects += "\"vhfchannel2\":\"\",";
        VtsJsObjects += "\"vhfchannel3\":\"\",";
        VtsJsObjects += "\"vhfchannel4\":\"\",";
        VtsJsObjects += "\"vhfreservechannel1\":\"10\",";
        VtsJsObjects += "\"vhfreservechannel2\":\"\",";
        VtsJsObjects += "\"iconImage\":\"img/OpenPortGuideLogo_32.png\",";
        VtsJsObjects += "\"VTSGuideLink\":\"http://www.vta.ee/public/GOFREP_web.pdf\",";
        VtsJsObjects += "\"showMaxDraught\":true,";
        VtsJsObjects += "\"showAirDraught\":false,";
        VtsJsObjects += "\"showFuelQuantity\":true,";
        VtsJsObjects += "\"showFuelDetails\":false,";
        VtsJsObjects += "\"showVesselType\":true,";
        VtsJsObjects += "\"showVesselLength\":true,";
        VtsJsObjects += "\"showDeadWeightTonnage\":false,";
        VtsJsObjects += "\"showGrossTonnage\":false,";
        VtsJsObjects += "\"showTrueCourse\":true,";
        VtsJsObjects += "\"showVesselCurrentPosition\":true,";
        VtsJsObjects += "\"deadWeightTonnageLimit\":0,";
        VtsJsObjects += "\"sendSummaryTo\":\"email\"}";

        VtsJsObjects += "]"; //end array

        return "{\"timestampUTC\":\"" + timestamp + "\",\"VtsJsObjects\":"+VtsJsObjects+"}";
    }

}





