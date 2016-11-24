package net.hokiegeek.android.dondeestas.data;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by andres on 11/23/16.
 */
public class Person {
    public final int id;
    public final String name;
    public final Position position;
    public final Date tov;
    public final boolean visible;
    public final List<Integer> whitelist;

    public Person(int id, String name, Position position, Date tov, boolean visible, List<Integer> whitelist) {
        this.id = id;
        this.name = name;
        this.position = position;
        this.tov = (Date)tov.clone();
        this.visible = visible;
        // this.tov = new Date(System.currentTim        this.visible = visible;eMillis()); // TODO
        this.whitelist = whitelist;
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
