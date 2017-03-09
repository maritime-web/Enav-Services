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
package dk.dma.embryo.common.area;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by Jesper Tejlgaard on 6/9/15.
 */
@Getter
@Setter
@ToString
public class Area {

    // //////////////////////////////////////////////////////////////////////
    // POJO fields (also see super class)
    // //////////////////////////////////////////////////////////////////////
    private double left;
    private double right;
    private double top;
    private double bottom;

    // //////////////////////////////////////////////////////////////////////
    // Constructors
    // //////////////////////////////////////////////////////////////////////
    public Area(double left, double top, double right, double bottom) {
        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;
    }

}
