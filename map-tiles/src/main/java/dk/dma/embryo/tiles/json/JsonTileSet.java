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

package dk.dma.embryo.tiles.json;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import dk.dma.embryo.tiles.model.BoundingBox;

/**
 * Created by Jesper Tejlgaard on 10/02/14.
 */
@AllArgsConstructor
@Data
public class JsonTileSet {

    private final String name;
    private final String provider;
    private final String source;
    private final String sourceType;
    private final Date ts;
    private final String url;
    private final BoundingBox extend;

    // //////////////////////////////////////////////////////////////////////
    // Constructors
    // //////////////////////////////////////////////////////////////////////

    public JsonTileSet(String name, String provider, String source, String sourceType, Date ts, String url) {
        this(name, provider, source, sourceType, ts, url, null);
    }
}
