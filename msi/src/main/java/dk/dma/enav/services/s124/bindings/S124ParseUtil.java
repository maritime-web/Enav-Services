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

import org.apache.commons.lang3.StringUtils;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.xml.Node;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
class S124ParseUtil {
    static Map<String, Object> parseNode(Node node) {
        Object value = node.getValue();
        if (value instanceof Map) {
            return (Map<String, Object>) value;
        } else if (value == null || value instanceof String) {
            if (StringUtils.isAllBlank((CharSequence) value)) {
                //No content. Perhaps attributes
                Map<String, Object> val = new HashMap<>();
                node.getAttributes().forEach(a -> {
                    Node att = (Node) a;
                    val.put(att.getComponent().getName(), att.getValue());
                });
                if (!val.isEmpty()) {
                    return val;
                }
            } else {
                Map<String, Object> res = new HashMap<>();
                res.put("name", value);
                return res;
            }
        } else if (value instanceof ReferencedEnvelope) {
            return new HashMap<>();
        } else {
            throw new IllegalArgumentException("Can't handle values of type " + value.getClass().getName());
        }

        return new HashMap<>();
    }
}
