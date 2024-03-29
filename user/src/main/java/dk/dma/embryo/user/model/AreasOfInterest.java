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
package dk.dma.embryo.user.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import dk.dma.embryo.common.EmbryonicException;
import dk.dma.embryo.common.area.Area;
import dk.dma.embryo.common.persistence.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.stream.Stream;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AreasOfInterest extends BaseEntity<Long> {

    private static final long serialVersionUID = -8480232439011093135L;

    // //////////////////////////////////////////////////////////////////////
    // Entity fields (also see super class)
    // //////////////////////////////////////////////////////////////////////

    private String name;
    @Lob
    @Column(columnDefinition = "TEXT")
    private String polygonsAsJson;
    private Boolean active;

    private static final String LEFT = "left";
    private static final String RIGHT = "right";
    private static final String BOTTOM = "bottom";
    private static final String TOP = "top";

    // //////////////////////////////////////////////////////////////////////
    // Business Logic
    // //////////////////////////////////////////////////////////////////////
    public Stream<Area> extractBounds() {
        if (getPolygonsAsJson() != null && !getPolygonsAsJson().trim().equalsIgnoreCase("[]")) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                @SuppressWarnings("unchecked")
                ArrayList<LinkedHashMap<String, Double>> bounds = (ArrayList<LinkedHashMap<String, Double>>) mapper.readValue(getPolygonsAsJson(), ArrayList.class);
                return bounds.stream().map(entry -> new Area(entry.get(LEFT), entry.get(TOP), entry.get(RIGHT), entry.get(BOTTOM)));
            } catch (IOException e) {
                throw new EmbryonicException("Error parsing json for InterestOfAreas", e);
            }
        }
        return Stream.empty();
    }
}
