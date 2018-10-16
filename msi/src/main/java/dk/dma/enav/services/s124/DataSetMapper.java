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
package dk.dma.enav.services.s124;

import dk.dma.enav.services.s124.S124View.S124ViewBuilder;
import org.opengis.feature.simple.SimpleFeature;

import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public class DataSetMapper {
    public S124View toViewType(SimpleFeature dataset) {
        S124View.S124ViewBuilder builder = new S124ViewBuilder(dataset.getID());


        Map<String, Object> nwPreamble = (Map<String, Object>) dataset.getAttribute("imember");
        if (nwPreamble != null) {
            builder
                    .id((String) nwPreamble.get("id"))
                    .title(getTitle(nwPreamble))
                    .areaHeading(createAreaHeading(nwPreamble))
                    .mainType("NW");

        }


        return builder.build();
    }

    private String getTitle(Map<String, Object> nwPreamble) {
        String res;
        Object titleElement = nwPreamble.get("title");
        if (titleElement instanceof List) {
            List titles = (List) titleElement;
            Map<String, Object> titleMap = (Map<String, Object>) titles.stream().filter(this::localizableFilter).findFirst().orElseGet(() -> titles.get(0));
            res = (String) titleMap.get("text");
        } else {
            Map<String, Object> title = (Map<String, Object>) titleElement;
            res = (String) title.get("text");
        }
        return res;
    }

    private boolean localizableFilter(Object o) {
        Map<String, Object> localizable = (Map<String, Object>) o;

        Object language = localizable.get("language");
        return language == null || language.equals("eng");
    }

    private String createAreaHeading(Map<String, Object> nwPreamble) {
        StringJoiner res = new StringJoiner(" - ");
        Object generalAreaElement = nwPreamble.get("generalArea");
        if (generalAreaElement instanceof List) {
            List<Map<String, Object>> areas = (List<Map<String, Object>>) generalAreaElement;
            res.add(areas.stream().map(this::getLocationText).collect(Collectors.joining(" - ")));
        } else {
            res.add(getLocationText((Map<String, Object>) generalAreaElement));
        }

        Object locality = nwPreamble.get("locality");
        if (locality instanceof List) {
            List<Map<String, Object>> localities = (List<Map<String, Object>>) locality;
            res.add(localities.stream().map(this::getLocationText).collect(Collectors.joining(" - ")));
        } else {
            res.add(getLocationText((Map<String, Object>) locality));
        }


        return res.toString();
    }

    private String getLocationText(Map<String, Object> area) {
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
}
