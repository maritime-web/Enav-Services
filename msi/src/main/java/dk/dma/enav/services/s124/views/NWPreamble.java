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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ToString
@Getter
public class NWPreamble {
    private String id;
    private List<Map<String, Object>> generalAreas;
    private List<Map<String, Object>> localities;
    private Map<String, List<Map<String, Object>>> other;
    private Map<String, Object> messageSeriesIdentifier;
    private Date publicationDate;
    private Date cancellationDate;

    public NWPreamble() {
        other = new HashMap<>();
        generalAreas = new ArrayList<>();
        localities = new ArrayList<>();
    }

    public void setId(String id) {

        this.id = id;
    }

    public void setMessageSeriesIdentifier(Map<String, Object> messageSeriesIdentifier) {

        this.messageSeriesIdentifier = messageSeriesIdentifier;
    }

    public void setPublicationDate(Date publicationDate) {

        this.publicationDate = publicationDate;
    }

    public void setCancellationDate(Date cancellationDate) {

        this.cancellationDate = cancellationDate;
    }

    public void setOthers(Map<String, List<Map<String, Object>>> others) {
        this.other = others;
    }

    public void addGeneralArea(Map<String, Object> generalArea) {
        generalAreas.add(generalArea);
    }

    public void addLocality(Map<String, Object> locality) {
        localities.add(locality);
    }
}
