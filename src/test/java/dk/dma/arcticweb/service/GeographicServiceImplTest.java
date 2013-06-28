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
package dk.dma.arcticweb.service;

import static org.junit.Assert.fail;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import dk.dma.arcticweb.dao.GeographicDaoImpl;
import dk.dma.embryo.domain.Berth;

public class GeographicServiceImplTest {

    private static EntityManagerFactory factory;
    private EntityManager entityManager;
    
    private GeographicService geoService;

    @BeforeClass
    public static void setupForAll() {
        factory = Persistence.createEntityManagerFactory("componentTest");

        EntityManager entityManager = factory.createEntityManager();
        entityManager.getTransaction().begin();

        // Test data found on
        // http://gronlandskehavnelods.dk/#HID=78
        entityManager.persist(new Berth("Nuuk", "64 10.4N", "051 43.5W"));
        entityManager.persist(new Berth("Sisimiut", "Holsteinsborg", "66 56.5N", "053 40.5W"));
        entityManager.persist(new Berth("Danmarkshavn", "76 46.0N", "018 45.0W"));
        entityManager.persist(new Berth("Kangilinnguit", "Grønnedal", "61 14.3N", "48 06.1W"));
        entityManager.persist(new Berth("Aasiaat", "Egedesminde", "68 42.6N", "052 53.0W"));
        entityManager.persist(new Berth("Ilulissat", "Jakobshavn", "69 13.5N", "051 06.0W"));
        entityManager.persist(new Berth("Qeqertarsuaq", "Godhavn", "69 15.0N", "053 33.0W"));
        entityManager.persist(new Berth("Ammassivik", "Sletten", "60 35.8N", "045 23.7W"));
        entityManager.persist(new Berth("Ittaajimmiut", "Kap Hope", "70 27.5N", "022 22.0W"));
        entityManager.persist(new Berth("Kangersuatsiaq", "Prøven", "72 22.7N","055 33.5W"));

        entityManager.getTransaction().commit();
        entityManager.close();
    }
    
    @Before
    public void setup() {
        entityManager = factory.createEntityManager();
        geoService = new GeographicServiceImpl(new GeographicDaoImpl(entityManager));
        entityManager.clear();
    }

    @After
    public void tearDown() {
        entityManager.close();
    }

    @Test
    public void testFindBerths_byFullName() {
        List<Berth> result = geoService.findBerths("Nuuk");
        
        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());
        
        Assert.assertEquals("Nuuk", result.get(0).getName());
        Assert.assertNull(result.get(0).getAlias());
        Assert.assertEquals("64 10.400N", result.get(0).getPosition().asGeometryPosition().getLatitudeAsString());
        Assert.assertEquals("051 43.500W", result.get(0).getPosition().asGeometryPosition().getLongitudeAsString());
    }

    @Test
    public void testFindBerths_byEmptyQuery() {
        List<Berth> result = geoService.findBerths("");
        
        Assert.assertNotNull(result);
        Assert.assertEquals(10, result.size());
    }

}
