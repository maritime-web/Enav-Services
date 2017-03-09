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
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

/**
 * This class maps a NetCDF variable to an ArcticWeb concept.
 * 
 * Simple variables are dimensions like latitude, longitude and time. Complex
 * variables are variables containing a multiple of the above mentioned
 * dimensions.
 * 
 * @author avlund
 *
 */
@Getter
@ToString
public class NetCDFVar {
    private final String varname, description;
    @Setter
    private int digits = 3;

    public NetCDFVar(String varname, String description) {
        this.varname = varname;
        this.description = description;
    }

    /**
     * Convenience method to add a CDF variable to a map using the varname as map key. 
     * 
     */
    public static void addToMap(Map<String, NetCDFVar> map, String varname, String description) {
        map.put(varname, new NetCDFVar(varname, description));
    }
}
