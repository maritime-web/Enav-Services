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
package dk.dma.enav.services.s124.views;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@ToString
@Getter
@Setter
public class Area {
    private String id;
    private List<LocationName> names;

    public Area() {
        names = new ArrayList<>();
    }

    public void addName(LocationName locationName) {
        names.add(locationName);
    }

    public String getText(String language) {
        return names.stream().filter(name -> localizableFilter(name, language)).findFirst().orElseGet(() -> names.get(0)).getText();
    }

    private boolean localizableFilter(LocationName locationName, String language) {
        return locationName.getLanguage() == null || locationName.getLanguage().equals(language);
    }

}
