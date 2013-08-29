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
package dk.dma.embryo.domain;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import dk.dma.embryo.rest.json.GreenPos;

/**
 * 
 * @author Jesper Tejlgaard
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class GreenPosReport extends BaseEntity<Long> {

    private static final long serialVersionUID = -7205030526506222850L;

    // //////////////////////////////////////////////////////////////////////
    // Entity fields (also see super class)
    // //////////////////////////////////////////////////////////////////////
    @NotNull
    private String enavId;

    @NotNull
    private String shipName;

    private Long shipMmsi;

    private String shipCallSign;

    private String shipMaritimeId;

    @Valid
    private Position position;

    @NotNull
    private String reportedBy;

    // //////////////////////////////////////////////////////////////////////
    // Utility methods
    // //////////////////////////////////////////////////////////////////////

    public static GreenPosReport from(GreenPos from) {
        switch (from.getReportType()) {
        case "SP":
            return GreenPosSailingPlanReport.fromJsonModel(from);
        case "PR":
            return GreenPosPositionReport.fromJsonModel(from);
        case "FR":
            return GreenPosFinalReport.fromJsonModel(from);
        case "DR":
            return GreenPosDeviationReport.fromJsonModel(from);
        default:
            throw new IllegalArgumentException("Unknown value '" + from.getReportType() + "' for reportType");
        }
    }

    //
    // public dk.dma.enav.model.voyage.Route toEnavModel() {
    // dk.dma.enav.model.voyage.Route toRoute = new dk.dma.enav.model.voyage.Route(this.enavId);
    // toRoute.setName(this.name);
    // toRoute.setDeparture(this.origin);
    // toRoute.setDestination(this.destination);
    //
    // for (WayPoint wp : this.getWayPoints()) {
    // toRoute.getWaypoints().add(wp.toEnavModel());
    // }
    //
    // return toRoute;
    // }

    // //////////////////////////////////////////////////////////////////////
    // Constructors
    // //////////////////////////////////////////////////////////////////////
    public GreenPosReport() {
        this.enavId = UUID.randomUUID().toString();
    }

    public GreenPosReport(String shipName, Long shipMmsi, String shipCallSign, String shipMaritimeId, String latitude,
            String longitude) {
        this();
        this.shipName = shipName;
        this.shipMmsi = shipMmsi;
        this.shipCallSign = shipCallSign;
        this.shipMaritimeId = shipMaritimeId;
        this.position = new Position(latitude, longitude);
    }

    public GreenPosReport(String shipName, Long shipMmsi, String shipCallSign, String shipMaritimeId,
            Position position) {
        this();
        this.shipName = shipName;
        this.shipMmsi = shipMmsi;
        this.shipCallSign = shipCallSign;
        this.shipMaritimeId = shipMaritimeId;
        this.position = position;
    }

    // //////////////////////////////////////////////////////////////////////
    // Object methods
    // //////////////////////////////////////////////////////////////////////
    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }

    // //////////////////////////////////////////////////////////////////////
    // Property methods
    // //////////////////////////////////////////////////////////////////////
    public String getShipName() {
        return shipName;
    }

    public String getShipMaritimeId() {
        return shipMaritimeId;
    }

    public String getEnavId() {
        return enavId;
    }

    public String getShipCallSign() {
        return shipCallSign;
    }

    public Long getShipMmsi() {
        return shipMmsi;
    }

    public Position getPosition() {
        return position;
    }

    public void setReportedBy(String userName) {
        this.reportedBy = userName;
    }

}