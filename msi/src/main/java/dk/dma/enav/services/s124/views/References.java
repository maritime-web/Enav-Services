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
import lombok.ToString;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ToString
@Getter
public class References {
    private String id;
    private Map<String, List<Map<String, Object>>> other;
    private String reference;

    public References() {
        other = new HashMap<>();
    }

    public void setId(String id) {

        this.id = id;
    }

    public void setReference(String reference) {

        this.reference = reference;
    }

    public void setOthers(Map<String, List<Map<String, Object>>> others) {
        other = others;
    }
}
