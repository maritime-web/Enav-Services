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
package dk.dma.enav.services.registry.mc;

import javax.xml.namespace.NamespaceContext;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Steen on 13-10-2016.
 *
 */
final class ServiceRegisterNamespaceContext implements NamespaceContext {
    private static Map<String, String> nsm;
    static {
        nsm = new HashMap<>();
        nsm.put("s", "http://efficiensea2.org/maritime-cloud/service-registry/v1/ServiceInstanceSchema.xsd");
        nsm.put("http://efficiensea2.org/maritime-cloud/service-registry/v1/ServiceInstanceSchema.xsd", "s");
    }

    private final Map<String, String> namespaceMap;

    private ServiceRegisterNamespaceContext(Map<String, String> namespaceMap) {
        this.namespaceMap = namespaceMap;
    }

    public static NamespaceContext getInstance() {
        return new ServiceRegisterNamespaceContext(nsm);
    }

    @Override
    public String getNamespaceURI(String prefix) {
        return namespaceMap.get(prefix);
    }

    @Override
    public String getPrefix(String namespaceURI) {
        return namespaceMap.get(namespaceURI);
    }

    @Override
    public Iterator getPrefixes(String namespaceURI) {
        List<String> list = Collections.singletonList(namespaceMap.get(namespaceURI));
        return list.iterator();
    }
}
