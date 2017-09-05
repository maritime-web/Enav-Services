package dk.dma.enav.services.vtsreport.service;

import lombok.Getter;
import lombok.Setter;

/**
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
