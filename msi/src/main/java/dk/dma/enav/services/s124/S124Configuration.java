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

import lombok.extern.slf4j.Slf4j;
import org.geotools.gml3.v3_2.GMLConfiguration;
import org.geotools.xml.Configuration;
import org.geotools.xml.XSD;
import org.geotools.xs.XSConfiguration;

@Slf4j
public class S124Configuration extends Configuration {
    /**
     * Creates a new configuration.
     *
     * <p>Any dependent schemas should be added in subclass constructor. The xml schema dependency
     * does not have to be added.
     *
     * @param xsd
     */
    public S124Configuration(XSD xsd) {
        super(xsd);
        log.info(GMLConfiguration.class.getPackage().toString());
        log.info(GMLConfiguration.class.toString());
        addDependency(new GMLConfiguration(true));
        addDependency(new XSConfiguration());

    }
}
