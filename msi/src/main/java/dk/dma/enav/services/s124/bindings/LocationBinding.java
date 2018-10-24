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

import dk.dma.enav.services.s124.views.Area;
import dk.dma.enav.services.s124.views.LocationName;
import org.geotools.xml.AbstractComplexBinding;
import org.geotools.xml.ElementInstance;
import org.geotools.xml.Node;

import javax.xml.namespace.QName;

@SuppressWarnings("unchecked")
public abstract class LocationBinding extends AbstractComplexBinding {

    @SuppressWarnings("RedundantThrows")
    @Override
    public Object parse(ElementInstance instance, Node node, Object value) throws Exception {
        Area res = new Area();

        node.getChildren().forEach(c -> {
            Node child = (Node) c;
            String name = child.getComponent().getName();

            if (name.equals("ocalityIdentifier")) {
                res.setId((String)child.getValue());
            } else if (name.equals("locationName")) {
                res.addName((LocationName)child.getValue());
            }
        });

        return res;
    }
}
