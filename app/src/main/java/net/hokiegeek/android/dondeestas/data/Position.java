package net.hokiegeek.android.dondeestas.data;

import java.util.Date;

/**
 * Created by andres on 11/23/16.
 */

public class Position {
    public final double latitude;
    public final double longitude;
    public final double elevation;
    public final Date tov;

    public Position(Date tov, double latitude, double longitude) {
        this(tov, latitude, longitude, 0.0);
    }

    public Position(Date tov, double latitude, double longitude, double elevation) {
        this.tov = (Date)tov.clone();
        this.latitude = latitude;
        this.longitude = longitude;
        this.elevation = elevation;
    }

    public Position clone() {
        return new Position(this.tov, this.latitude, this.longitude, this.elevation);
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(this.latitude);
        s.append(",");
        s.append(this.longitude);
        s.append(",");
        s.append(this.elevation);
        return s.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Position) {
            Position other = (Position)o;
            return this.latitude == other.latitude &&
                   this.longitude == other.longitude &&
                   this.elevation == other.elevation &&
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
