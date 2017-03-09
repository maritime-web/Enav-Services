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
package dk.dma.embryo.vessel.json;

import lombok.Data;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import java.util.Date;

/**
 * Created by Jesper Tejlgaard on 6/24/15.
 */
@Data
public class TrackPos {

    private Double cog;
    private Double lat;
    private Double lon;
    private Double sog;
    private Date ts;
}
