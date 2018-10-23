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

import dk.dma.enav.services.s124.views.DataSet;
import dk.dma.enav.services.s124.views.NWPreamble;
import dk.dma.enav.services.s124.views.NavigationalWarningFeaturePart;
import dk.dma.enav.services.s124.views.References;
import lombok.extern.slf4j.Slf4j;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.xml.AbstractComplexBinding;
import org.geotools.xml.ElementInstance;
import org.geotools.xml.Node;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
@Slf4j
public class DataSetTypeBinding extends AbstractComplexBinding {
    public static final QName Q_NAME =
            new QName("http://www.iho.int/S124/gml/cs0/0.1", "DatasetType");

    @Override
    public QName getTarget() {
        return Q_NAME;
    }

    @Override
    public Class getType() {
        return DataSet.class;
    }

    @SuppressWarnings("RedundantThrows")
    @Override
    public Object parse(ElementInstance instance, Node node, Object value) throws Exception {
        DataSet dataSet = new DataSet();

        String id = (String) node.getAttribute("id").getValue();
        dataSet.setId(id);

        Map<String, List<Map<String, Object>>> others = new HashMap<>();

        node.getChildren().forEach(c -> {
            Node child = (Node) c;
            Object childValue = child.getValue();

            if (childValue instanceof NWPreamble) {
                dataSet.setNWPreamble((NWPreamble)childValue);
            } else if (childValue instanceof References) {
                dataSet.add((References)childValue);
            } else if (childValue instanceof ReferencedEnvelope) {
                // not handled yet
            } else if (childValue instanceof NavigationalWarningFeaturePart) {
                dataSet.addNavigationalWarningFeaturePart((NavigationalWarningFeaturePart)childValue);
            } else {
                String name = child.getComponent().getName();
                others.computeIfAbsent(name, k -> new ArrayList<>()).add(S124ParseUtil.parseNode(child));
            }
        });

        dataSet.setOthers(others);
        dataSet.createViewAttributes();
        return dataSet;
    }
}
