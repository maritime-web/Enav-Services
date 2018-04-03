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
    private String version = "1.02";


    @GET
    @Produces("application/json")
    public String getMessage() {
        int counter = 1; //can't start with zero
        String VtsJsObjects = "["; //start as array

        VtsJsObjects += "{\"id\": "+(counter++)+",";
        VtsJsObjects += "\"shortname\": \"BELTREP\",";
        VtsJsObjects += "\"name\": \"The Great Belt Vessel Traffic Service\",";
        VtsJsObjects += "\"country\": \"Denmark\",";
        VtsJsObjects += "\"areaWKT\":\"POLYGON((10.8544921875 56.00020483338158,10.634765625 55.78612990561606,10.639572143554688 55.59377045090436,10.699481964111328 55.537471031636194,10.710639953613281 55.50948546957061,10.748062133789062 55.47992355387766,10.715789794921875 55.478367048901404,10.669441223144531 55.46999978135458,10.658798217773438 55.45072885429033,10.733985900878906 55.40377742551821,10.768489837646484 55.37696476612461,10.801620483398438 55.34154420930367,10.811169147491455 55.31166093369811,10.841317176818848 55.297004038677564,10.832948684692383 55.28722976638847,10.791492462158203 55.30990239223714,10.769004821777344 55.295831253109355,10.792222023010254 55.289380312845964,10.778360366821289 55.27823530957378,10.80831527709961 55.231666217247664,10.807135105133057 55.195772778435874,10.780549049377441 55.139051076761305,10.747976303100586 55.079644702089944,10.723793506622314 55.06219879765375,10.679011344909668 55.063231020579984,10.706326961517334 55.038696238018964,10.829848051071167 55.018550750900516,10.934180617332458 55.083611969234184,11.150436401367188 55.082592554253026,11.149063110351562 55.14189643701843,11.257553100585938 55.20189604994834,11.23852014541626 55.21917084513638,11.255192756652832 55.22194929359606,11.237726211547852 55.24160096923464,11.24948501586914 55.25896858562746,11.235923767089844 55.28977130877401,11.177494525909424 55.30639728881813,11.135544776916504 55.32887586585372,11.134042739868164 55.340226304556936,11.09670639038086 55.35042671966474,11.110830903053284 55.36434738778924,11.154481172561646 55.36460654329584,11.177237033843994 55.377610921211996,11.206269264221191 55.3825481729472,11.220388412475586 55.40255906269813,11.199072693125345 55.41843292389684,11.202476212056354 55.453774049184325,11.180444138590246 55.47496812006111,11.173458849079907 55.49982449187042,11.153995105996728 55.509072576453576,11.135666985064745 55.52445292225763,11.122356690466404 55.53344056609079,11.112215593457222 55.53042982740715,11.116652637720108 55.52285291109611,11.107673943042755 55.51236055508394,11.093836426734924 55.51236814879824,11.085387468338013 55.51704863552835,11.09046220779419 55.53262599872962,11.118196249008179 55.53537644779899,11.141810417175293 55.53424107602157,11.160741448402405 55.55120448704025,11.155639886856079 55.577865551339315,11.128957271575928 55.62338016304374,11.072872281074524 55.63962691171739,11.036933362483978 55.65259070786218,10.982571691274643 55.65790893636428,10.948809385299683 55.66167718533653,10.93449980020523 55.67114878401218,10.988456457853317 55.66504114367706,11.00719504058361 55.667796318034206,11.068505644798279 55.65931989720547,11.094996482133865 55.6611514225246,11.100201308727264 55.67343880884251,11.070013800635934 55.67997788608094,11.01716699078679 55.705478000578836,11.004857160151005 55.72241174871169,10.963758006691933 55.731907344624524,10.922758430242538 55.72731172907511,10.881072208285332 55.7401139924351,10.925925616174936 55.748531488411665,10.96803244203329 55.746127065758856,11.039665024727583 55.72980566620513,11.085725636221468 55.73365054745812,11.127666374668479 55.74909046737872,11.148664425127208 55.75042164856431,11.175299619790167 55.75282614853778,11.181958418455906 55.73990043224082,11.203140600991901 55.73034295332386,11.222262847004458 55.73702270414168,11.217009177489672 55.745440567607844,11.223995379841654 55.74771651581482,11.238474809142645 55.734939441648315,11.256014206410327 55.7254559336383,11.27630018570926 55.730665168065144,11.29610907722963 55.74720122607713,11.310820041544503 55.75237535883099,11.347701280537876 55.755348564275536,11.36516133637997 55.76613660178423,11.380071173871329 55.77307440750018,11.382621392222063 55.78271393571909,11.378795437321969 55.79889825864999,11.377569105379735 55.81335444345163,11.376955939408617 55.82829471821824,11.369782901344934 55.84116069432536,11.394348848133404 55.827156267569855,11.449890488519827 55.845217981244815,11.49002092785372 55.84383811617136,11.520385830137798 55.86627273832417,11.527328535186143 55.89558460364457,11.528053305679009 55.92716232336522,11.495456706550385 55.94024970959475,11.464052205814255 55.95102096750692,11.431870463258747 55.950639038914815,11.376640798035623 55.955061311971065,11.338726282806874 55.97033937084626,11.323888898239375 55.972212918131405,11.308917105369687 55.97929691361664,11.299371272411463 55.97477068998916,11.291165128393231 55.984032874307026,11.28570556640625 56.00174066390333,10.8544921875 56.00020483338158))\",";
        VtsJsObjects += "\"callsign\":\"Great Belt Traffic\",";
        VtsJsObjects += "\"email\":\"vts@beltrep.org\",";
        VtsJsObjects += "\"telephone1\":\"+45 58 37 68 68\",";
        VtsJsObjects += "\"telephone2\":\"\",";
        VtsJsObjects += "\"fax\":\"\",";
        VtsJsObjects += "\"vhfchannel1\":\"North 74\",";
        VtsJsObjects += "\"vhfchannel2\":\"South 11\",";
        VtsJsObjects += "\"vhfchannel3\":\"\",";
        VtsJsObjects += "\"vhfchannel4\":\"\",";
        VtsJsObjects += "\"vhfreservechannel1\":\"11\",";
        VtsJsObjects += "\"vhfreservechannel2\":\"\",";
        VtsJsObjects += "\"iconImage\":\"img/vts_logos/forsvaret_logo.jpg\",";
        VtsJsObjects += "\"VTSGuideLink\":\"http://forsvaret.dk/VTSSTB/eng/Documents/BELTREP%20Information%20ver%200.pdf\",";
        VtsJsObjects += "\"showPortOfDestination\":true,";
        VtsJsObjects += "\"showMaxDraught\":false,";
        VtsJsObjects += "\"draughtMax\":20,"; //meters
        VtsJsObjects += "\"showAirDraught\":true,";
        VtsJsObjects += "\"airDraughtMax\":65,"; //meters
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
        VtsJsObjects += "\"name\": \"Sound Vessel Traffic Service\",";
        VtsJsObjects += "\"country\": \"Sweden & Denmark\",";
        VtsJsObjects += "\"areaWKT\":\"POLYGON((12.18333 56.11611, 12.18333 56.23333, 12.29417 56.30222, 12.45778 56.30222,12.8 56.13333, 13.13333 55.68333, 13.0425 55.39139, 12.91389 55.16667,12.45778 55.16667,12.45778 55.29556, 12.59177997558595 55.55481561965491, 12.59806 55.55778, 12.18333 56.11611))\",";
        VtsJsObjects += "\"callsign\":\"Sound Traffic\",";
        VtsJsObjects += "\"email\":\"contact@soundvts.org\",";
        VtsJsObjects += "\"telephone1\":\"+46 771-630600\",";
        VtsJsObjects += "\"telephone2\":\"\",";
        VtsJsObjects += "\"fax\":\"\",";
        VtsJsObjects += "\"vhfchannel1\":\"North 73\",";
        VtsJsObjects += "\"vhfchannel2\":\"South 71\",";
        VtsJsObjects += "\"vhfchannel3\":\"\",";
        VtsJsObjects += "\"vhfchannel4\":\"\",";
        VtsJsObjects += "\"vhfreservechannel1\":\"68\",";
        VtsJsObjects += "\"vhfreservechannel2\":\"79\",";
        VtsJsObjects += "\"iconImage\":\"img/vts_logos/logo_SOUNDREP.png\",";
        VtsJsObjects += "\"VTSGuideLink\":\"http://www.sjofartsverket.se/pages/32062/SoundVTS2011.pdf\",";
        VtsJsObjects += "\"showPortOfDestination\":true,";
        VtsJsObjects += "\"showMaxDraught\":true,";
        VtsJsObjects += "\"draughtMax\":8,"; //meters
        VtsJsObjects += "\"showAirDraught\":true,";
        VtsJsObjects += "\"airDraughtMax\":55,"; //meters
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
        VtsJsObjects += "\"name\": \"Gulf Of Finland Vessel Traffic Service\",";
        VtsJsObjects += "\"country\": \"Finland\",";
        VtsJsObjects += "\"areaWKT\":\"POLYGON((24.649658203125 59.76514424409562,26.61503706127405 59.951086047529095,26.595224048942327 60.09026504940924,26.611116603016853 60.146929859621764,26.666447073221207 60.179237670405364,26.941902935504913 60.23285096901605,27.113786339759827 60.23362814077648,27.237826585769653 60.27333864027852,26.733343601226807 60.262822553916955,26.625256538391113 60.22814498987895,25.689477920532227 60.134069875476186,25.702075958251953 60.02479690435002,25.480079650878906 60.00348748823943,24.65141773223877 59.94730435173578,23.575563430786133 59.736352101379545,23.15145492553711 59.67849446024209,22.844314575195312 59.687473427713876,22.835235595703125 59.75381607559784,22.47100830078125 59.72310289922612,22.466278076171875 59.558077430133906,21.5435791015625 59.28946745522361,21.7193603515625 59.155100214248826,22.6092529296875 59.4361386136859,24.649658203125 59.76514424409562))\",";
        VtsJsObjects += "\"callsign\":\"Helsinki Traffic\",";
        VtsJsObjects += "\"email\":\"gofrep@fta.fi\",";
        VtsJsObjects += "\"telephone1\":\"+358 (0)204 48 5387\",";
        VtsJsObjects += "\"telephone2\":\"+358 (0)204 48 5388\",";
        VtsJsObjects += "\"fax\":\"+358 (0)204 48 5394\",";
        VtsJsObjects += "\"vhfchannel1\":\"60\",";
        VtsJsObjects += "\"vhfchannel2\":\"\",";
        VtsJsObjects += "\"vhfchannel3\":\"\",";
        VtsJsObjects += "\"vhfchannel4\":\"\",";
        VtsJsObjects += "\"vhfreservechannel1\":\"80\",";
        VtsJsObjects += "\"vhfreservechannel2\":\"\",";
        VtsJsObjects += "\"iconImage\":\"img/vts_logos/OpenPortGuideLogo_32.png\",";
        VtsJsObjects += "\"VTSGuideLink\":\"http://www.vta.ee/public/GOFREP_web.pdf\",";
        VtsJsObjects += "\"showPortOfDestination\":true,";
        VtsJsObjects += "\"showMaxDraught\":true,";
        VtsJsObjects += "\"draughtMax\":30,"; //meters
        VtsJsObjects += "\"showAirDraught\":false,";
        VtsJsObjects += "\"airDraughtMax\":0,"; //meters
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
        VtsJsObjects += "\"name\": \"Gulf Of Finland Vessel Traffic Service\",";
        VtsJsObjects += "\"country\": \"Estonia\",";
        VtsJsObjects += "\"areaWKT\":\"POLYGON((24.649658203125 59.76514424409562,26.62053022533655 59.95246134015228,26.679781153798103 59.74804906453188,27.644230276346207 59.63111639068277,28.057015240192413 59.468657060713205,27.915788292884827 59.366045402614745,25.447055101394653 59.45708788493664,25.167791843414307 59.52719396152097,25.153088569641113 59.70301489986097,24.678735733032227 59.69070721967096,24.433155059814453 59.63832420026402,24.370460510253906 59.47756159918352,24.37675952911377 59.38134132436305,24.097414016723633 59.26800861080957,23.73647689819336 59.203718246848524,23.508987426757812 59.2212581229195,22.599029541015625 59.08310799830076,22.63580322265625 58.981084524951065,22.5225830078125 58.89551991212817,22.054443359375 58.92848220536834,21.7193603515625 59.155100214248826,22.6092529296875 59.4361386136859,24.649658203125 59.76514424409562))\",";
        VtsJsObjects += "\"callsign\":\"Tallinn Traffic\",";
        VtsJsObjects += "\"email\":\"gofrep@vta.ee\",";
        VtsJsObjects += "\"telephone1\":\"+372 6 205 764\",";
        VtsJsObjects += "\"telephone2\":\"+372 6 205 777\",";
        VtsJsObjects += "\"fax\":\"+372 620 5766\",";
        VtsJsObjects += "\"vhfchannel1\":\"61\",";
        VtsJsObjects += "\"vhfchannel2\":\"\",";
        VtsJsObjects += "\"vhfchannel3\":\"\",";
        VtsJsObjects += "\"vhfchannel4\":\"\",";
        VtsJsObjects += "\"vhfreservechannel1\":\"81\",";
        VtsJsObjects += "\"vhfreservechannel2\":\"\",";
        VtsJsObjects += "\"iconImage\":\"img/vts_logos/OpenPortGuideLogo_32.png\",";
        VtsJsObjects += "\"VTSGuideLink\":\"http://www.vta.ee/public/GOFREP_web.pdf\",";
        VtsJsObjects += "\"showPortOfDestination\":true,";
        VtsJsObjects += "\"showMaxDraught\":true,";
        VtsJsObjects += "\"draughtMax\":30,"; //meters
        VtsJsObjects += "\"showAirDraught\":false,";
        VtsJsObjects += "\"airDraughtMax\":0,"; //meters
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
        VtsJsObjects += "\"name\": \"St. Petersburg - Gulf Of Finland Vessel Traffic Service\",";
        VtsJsObjects += "\"country\": \"Russia\",";
        VtsJsObjects += "\"areaWKT\":\"POLYGON((26.61503706127405 59.951086047529095,26.595224048942327 60.09026504940924,26.611116603016853 60.146929859621764,26.666447073221207 60.179237670405364,26.941902935504913 60.23285096901605,27.113786339759827 60.23362814077648,27.237826585769653 60.27333864027852,27.441961765289307 60.303668012016914,27.693676948547363 60.43478931155425,27.680749893188477 60.4613662462411,27.735919952392578 60.478610321157305,27.76087760925293 60.50125540457493,27.78034210205078 60.55090310246514,28.176808953285217 60.53994969484775,28.320590257644653 60.67460138982486,28.542234897613525 60.68506496264257,28.69987964630127 60.743588496337495,28.79071831703186 60.69089973488096,28.70577573776245 60.54911871749636,28.755415678024292 60.40128766873473,28.977989554405212 60.261789009681294,29.083783328533173 60.210924802882644,29.372886270284653 60.17726855008796,29.533917233347893 60.225960070880646,29.845145605504513 60.19844804231959,30.072170924395323 60.10538200893658,30.009902333840728 60.02857762722175,30.22596042137593 60.00109613767738,30.332155227661133 59.90755547079511,30.25468349456787 59.87327359956075,29.99072790145874 59.84508348834203,29.693955183029175 59.93021302899041,29.128088355064392 59.96719790836119,29.0923473238945 59.86179401156369,28.882216066122055 59.77303554890563,28.716725632548332 59.75901370973583,28.53510346263647 59.83767209434797,28.45527870580554 59.65288037485857,28.217612421140075 59.65736595271981,28.137231427244842 59.775947592584586,28.031122961547226 59.691237588451195,28.109904666198418 59.60436162007957,28.116336534149013 59.52742709431608,28.04586410522461 59.47264597985436,27.524490356445312 59.631977720080876,26.685943603515625 59.71090008448983,26.6265869140625 59.84750023273905,26.61503706127405 59.951086047529095))\",";
        VtsJsObjects += "\"callsign\":\"St. Petersburg Traffic\",";
        VtsJsObjects += "\"email\":\"gofrep@rsbm.ru\",";
        VtsJsObjects += "\"telephone1\":\"+7 12 380 70 21\",";
        VtsJsObjects += "\"telephone2\":\"+7 812 380 70 81\",";
        VtsJsObjects += "\"fax\":\"+7 812 3880 70 20\",";
        VtsJsObjects += "\"vhfchannel1\":\"74\",";
        VtsJsObjects += "\"vhfchannel2\":\"\",";
        VtsJsObjects += "\"vhfchannel3\":\"\",";
        VtsJsObjects += "\"vhfchannel4\":\"\",";
        VtsJsObjects += "\"vhfreservechannel1\":\"10\",";
        VtsJsObjects += "\"vhfreservechannel2\":\"\",";
        VtsJsObjects += "\"iconImage\":\"img/vts_logos/OpenPortGuideLogo_32.png\",";
        VtsJsObjects += "\"VTSGuideLink\":\"http://www.vta.ee/public/GOFREP_web.pdf\",";
        VtsJsObjects += "\"showPortOfDestination\":true,";
        VtsJsObjects += "\"showMaxDraught\":true,";
        VtsJsObjects += "\"draughtMax\":30,"; //meters
        VtsJsObjects += "\"showAirDraught\":false,";
        VtsJsObjects += "\"airDraughtMax\":0,"; //meters
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

        return "{\"timestampUTC\":\"" + timestamp + "\",\"version\":\"" + version + "\",\"VtsJsObjects\":"+VtsJsObjects+"}";
    }

}





