package net.hokiegeek.android.dondeestas.data;

import java.util.Date;

/**
 * Created by andres on 11/23/16.
 */

public class Person {
    public final int id;
    public final String name;
    public final Position position;
    public final boolean visible;
    public final Date tov;

    public Person(int id, String name, Position position, boolean visible) {
        this.id = id;
        this.name = name;
        this.position = position;
        this.visible = visible;
        this.tov = new Date(System.currentTimeMillis()); // TODO
    }

    @Override
    public String toString() {
        return "TODO"; // TODO
    }

    @Override
    public boolean equals(Object o) {
        return true; // TODO
    }

    @Override
    public int hashCode() {
        return 41; // TODO
    }
}
