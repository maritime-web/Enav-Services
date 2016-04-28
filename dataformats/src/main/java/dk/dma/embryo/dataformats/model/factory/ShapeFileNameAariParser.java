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
package dk.dma.embryo.dataformats.model.factory;

import javax.inject.Named;

import dk.dma.embryo.dataformats.model.ShapeFileMeasurement;

/**
 * @author Jesper Tejlgaard
 */
@Named
public class ShapeFileNameAariParser implements ShapeFileNameParser {

    public ShapeFileNameAariParser() {
    }

    @Override
    public ShapeFileMeasurement parse(String chartType, String name) {
        ShapeFileMeasurement measurement = new ShapeFileMeasurement();
        measurement.setFileName(name);
        measurement.setVersion(0);
        measurement.setProvider(getProvider());
        measurement.setChartType(chartType);
        return measurement;
    }

    @Override
    public String getProvider() {
        return "aari";
    }

}