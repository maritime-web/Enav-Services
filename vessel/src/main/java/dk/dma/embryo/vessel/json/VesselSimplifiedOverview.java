package dk.dma.embryo.vessel.json;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Created by andreas on 3/9/16.
 */
public class VesselSimplifiedOverview {

        private String type;
        private Double x;
        private Double y;



    // //////////////////////////////////////////////////////////////////////
    // Object methods
    // //////////////////////////////////////////////////////////////////////
    @Override
    public int hashCode() {
        int result = x != null ? x.hashCode() : 0;
        result = 31 * result + (y != null ? y.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "VesselSimplifiedOverview{" +
                ", x=" + x +
                ", y=" + y +
                ", type='" + type + '\'' +
                '}';
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }
}