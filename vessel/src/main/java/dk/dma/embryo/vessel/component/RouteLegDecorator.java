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
package dk.dma.embryo.vessel.component;

import dk.dma.embryo.common.util.Converter;
import dk.dma.enav.model.geometry.Position;

public class RouteLegDecorator {
    private WaypointDecorator from, to;
    private dk.dma.enav.model.voyage.RouteLeg routeLeg;

    RouteLegDecorator(dk.dma.enav.model.voyage.RouteLeg routeLeg, WaypointDecorator from) {
        super();
        this.from = from;
        this.routeLeg = routeLeg;
    }


    // //////////////////////////////////////////////////////////////////////
    // Logic
    // //////////////////////////////////////////////////////////////////////
    double calcRange() {
        double meters;

        Position pos1 = Position.create(from.getLatitude(), from.getLongitude());
        Position pos2 = Position.create(to.getLatitude(), to.getLongitude());

        if (getHeading() == dk.dma.enav.model.voyage.RouteLeg.Heading.RL) {
            meters = pos1.rhumbLineDistanceTo(pos2);
        } else {
            meters = pos1.geodesicDistanceTo(pos2);
        }
        return Converter.metersToNm(meters);
    }

    // todo
    long calcTtg() {
        if (getSpeed() < 0.1) {
            return -1L;
        }
        return Math.round(calcRange() * 3600.0 / getSpeed() * 1000.0);
    }


    // //////////////////////////////////////////////////////////////////////
    // Inner class Property methods
    // //////////////////////////////////////////////////////////////////////
    void setTo(WaypointDecorator to) {
        this.to = to;
    }

    public Double getSFLen() {
        return routeLeg.getSFLen();
    }

    public Double getSFWidth() {
        return routeLeg.getSFWidth();
    }

    public Double getSpeed() {
        return routeLeg.getSpeed();
    }

    public dk.dma.enav.model.voyage.RouteLeg.Heading getHeading() {
        return routeLeg.getHeading();
    }

    public Double getXtdPort() {
        return routeLeg.getXtdPort();
    }

    public Double getXtdStarboard() {
        return routeLeg.getXtdStarboard();
    }

    public void setSFLen(Double sFLen) {
        routeLeg.setSFLen(sFLen);
    }

    public void setSFWidth(Double sFWidth) {
        routeLeg.setSFWidth(sFWidth);
    }

    public void setSpeed(Double speed) {
        routeLeg.setSpeed(speed);
    }

    public void setXtdPort(Double xtdPort) {
        routeLeg.setXtdPort(xtdPort);
    }

    public void setXtdStarboard(Double xtdStarboard) {
        routeLeg.setXtdStarboard(xtdStarboard);
    }

    public void setHeading(dk.dma.enav.model.voyage.RouteLeg.Heading heading) {
        routeLeg.setHeading(heading);
    }
}
