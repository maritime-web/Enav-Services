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
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
@ToString
@Getter
public class DataSet {
    private String id;
    private String mainType = "NW";
    private String warningType;
    private String areaHeading;
    private String title;
    private NWPreamble nwPreamble;
    private List<References> references;
    private List<NavigationalWarningFeaturePart> navigationalWarningFeaturePart;
    private Map<String, List<Map<String, Object>>> other;

    public DataSet() {
        references = new ArrayList<>();
        navigationalWarningFeaturePart = new ArrayList<>();
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setNWPreamble(NWPreamble nwPreamble) {
        this.nwPreamble = nwPreamble;
    }

    public void add(References references) {
        this.references.add(references);
    }

    public void addNavigationalWarningFeaturePart(NavigationalWarningFeaturePart part) {
        navigationalWarningFeaturePart.add(part);
    }

    public void createViewAttributes() {
        warningType = (String) nwPreamble.getMessageSeriesIdentifier().get("warningType");
        title = createTitle();
        areaHeading = createAreaHeading();
    }

    private String createTitle() {
        List<Map<String, Object>> titles = nwPreamble.getOther().get("title");
        Map<String, Object> title = titles.stream().filter(this::localizableFilter).findFirst().orElseGet(() -> titles.get(0));
        return (String) title.get("text");
    }

    private String createAreaHeading() {
        StringJoiner res = new StringJoiner(" - ");
        List<Map<String, Object>> generalAreas = nwPreamble.getGeneralAreas();
        res.add(generalAreas.stream().map(this::getLocationText).collect(Collectors.joining(" - ")));

        List<Map<String, Object>> locality = nwPreamble.getLocalities();
        if (locality != null) {
            res.add(locality.stream().map(this::getLocationText).collect(Collectors.joining(" - ")));
        }

        return res.toString();
    }

    private boolean localizableFilter(Object o) {
        Map<String, Object> localizable = (Map<String, Object>) o;

        Object language = localizable.get("language");
        return language == null || language.equals("eng");
    }

    private String  getLocationText(Map<String, Object> area) {
        String res;
        Object locationNameElement = area.get("locationName");
        if (locationNameElement instanceof List) {
            List<Map<String, Object>> areas = (List<Map<String, Object>>) locationNameElement;
            Map<String, Object> a = areas.stream().filter(this::localizableFilter).findFirst().orElseGet(() -> areas.get(0));
            res = (String) a.get("text");
        } else {
            res = (String) area.get("text");
        }
        return res;
    }

    public void setOthers(Map<String, List<Map<String, Object>>> others) {
        other = others;
    }
}
