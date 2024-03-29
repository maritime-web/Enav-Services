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
package dk.dma.embryo.dataformats.persistence;

import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import dk.dma.embryo.common.persistence.DaoImpl;
import dk.dma.embryo.dataformats.model.ShapeFileMeasurement;

public class ShapeFileMeasurementDaoImpl extends DaoImpl implements ShapeFileMeasurementDao {
    public ShapeFileMeasurement lookup(String fn, String chartType, String provider) {
        TypedQuery<ShapeFileMeasurement> query = em.createNamedQuery("ShapeFileMeasurement:lookup", ShapeFileMeasurement.class);

        query.setParameter("fileName", fn);
        query.setParameter("provider", provider);
        query.setParameter("chartType", chartType);
        query.setMaxResults(1);

        List<ShapeFileMeasurement> result = query.getResultList();

        return getSingleOrNull(result);
    }

    public void deleteAll(String chartType, String provider) {
        Query query = em.createNamedQuery("ShapeFileMeasurement:deleteAll");
        query.setParameter("chartType", chartType);
        query.setParameter("provider", provider);
        query.executeUpdate();
    }

    public List<ShapeFileMeasurement> list(String chartType) {
        TypedQuery<ShapeFileMeasurement> query = em.createNamedQuery("ShapeFileMeasurement:list", ShapeFileMeasurement.class);

        query.setParameter("chartType", chartType);
        return query.getResultList();
    }

    public List<ShapeFileMeasurement> list(String chartType, String provider) {
        TypedQuery<ShapeFileMeasurement> query = em.createNamedQuery("ShapeFileMeasurement:listByProvider", ShapeFileMeasurement.class);

        query.setParameter("chartType", chartType);
        query.setParameter("provider", provider);

        return query.getResultList();
    }

}
