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

package dk.dma.embryo.dataformats.model;

import com.google.common.base.Preconditions;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

/**
 * Value class representing identification of a forecast data document.
 */
@Getter
@EqualsAndHashCode(of = "id")
@ToString
public class ForecastDataId {
    private String area;
    private ForecastProvider provider;
    private Type type;
    private String id;

    public ForecastDataId(String area, ForecastProvider provider, Type type) {
        this.area = Preconditions.checkNotNull(area, "area");
        this.provider = Preconditions.checkNotNull(provider, "provider");
        this.type = Preconditions.checkNotNull(type, "type");
        this.id = (area + provider + type).replaceAll("\\s", "-");
    }
}
