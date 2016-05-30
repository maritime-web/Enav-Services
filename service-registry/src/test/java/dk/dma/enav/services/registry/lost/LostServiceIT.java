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
package dk.dma.enav.services.registry.lost;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.WKTWriter;
import dk.dma.embryo.common.configuration.LogConfiguration;
import dk.dma.embryo.common.configuration.PropertyFileService;
import dk.dma.enav.services.registry.ServiceInstanceMetadata;
import dk.dma.enav.services.registry.ServiceLookupService;
import org.jglue.cdiunit.AdditionalClasses;
import org.jglue.cdiunit.CdiRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.List;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

/**
 * Created by Steen on 04-05-2016.
 *
 */
@RunWith(CdiRunner.class)
@AdditionalClasses(value = {LogConfiguration.class, LostService.class, PropertyFileService.class, JaxbAdapter.class, DomUtil.class, LocationFactory.class, Geodetic2DFactory.class})
public class LostServiceIT {

    @Inject
    private ServiceLookupService cut;

    @Test
    public void shouldFindAtleastOneService() throws Exception {
        List<ServiceInstanceMetadata> services = cut.getServiceInstancesForService("urn:mrnx:mcl:service:dma:nw-nm:rest", 55D, 11D);

        assertThat(services, is(not(empty())));

        System.out.println(services);
    }

    @Test
    public void shouldFindAtleastOneServiceWkt() throws Exception {
        GeometryFactory fac = new GeometryFactory();
        Polygon poly = fac.createPolygon(new Coordinate[]{new Coordinate(6D, 50D), new Coordinate(6D, 60D), new Coordinate(19D, 60D), new Coordinate(19D, 50D), new Coordinate(6D, 50D)});
        WKTWriter w = new WKTWriter();

        String location = w.write(poly);
        System.out.println(poly);

        List<ServiceInstanceMetadata> services = cut.getServiceInstancesForService("urn:mrnx:mcl:service:dma:nw-nm:rest", location);

        assertThat(services, is(not(empty())));

        System.out.println(services);
    }
}
