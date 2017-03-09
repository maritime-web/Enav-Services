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
package dk.dma.embryo.msi;

import dk.frv.msiedit.core.webservice.message.MsiDto;
import dk.frv.msiedit.core.webservice.message.PointDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public interface MsiClient {
    List<MsiItem> getActiveWarnings(List<String> regions);
    List<Region> getRegions();

    enum Type {Point, Polygon, Polyline, Points, General}

    @Getter
    @EqualsAndHashCode
    class Point {
        private final double longitude;
        private final double latitude;

        public Point(PointDto pointDto) {
            this.longitude = pointDto.getLongitude();
            this.latitude = pointDto.getLatitude();
        }

        public String toString() {
            return "(Latitude: " + getLatitude() + " Longitude: " + getLongitude() + ")";
        }
    }

    @Data
    class Region {
        private String name;
        private String description;
    }

    @Getter
    @ToString
    @EqualsAndHashCode
    class MsiItem {
        //private MsiDto md;

        private Type type;
        private Date created;
        private String eNCText;
        private String mainArea;
        private String subArea;
        private String navtexNo;
        private String text;
        private Date updated;
        private List<Point> points;

        public MsiItem(MsiDto msiDto) {
            this.type = getType(msiDto);
            this.created = msiDto.getCreated().toGregorianCalendar().getTime();
            this.eNCText = msiDto.getEncText();
            this.mainArea = msiDto.getAreaEnglish();
            this.subArea = msiDto.getSubarea();
            this.navtexNo = msiDto.getNavtexNo();
            this.text = msiDto.getNavWarning();
            this.updated = msiDto.getUpdated().toGregorianCalendar().getTime();
            this.points = getPoints(msiDto);
        }

        private Type getType(MsiDto msiDto) {
            if (msiDto.getPoints() == null || msiDto.getPoints().getPoint().size() == 0) {
                return Type.General;
            }
            return Type.valueOf(msiDto.getLocationType());
        }

        private List<Point> getPoints(MsiDto msiDto) {
            List<Point> result = new ArrayList<>();
            for (PointDto pd : msiDto.getPoints().getPoint()) {
                result.add(new Point(pd));
            }
            return result;
        }

        // todo this getter does not follow the normal naming convention, but since this class is used in a service response I don't want to change it
        public String getENCtext() {
            return eNCText;
        }
    }
}
