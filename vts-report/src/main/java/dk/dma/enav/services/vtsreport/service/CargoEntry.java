package dk.dma.enav.services.vtsreport.service;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by rob on 8/15/17.
 */
@Setter
@Getter
public class CargoEntry {
    private String imoClass;
    private String note;
    private String tonnage;
}
