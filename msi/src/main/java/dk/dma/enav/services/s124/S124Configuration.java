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

import dk.dma.enav.services.s124.bindings.AffectedChartPublicationsTypeBinding;
import dk.dma.enav.services.s124.bindings.ChartAffectedTypeBinding;
import dk.dma.enav.services.s124.bindings.DataSetTypeBinding;
import dk.dma.enav.services.s124.bindings.GeneralAreaBinding;
import dk.dma.enav.services.s124.bindings.LocalityBinding;
import dk.dma.enav.services.s124.bindings.LocationNameBinding;
import dk.dma.enav.services.s124.bindings.NavigationalWarningFeaturePartTypeBinding;
import dk.dma.enav.services.s124.bindings.NwPreambleTypeBinding;
import dk.dma.enav.services.s124.bindings.ReferencesTypeBinding;
import lombok.extern.slf4j.Slf4j;
import org.geotools.factory.Hints;
import org.geotools.gml3.v3_2.GMLConfiguration;
import org.geotools.xml.Configuration;
import org.geotools.xml.XSD;
import org.geotools.xs.XSConfiguration;

import java.util.Map;

@SuppressWarnings("unchecked")
@Slf4j
public class S124Configuration extends Configuration {

    /**
     * Creates a new configuration.
     *
     * <p>Any dependent schemas should be added in subclass constructor. The xml schema dependency
     * does not have to be added.
     *
     * @param xsd the S-124 Schema
     */
    @SuppressWarnings("WeakerAccess")
    public S124Configuration(XSD xsd) {
        super(xsd);
        addDependency(new GMLConfiguration(true));
        addDependency(new XSConfiguration());
        Hints.putSystemDefault(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, true);

    }

    @Override
    protected void registerBindings(Map bindings) {
        super.registerBindings(bindings);
        bindings.put(DataSetTypeBinding.Q_NAME, DataSetTypeBinding.class);
        bindings.put(NwPreambleTypeBinding.Q_NAME, NwPreambleTypeBinding.class);
        bindings.put(ReferencesTypeBinding.Q_NAME, ReferencesTypeBinding.class);
        bindings.put(NavigationalWarningFeaturePartTypeBinding.Q_NAME, NavigationalWarningFeaturePartTypeBinding.class);
        bindings.put(LocationNameBinding.Q_NAME, LocationNameBinding.class);
        bindings.put(GeneralAreaBinding.Q_NAME, GeneralAreaBinding.class);
        bindings.put(LocalityBinding.Q_NAME, LocalityBinding.class);
        bindings.put(ChartAffectedTypeBinding.Q_NAME, ChartAffectedTypeBinding.class);
        bindings.put(AffectedChartPublicationsTypeBinding.Q_NAME, AffectedChartPublicationsTypeBinding.class);
    }
}
