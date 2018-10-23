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
package dk.dma.enav.services.s124.bindings;

import dk.dma.enav.services.s124.JtsConverter;
import dk.dma.enav.services.s124.views.NavigationalWarningFeaturePart;
import lombok.extern.slf4j.Slf4j;
import org.geotools.xml.AbstractComplexBinding;
import org.geotools.xml.ElementInstance;
import org.geotools.xml.Node;
import org.locationtech.jts.geom.Geometry;
import org.niord.model.geojson.GeometryVo;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
@Slf4j
public class NavigationalWarningFeaturePartTypeBinding extends AbstractComplexBinding {
    public static final QName Q_NAME =
            new QName("http://www.iho.int/S124/gml/cs0/0.1", "NavigationalWarningFeaturePartType");

    @Override
    public QName getTarget() {
        return Q_NAME;
    }

    @Override
    public Class getType() {
        return NavigationalWarningFeaturePart.class;
    }

    @SuppressWarnings("RedundantThrows")
    @Override
    public Object parse(ElementInstance instance, Node node, Object value) throws Exception {
        NavigationalWarningFeaturePart featurePart = new NavigationalWarningFeaturePart();

        String id = (String) node.getAttribute("id").getValue();
        featurePart.setId(id);

        Map<String, List<Map<String, Object>>> others = new HashMap<>();
        node.getChildren().forEach(c -> {
            Node child = (Node) c;

            String name = child.getComponent().getName();
            Object childValue = child.getValue();
            switch (name) {
                case "geometry":
                    if (childValue instanceof Geometry) {
                        GeometryVo geometry = JtsConverter.fromJts((Geometry) childValue);
                        featurePart.addGeometry(geometry);
                    } else if (childValue instanceof Map) {
                        if (!((Map)childValue).isEmpty()) {
                            log.info("Can't understand geometry in a Map!");
                            log.info(childValue.toString());
                        }
                    }
                    break;
                case "header":
                    Map<String, Object> headerValues = new HashMap<>();
                    child.getAttributes().forEach(a -> {
                        Node att = (Node) a;
                        headerValues.put(att.getComponent().getName(), att.getValue());
                    });
                    featurePart.setHeader(headerValues);
                    break;
                default:
                    others.computeIfAbsent(name, k -> new ArrayList<>()).add(S124ParseUtil.parseNode(child));

                    break;
            }
            featurePart.setOthers(others);
        });

        return featurePart;
    }
}
