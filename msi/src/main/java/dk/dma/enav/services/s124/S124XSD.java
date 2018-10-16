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

import org.geotools.gml3.v3_2.GML;

import java.util.Set;

public final class S124XSD extends org.geotools.gml3.ApplicationSchemaXSD {
    private static S124XSD instance;

    public static S124XSD getInstance() {
        if (instance == null) {
            instance = new S124XSD();
        }
        return instance;
    }

    private S124XSD() {
        super("http://www.iho.int/S124/gml/cs0/0.1", S124XSD.class.getClassLoader().getResource("schema/S124.xsd").toString());
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void addDependencies(Set dependencies) {
        String schemaLocation = getClass().getClassLoader().getResource("schema/S100_gmlProfile.xsd").toString();
        dependencies.add(new ApplicationSchemaXSD("http://www.opengis.net/gml/3.2", schemaLocation));
        schemaLocation = getClass().getClassLoader().getResource("schema/S100_gmlProfileLevels.xsd").toString();
        dependencies.add(new ApplicationSchemaXSD("http://www.iho.int/S-100/profile/s100_gmlProfile", schemaLocation));
        schemaLocation = getClass().getClassLoader().getResource("schema/s100gmlbase.xsd").toString();
        dependencies.add(new ApplicationSchemaXSD("http://www.iho.int/s100gml/1.0", schemaLocation));
        schemaLocation = getClass().getClassLoader().getResource("schema/xlink.xsd").toString();
        dependencies.add(new ApplicationSchemaXSD("http://www.w3.org/1999/xlink", schemaLocation));

        dependencies.add(GML.getInstance());
    }

}
