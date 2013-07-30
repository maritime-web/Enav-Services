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
package dk.dma.embryo.domain.transformers;


import org.junit.Assert;
import org.junit.Test;

import dk.dma.embryo.domain.WayPoint;
import dk.dma.enav.model.voyage.Route;
import dk.dma.enav.model.voyage.RouteLeg;
import dk.dma.enav.model.voyage.RouteLeg.Heading;
import dk.dma.enav.model.voyage.Waypoint;

public class RouteTransformerTest {

    @Test
    public void testTransformRoute() {
        
        // DATA
        Route route = new Route("test route", "departure", "destination");
        
        Waypoint wp1 = new Waypoint("wp1", 55.5, 88.8, 0.5, 1.0);
        wp1.setRouteLeg(new RouteLeg(10.0, Heading.GC, 0.5, 0.7));
        route.getWaypoints().add(wp1);

        Waypoint wp2 = new Waypoint("wp2", 65.5, 44.4, 0.2, 0.5);
        wp2.setRouteLeg(new RouteLeg(20.0, Heading.RL, 0.4, 1.0));
        route.getWaypoints().add(wp2);
        
        //Execute
        dk.dma.embryo.domain.Route transformed = new RouteTransformer().transform(route);
        
        Assert.assertEquals("test route", transformed.getName());
        Assert.assertEquals("departure", transformed.getOrigin());
        Assert.assertEquals("destination", transformed.getDestination());

        Assert.assertEquals(2, transformed.getWayPoints().size());

        WayPoint wpTrans = transformed.getWayPoints().get(0); 
        Assert.assertEquals("wp1", wpTrans.getName());
        Assert.assertEquals(55.5, wpTrans.getPosition().getLatitude(), 0.0);
        Assert.assertEquals(88.8, wpTrans.getPosition().getLongitude(), 0.0);
        Assert.assertEquals(0.5, wpTrans.getRot(), 0.0);
        Assert.assertEquals(1.0, wpTrans.getTurnRadius(), 0.0);

        Assert.assertEquals(10.0, wpTrans.getLeg().getSpeed(), 0.0);
        Assert.assertEquals(0.5, wpTrans.getLeg().getXtdPort(), 0.0);
        Assert.assertEquals(0.7, wpTrans.getLeg().getXtdStarboard(), 0.0);

        wpTrans = transformed.getWayPoints().get(1); 
        Assert.assertEquals("wp2", wpTrans.getName());
        Assert.assertEquals(65.5, wpTrans.getPosition().getLatitude(), 0.0);
        Assert.assertEquals(44.4, wpTrans.getPosition().getLongitude(), 0.0);
        Assert.assertEquals(0.2, wpTrans.getRot(), 0.0);
        Assert.assertEquals(0.5, wpTrans.getTurnRadius(), 0.0);

        Assert.assertEquals(20.0, wpTrans.getLeg().getSpeed(), 0.0);
        Assert.assertEquals(0.4, wpTrans.getLeg().getXtdPort(), 0.0);
        Assert.assertEquals(1.0, wpTrans.getLeg().getXtdStarboard(), 0.0);
}
}
