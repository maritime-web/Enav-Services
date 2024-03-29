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

import javax.xml.namespace.QName;

@SuppressWarnings("unchecked")
public class LocalityBinding extends LocationBinding {
    public static final QName Q_NAME =
            new QName("http://www.iho.int/S124/gml/cs0/0.1", "localityType");

    @Override
    public QName getTarget() {
        return Q_NAME;
    }

    @Override
    public Class getType() {
        return Area.class;
    }
}
