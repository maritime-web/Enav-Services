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

import org.w3c.dom.Element;

import javax.inject.Inject;
import java.util.Locale;

/**
 * Produces shapes according to
 * "GEOPRIV Presence Information Data Format Location Object (PIDF-LO)
 * Usage Clarification, Considerations, and Recommendations"
 * See <a href="https://tools.ietf.org/html/rfc5491">rfc5491</a>
 *
 */
class Geodetic2DFactory {
    private static final String POINT_TEMPLATE = "<p2:Point id=\"point1\" srsName=\"urn:ogc:def:crs:EPSG::4326\" xmlns:p2=\"http://www.opengis.net/gml\"><p2:pos>%1$1.16f %2$1.16f</p2:pos></p2:Point>";
    private final DomUtil domUtil;

    @Inject
    Geodetic2DFactory(DomUtil domUtil) {
        this.domUtil = domUtil;
    }

    Element createPoint(double p1, double p2) {
        return domUtil.createElement(String.format(Locale.ENGLISH, POINT_TEMPLATE, p1, p2));
    }
}
