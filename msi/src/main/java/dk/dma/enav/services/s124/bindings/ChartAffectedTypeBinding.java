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

import dk.dma.enav.services.s124.views.ChartAffected;
import org.geotools.xml.AbstractComplexBinding;
import org.geotools.xml.ElementInstance;
import org.geotools.xml.Node;

import javax.xml.namespace.QName;
import java.util.Date;

@SuppressWarnings("unchecked")
public class ChartAffectedTypeBinding extends AbstractComplexBinding {
    public static final QName Q_NAME =
            new QName("http://www.iho.int/S124/gml/cs0/0.1", "chartAffectedType");

    @Override
    public QName getTarget() {
        return Q_NAME;
    }

    @Override
    public Class getType() {
        return ChartAffected.class;
    }

    @SuppressWarnings("RedundantThrows")
    @Override
    public Object parse(ElementInstance instance, Node node, Object value) throws Exception {
        ChartAffected res = new ChartAffected();

        node.getChildren().forEach(c -> {
            Node child = (Node) c;
            String name = child.getComponent().getName();
            Object childValue = child.getValue();

            switch (name) {
                case "chartNumber":
                    res.setChartNumber((String) childValue);
                    break;
                case "editionDate":
                    res.setEditionDate((Date)childValue);
                    break;
                case "lastNoticeDate":
                    res.setLastNoticeDate((Date)childValue);

                    break;
                case "publicationAffected":
                    res.setPublicationAffected((String) childValue);
                    break;
            }
        });

        return res;
    }
}
