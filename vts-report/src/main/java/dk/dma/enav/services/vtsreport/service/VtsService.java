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

import dk.dma.enav.services.vtsreport.mail.VtsReportMail;
import lombok.Getter;
import lombok.Setter;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import dk.dma.embryo.common.configuration.PropertyFileService;
import dk.dma.embryo.common.mail.MailSender;
import dk.dma.embryo.user.security.Subject;


/**
 * Created by rob on 5/31/17.

 * Catches JSON from frontend, compiles a nice html email (subject/body) then sends it to the intended address using a backend lookup.
 * TODO - make a service in the maritime cloud which hosts the objects for the VTS interface.
 * TODO - security of the passed strings, injection killing etc.
 */
@SuppressWarnings("all")
@Path("/vtsemail")
public class VtsService {

    @Inject
    private PropertyFileService propertyFileService;

    @Inject
    private MailSender mailSender;

    @Inject
    private Subject subject;

    private String generatedEmailSubject = "";
    private String generatedEmailBody = "";
    private String emailTo = "";
    private String userEmail = "dma.vts.test@gmail.com";

    @Setter
    @Getter
    private static class Reader{
        String vtsShortName;
        String vtsCallSign;
        String vtsEmail;

        String vesselName;
        String vesselCallSign;
        String vesselMMSI;
        String vesselIMO;
        String vesselDraught;
        String vesselAirDraught;
        String vesselPersonsOnboard;
        String vesselLength;
        String vesselDeadWeight;
        String vesselGRT;
        String vesselDefects;
        String vesselType;

        String fuelTotalFuel;
        String fuelTypeHFORegular;
        String fuelTypeHFOLowSulphur;
        String fuelTypeHFOUltraLowSulphur;
        String fuelTypeIFORegular;
        String fuelTypeIFOLowSulphur;
        String fuelTypeIFOUltraLowSulphur;
        String fuelTypeMDORegular;
        String fuelTypeMDOLowSulphur;
        String fuelTypeMDOUltraLowSulphur;
        String fuelTypeMGORegular;
        String fuelTypeMGOLowSulphur;
        String fuelTypeMGOUltraLowSulphur;
        String fuelTypeLPG;
        String fuelTypeLNG;

        String cargoType;
        String cargoIMOClass01;
        String cargoIMOClass02;
        String cargoIMOClass03;
        String cargoIMOClass04;
        String cargoIMOClass05;
        String cargoIMOClass06;
        String cargoIMOClass07;
        String cargoIMOClass08;
        String cargoIMOClass09;
        String cargoDangerousCargoTotalTonnage;
        String cargoDangerousCargoOnBoard;
        String cargoIMOClassesOnBoard;
        String cargoPollutantOrDCLostOverBoard;
        String cargoInformationOrOwnerContact;

        String voyagePositionLon;
        String voyagePositionLat;
        String voyageSpeed;
        String voyageTrueHeading;
        String voyagePortOfDestination;
        String voyageVTSETADate;
        String voyageVTSETATime;
    }


    @GET
    @Produces("text/plain")
    public String getMessage() {
        return "VTS report service endpoint - expecting POST.";
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createTrackInJSON(Reader vtsdata) {

        long ts = System.currentTimeMillis();
        Date localTime = new Date(ts);
        String format = "HH:mm:ss - yyyy/MM/dd";
        SimpleDateFormat sdf = new SimpleDateFormat (format);

        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String UTCtimestamp = sdf.format(new Date());

        //"Example: BELTREP ship report: Brick of the Sea, IMO123456789, 11:15:43 - 2017/05/29 UTC"
        String emailSubject = vtsdata.vtsShortName + " ship report: " + vtsdata.vesselName + ", IMO" + vtsdata.vesselIMO + ", " + UTCtimestamp + " UTC";

        //Start
        String emailBody = "";
        emailBody += "<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional //EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'>";
        emailBody += "<html><body style='color: #111111; font-family:Verdana; font-size:11px;'>";

        //General info about this email
        emailBody += "<div style='color:#bbbbbb' font-size:9px;>";
        emailBody += "This email was generated by the Baltic Web interface, as part of the EfficienSea2 project, 2017, by the Danish Maritime Authority, under grant from the European Union's Horizon 2020 reseach and innovation programme, agreement No. 636329.<br>";
        emailBody += "Contact: Department of E-Navigation, sfs@dma.dk<br>";
        emailBody += "Time and date this report was generated:<span style='color:#111111'>" + UTCtimestamp + " UTC</span>";
        emailBody += "</div>";
        emailBody += "<br><br>";

        //VTS report content
        emailBody += "<br><div>";
        emailBody += "<span style='text-decoration: underline'>IMO Designator A</span><br>";

        if (vtsdata.vesselName != null && !vtsdata.vesselName.equals("")) {
            emailBody += "Vessel name: <strong>" + vtsdata.vesselName + "</strong><br>";
        }

        emailBody += "Vessel name: <strong>" + vtsdata.vesselName + "</strong><br>";
        emailBody += "Vessel callsign: <strong>" + vtsdata.vesselCallSign + "</strong><br>";
        emailBody += "<br>";

        emailBody += "<span style='text-decoration: underline'>IMO Designator B</span><br>";
        emailBody += "ETA at reporting area: <strong>" + vtsdata.voyageVTSETATime + " - " + vtsdata.voyageVTSETADate + " UTC </strong>";
        emailBody += "<br><br>";

        emailBody += "<span style='text-decoration: underline'>IMO Designator C</span><br>";
        emailBody += "Current vessel position: <strong>" + vtsdata.voyagePositionLon + " - " + vtsdata.voyagePositionLat + "</strong>";
        emailBody += "<br><br>";

        emailBody += "<span style='text-decoration: underline'>IMO Designator E</span><br>";
        emailBody += "Vessel true heading: <strong>" + vtsdata.voyageTrueHeading + "&deg;</strong>";
        emailBody += "<br><br>";

        emailBody += "<span style='text-decoration: underline'>IMO Designator F</span><br>";
        emailBody += "Vessel current speed: <strong>" + vtsdata.voyageSpeed + " knots</strong>";
        emailBody += "<br><br>";

        emailBody += "<span style='text-decoration: underline'>IMO Designator I</span><br>";
        emailBody += "Destination port: <strong>" + vtsdata.voyagePortOfDestination + "</strong><br>";
        emailBody += "Expected time of arrival at "+vtsdata.voyagePortOfDestination+": <strong>N/A or unknown</strong>";
        emailBody += "<br><br>";

        emailBody += "<span style='text-decoration: underline'>IMO Designator L</span><br>";
        emailBody += "Expected route: <strong>Currently disabled function</strong>";
        emailBody += "<br><br>";

        emailBody += "<span style='text-decoration: underline'>IMO Designator O</span><br>";
        emailBody += "Current maximum draught: <strong>" + vtsdata.vesselDraught + "</strong><br>";
        emailBody += "<br><br>";
        emailBody += "<span style='text-decoration: underline'>IMO Designator U2</span><br>";
        emailBody += "Current air draught: <strong>" + vtsdata.vesselAirDraught + "</strong>";
        emailBody += "<br><br>";

        //vessel defects or deficiencies
        if(vtsdata.vesselDefects != "0" && vtsdata.vesselDefects != ""  && vtsdata.vesselDefects != null) {
            emailBody += "<span style='font-weight: bold; color:BB0000;'>Vessel defects or deficiencies:</span>";
            emailBody += "<br>" + vtsdata.vesselDefects;
        }

        //Cargo information
        emailBody += "<span style='text-decoration: underline'>IMO Designator P</span><br>";
        emailBody += "Cargo Information:";

        if(vtsdata.cargoType != "0" && vtsdata.cargoType != ""  && vtsdata.cargoType != null) {
            emailBody += "General Cargo Type: <strong>" + vtsdata.cargoType + "</strong><br>";
        }

        if(vtsdata.cargoDangerousCargoTotalTonnage == "" || vtsdata.cargoDangerousCargoTotalTonnage == null) vtsdata.cargoDangerousCargoTotalTonnage = "0";
        emailBody += "Total tonnage of dangerous cargo: <strong>" + vtsdata.cargoDangerousCargoTotalTonnage + "</strong><br>"; //always displayed

        if(vtsdata.cargoIMOClassesOnBoard!="" && vtsdata.cargoIMOClassesOnBoard != null) {
            emailBody += "IMO classes of dangerous cargo: <strong>" + vtsdata.cargoIMOClassesOnBoard + "</strong><br>"; //always displayed
        }

        //Different classes of dangerous cargo
        if(vtsdata.cargoIMOClass01 != "0" && vtsdata.cargoIMOClass01 != ""  && vtsdata.cargoIMOClass01 != null) {
            emailBody += "IMO class 1 cargo tonnage: <strong>" + vtsdata.cargoIMOClass01 + "</strong><br>";
        }
        if(vtsdata.cargoIMOClass02 != "0" && vtsdata.cargoIMOClass02 != ""  && vtsdata.cargoIMOClass02 != null) {
            emailBody += "IMO class 2 cargo tonnage: <strong>" + vtsdata.cargoIMOClass02 + "</strong><br>";
        }
        if(vtsdata.cargoIMOClass03 != "0" && vtsdata.cargoIMOClass03 != ""  && vtsdata.cargoIMOClass03 != null) {
            emailBody += "IMO class 3 cargo tonnage: <strong>" + vtsdata.cargoIMOClass03 + "</strong><br>";
        }
        if(vtsdata.cargoIMOClass04 != "0" && vtsdata.cargoIMOClass04 != ""  && vtsdata.cargoIMOClass04 != null) {
            emailBody += "IMO class 4 cargo tonnage: <strong>" + vtsdata.cargoIMOClass04 + "</strong><br>";
        }
        if(vtsdata.cargoIMOClass05 != "0" && vtsdata.cargoIMOClass05 != ""  && vtsdata.cargoIMOClass05 != null) {
            emailBody += "IMO class 5 cargo tonnage: <strong>" + vtsdata.cargoIMOClass05 + "</strong><br>";
        }
        if(vtsdata.cargoIMOClass06 != "0" && vtsdata.cargoIMOClass06 != ""  && vtsdata.cargoIMOClass06 != null) {
            emailBody += "IMO class 6 cargo tonnage: <strong>" + vtsdata.cargoIMOClass06 + "</strong><br>";
        }
        if(vtsdata.cargoIMOClass07 != "0" && vtsdata.cargoIMOClass07 != ""  && vtsdata.cargoIMOClass07 != null) {
            emailBody += "IMO class 7 cargo tonnage: <strong>" + vtsdata.cargoIMOClass07 + "</strong><br>";
        }
        if(vtsdata.cargoIMOClass08 != "0" && vtsdata.cargoIMOClass08 != ""  && vtsdata.cargoIMOClass08 != null) {
            emailBody += "IMO class 8 cargo tonnage: <strong>" + vtsdata.cargoIMOClass08 + "</strong><br>";
        }
        if(vtsdata.cargoIMOClass09 != "0" && vtsdata.cargoIMOClass09 != ""  && vtsdata.cargoIMOClass09 != null) {
            emailBody += "IMO class 9 cargo tonnage: <strong>" + vtsdata.cargoIMOClass09 + "</strong><br>";
        }

        //dangerous cargo or pollutant lost over board
        if(vtsdata.cargoPollutantOrDCLostOverBoard != "0" && vtsdata.cargoPollutantOrDCLostOverBoard != ""  && vtsdata.cargoPollutantOrDCLostOverBoard != null) {
            emailBody += "<span style='font-weight: bold; color:BB0000;'>Dangerous cargo or pollutant lost over board:</span>";
            emailBody += "<br>"+vtsdata.cargoPollutantOrDCLostOverBoard ;
        }
        //END cargo information

        emailBody += "<br><br>";

        emailBody += "<span style='text-decoration: underline'>IMO Designator T</span><br>";
        emailBody += "Designated Person Ashore, or communication of cargo contact information:<br><strong>" + vtsdata.cargoInformationOrOwnerContact + "</strong>";
        emailBody += "<br><br>";

        emailBody += "<span style='text-decoration: underline'>IMO Designator W</span><br>";
        emailBody += "Number of persons on board: <strong>" + vtsdata.vesselPersonsOnboard + "</strong>";
        emailBody += "<br><br>";

        //Bunker information
        emailBody += "<span style='text-decoration: underline'>IMO Designator X</span><br>";
        emailBody += "Bunker information:";

        emailBody += "<br>";
        emailBody += "Total bunker tonnage: <strong>" + vtsdata.fuelTotalFuel + "</strong><br>";

        //HFO
        if(vtsdata.fuelTypeHFORegular != "0" && vtsdata.fuelTypeHFORegular != ""  && vtsdata.fuelTypeHFORegular != null) {
            emailBody += "HFO: <strong>" + vtsdata.fuelTypeHFORegular + "</strong><br>";
        }
        if(vtsdata.fuelTypeHFOLowSulphur != "0" && vtsdata.fuelTypeHFOLowSulphur != "" && vtsdata.fuelTypeHFOLowSulphur != null) {
            emailBody += "HFO Low Sulphur: <strong>" + vtsdata.fuelTypeHFOLowSulphur + "</strong><br>";
        }
        if(vtsdata.fuelTypeHFOUltraLowSulphur != "0" && vtsdata.fuelTypeHFOUltraLowSulphur != "" && vtsdata.fuelTypeHFOUltraLowSulphur != null) {
            emailBody += "HFO Ultra Low Sulphur: <strong>" + vtsdata.fuelTypeHFOUltraLowSulphur + "</strong><br>";
        }

        //IFO
        if(vtsdata.fuelTypeIFORegular != "0" && vtsdata.fuelTypeIFORegular != ""  && vtsdata.fuelTypeIFORegular != null) {
            emailBody += "IFO: <strong>" + vtsdata.fuelTypeIFORegular + "</strong><br>";
        }
        if(vtsdata.fuelTypeIFOLowSulphur != "0" && vtsdata.fuelTypeIFOLowSulphur != "" && vtsdata.fuelTypeIFOLowSulphur != null) {
            emailBody += "IFO Low Sulphur: <strong>" + vtsdata.fuelTypeIFOLowSulphur + "</strong><br>";
        }
        if(vtsdata.fuelTypeIFOUltraLowSulphur != "0" && vtsdata.fuelTypeIFOUltraLowSulphur != "" && vtsdata.fuelTypeIFOUltraLowSulphur != null) {
            emailBody += "IFO Ultra Low Sulphur: <strong>" + vtsdata.fuelTypeIFOUltraLowSulphur + "</strong><br>";
        }

        //MDO
        if(vtsdata.fuelTypeMDORegular != "0" && vtsdata.fuelTypeMDORegular != ""  && vtsdata.fuelTypeMDORegular != null) {
            emailBody += "MDO: <strong>" + vtsdata.fuelTypeMDORegular + "</strong><br>";
        }
        if(vtsdata.fuelTypeMDOLowSulphur != "0" && vtsdata.fuelTypeMDOLowSulphur != "" && vtsdata.fuelTypeMDOLowSulphur != null) {
            emailBody += "MDO Low Sulphur: <strong>" + vtsdata.fuelTypeMDOLowSulphur + "</strong><br>";
        }
        if(vtsdata.fuelTypeMDOUltraLowSulphur != "0" && vtsdata.fuelTypeMDOUltraLowSulphur != "" && vtsdata.fuelTypeMDOUltraLowSulphur != null) {
            emailBody += "MDO Ultra Low Sulphur: <strong>" + vtsdata.fuelTypeMDOUltraLowSulphur + "</strong><br>";
        }

        //MGO
        if(vtsdata.fuelTypeMGORegular != "0" && vtsdata.fuelTypeMGORegular != ""  && vtsdata.fuelTypeMGORegular != null) {
            emailBody += "MGO: <strong>" + vtsdata.fuelTypeMGORegular + "</strong><br>";
        }
        if(vtsdata.fuelTypeMGOLowSulphur != "0" && vtsdata.fuelTypeMGOLowSulphur != "" && vtsdata.fuelTypeMGOLowSulphur != null) {
            emailBody += "MGO Low Sulphur: <strong>" + vtsdata.fuelTypeMGOLowSulphur + "</strong><br>";
        }
        if(vtsdata.fuelTypeMGOUltraLowSulphur != "0" && vtsdata.fuelTypeMGOUltraLowSulphur != "" && vtsdata.fuelTypeMGOUltraLowSulphur != null) {
            emailBody += "MGO Ultra Low Sulphur: <strong>" + vtsdata.fuelTypeMGOUltraLowSulphur + "</strong><br>";
        }

        //LPG
        if(vtsdata.fuelTypeLPG != "0" && vtsdata.fuelTypeLPG != ""  && vtsdata.fuelTypeLPG != null) {
            emailBody += "LPG: <strong>" + vtsdata.fuelTypeLPG + "</strong><br>";
        }

        //LPG
        if(vtsdata.fuelTypeLNG != "0" && vtsdata.fuelTypeLNG != ""  && vtsdata.fuelTypeLNG != null) {
            emailBody += "LNG: <strong>" + vtsdata.fuelTypeLNG + "</strong><br>";
        }

        emailBody += "<br><br>";
        emailBody += "</div>";
        emailBody += "</body></html>";
        //End email



        //for email to actually send
        generatedEmailSubject = emailSubject;
        generatedEmailBody = emailBody;

        //gets the protected email address
        emailTo = propertyFileService.getProperty("vtsservice.emailaddress.by.vtsshortname."+vtsdata.vtsShortName) + ";";

//        userEmail = subject.getUser().getEmail(); //TODO get useremail from whereever it needs to come from

        mailSender.sendEmail(new VtsReportMail(generatedEmailSubject, generatedEmailBody, userEmail, emailTo, propertyFileService));

        return Response.status(201).entity("{\"confirm\":true}").build();

        //For debug, returns the email header and body with POST status
//      return Response.status(201).entity("{\"confirm\":true,\"message\":\""+emailSubject+"<br>"+emailBody+"<br>"+emailTo+"\"}").build();

    }




}





