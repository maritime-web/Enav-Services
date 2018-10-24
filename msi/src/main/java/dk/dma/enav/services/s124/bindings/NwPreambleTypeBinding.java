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

import dk.dma.enav.services.s124.views.AffectedChartPublication;
import dk.dma.enav.services.s124.views.Area;
import dk.dma.enav.services.s124.views.NWPreamble;
import org.geotools.xml.AbstractComplexBinding;
import org.geotools.xml.ElementInstance;
import org.geotools.xml.Node;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public class NwPreambleTypeBinding extends AbstractComplexBinding {
    public static final QName Q_NAME =
            new QName("http://www.iho.int/S124/gml/cs0/0.1", "NWPreambleType");

    @Override
    public QName getTarget() {
        return Q_NAME;
    }

    @Override
    public Class getType() {
        return NWPreamble.class;
    }

    @SuppressWarnings("RedundantThrows")
    @Override
    public Object parse(ElementInstance instance, Node node, Object value) throws Exception {
        NWPreamble nwPreamble = new NWPreamble();
        String id = (String) node.getAttribute("id").getValue();
        nwPreamble.setId(id);

        Map<String, List<Map<String, Object>>> others = new HashMap<>();
        node.getChildren().forEach(c -> {
            Node child = (Node) c;

            String name = child.getComponent().getName();
            Object childValue = child.getValue();
            switch (name) {
                case "generalArea":
                    nwPreamble.addGeneralArea((Area) childValue);
                    break;
                case "locality":
                    nwPreamble.addLocality((Area) childValue);
                    break;
                case "messageSeriesIdentifier":
                    nwPreamble.setMessageSeriesIdentifier((Map<String, Object>) childValue);
                    break;
                case "publicationDate":
                    nwPreamble.setPublicationDate((Date) childValue);
                    break;
                case "cancellationDate":
                    nwPreamble.setCancellationDate((Date) childValue);
                    break;
                case "affectedChartPublications":
                    nwPreamble.addAffectedChartPublications((AffectedChartPublication) childValue);
                    break;
                default:
                    others.computeIfAbsent(name, k -> new ArrayList<>()).add(S124ParseUtil.parseNode(child));
                    break;
            }
        });

        nwPreamble.setOthers(others);
        return nwPreamble;
    }
}
