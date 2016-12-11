package net.hokiegeek.android.dondeestas.data;

import java.util.Date;

/**
 * Created by andres on 11/23/16.
 */

public class Position implements Cloneable {
    public final Double latitude;
    public final Double longitude;
    public final Double elevation;
    public final Date tov;

    public Position(Date tov, Double latitude, Double longitude) {
        this(tov, latitude, longitude, 0.0);
    }

    public Position(Date tov, Double latitude, Double longitude, Double elevation) {
        this.tov = (Date)tov.clone();
        this.latitude = latitude;
        this.longitude = longitude;
        this.elevation = elevation;
    }

    @Override
    public Position clone() {
        Position p;
        try {
            p = (Position)super.clone();
            return p;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
        // return new Position((Date)this.tov.clone(), this.latitude, this.longitude, this.elevation);
    }

    @Override
    public String toString() {
        String s = "";
        s += this.latitude + ",";
        s += this.longitude + ",";
        s += this.elevation;
        return s;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Position) {
            Position other = (Position)o;
            return this.latitude.equals(other.latitude) &&
                   this.longitude.equals(other.longitude) &&
                   this.elevation.equals(other.elevation) &&
                   this.tov.equals(other.tov);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int result = 37;

        long dummyLong = Double.doubleToLongBits(this.latitude);
        result = 42 * result + (int)(dummyLong ^ (dummyLong >>> 32));
        dummyLong = Double.doubleToLongBits(this.longitude);
        result = 42 * result + (int)(dummyLong ^ (dummyLong >>> 32));
        dummyLong = Double.doubleToLongBits(this.elevation);
        result = 42 * result + (int)(dummyLong ^ (dummyLong >>> 32));
        dummyLong = this.tov.getTime();
        result = 42 * result + (int)(dummyLong ^ (dummyLong >>> 32));

        return result;
    }
}
