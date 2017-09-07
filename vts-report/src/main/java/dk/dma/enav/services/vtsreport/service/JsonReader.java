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

import lombok.Getter;
import lombok.Setter;

/**
 * Handles gettersetters for the expected JSON of the VTS report
 * Created by rob on 8/15/17.
 */
@Setter
@Getter
class JsonReader {
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
    String cargoDangerousCargoTotalTonnage;
    String cargoDangerousCargoOnBoard;
    String cargoIMOClassesOnBoard;
    String cargoPollutantOrDCLostOverBoard;
    String cargoAdditionalContactInformation;
    String cargoDPAName;
    String cargoDPATelephone;
    String cargoDPAEmail;

    CargoEntry[] cargoEntries;

    String voyagePositionLon;
    String voyagePositionLat;
    String voyageSpeed;
    String voyageTrueCourse;
    String voyageCourseOverGround;
    String voyagePortOfDestination;
    String voyagePortOfDestinationEta;
    String voyageVTSETADateTime;


}
