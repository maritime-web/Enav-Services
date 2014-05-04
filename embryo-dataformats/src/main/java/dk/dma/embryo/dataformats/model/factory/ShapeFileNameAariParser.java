/* Copyright (c) 2011 Danish Maritime Authority
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library.  If not, see <http://www.gnu.org/licenses/>.
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
    public ShapeFileMeasurement parse(String name) {
        ShapeFileMeasurement measurement = new ShapeFileMeasurement();
        measurement.setFileName(name);
        measurement.setVersion(0);
        measurement.setProvider(getProvider());
        return measurement;
    }

    @Override
    public String getProvider() {
        return "aari";
    }

}