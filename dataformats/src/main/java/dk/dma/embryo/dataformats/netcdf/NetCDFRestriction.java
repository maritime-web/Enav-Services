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
package dk.dma.embryo.dataformats.netcdf;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Restricts the range of the dimensions used in parsing.
 * 
 * @author avlund
 *
 */
@NoArgsConstructor
@Getter
@ToString
public class NetCDFRestriction {
    private boolean subarea;
    private double minLat;
    private double maxLat;
    private double minLon;
    private double maxLon;
    @Setter
    private int timeStart;
    @Setter
    private int timeInterval = 1;


    public NetCDFRestriction(double minLat, double maxLat, double minLon, double maxLon) {
        this.minLat = minLat;
        this.maxLat = maxLat;
        this.minLon = minLon;
        this.maxLon = maxLon;
        subarea = true;
    }

    public void setMinLat(double minLat) {
        this.minLat = minLat;
        subarea = true;
    }

    public void setMaxLat(double maxLat) {
        this.maxLat = maxLat;
        subarea = true;
    }

    public void setMinLon(double minLon) {
        this.minLon = minLon;
        subarea = true;
    }

    public void setMaxLon(double maxLon) {
        this.maxLon = maxLon;
        subarea = true;
    }
}
