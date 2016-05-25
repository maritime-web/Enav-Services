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

import ietf.lost1.Location;
import ietf.lost1.ObjectFactory;
import org.w3c.dom.Element;

import javax.inject.Inject;

/**
 * Created by Steen on 25-05-2016.
 *
 */
class LocationFactory {
    private final Geodetic2DFactory geodetic2DFactory;

    @Inject
    LocationFactory(Geodetic2DFactory geodetic2DFactory) {
        this.geodetic2DFactory = geodetic2DFactory;
    }

    Location createLocation(double p1, double p2) {
        ObjectFactory objectFactory = new ObjectFactory();
        Location location = objectFactory.createLocation();
        Element point = geodetic2DFactory.createPoint(p1, p2);
        location.setId(String.valueOf(point.hashCode()));
        location.setProfile("geodetic-2d");
        location.getExtensionPoint().add(point);
        return location;
    }

}
