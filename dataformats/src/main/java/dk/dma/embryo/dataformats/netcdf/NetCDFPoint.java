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

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serializable;

/**
 * Represents a coordinate's position in a NetCDF file.
 * // todo It looks like this class only exposes data via toString()....
 * @author avlund
 *
 */
@AllArgsConstructor
@EqualsAndHashCode
public class NetCDFPoint implements Serializable {
    private static final long serialVersionUID = 5935623275810717515L;

    private final int lat, lon;

    @Override
    public String toString() {
        return lat + "_" + lon;
    }
}
