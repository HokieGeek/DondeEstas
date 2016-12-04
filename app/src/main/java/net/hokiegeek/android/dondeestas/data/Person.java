package net.hokiegeek.android.dondeestas.data;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by andres on 11/23/16.
 */
public class Person {
    protected final PersonBuilder params;

    public Person(PersonBuilder params) {
        this.params = params;
    }

    public Person clone() { return this.params.clone().build(); }

    public String getId() {
        return params.id;
    }

    public String getName() {
        return params.name;
    }

    public Position getPosition() {
        return params.position;
    }

    public Boolean getVisible() {
        return params.visible;
    }

    public List<String> getWhitelist() {
        return params.whitelist;
    }

    public List<String> getFollowing() { return params.following; }

    @Override
    public String toString() {
        return "TODO"; // TODO: implement toString()
    }

    @Override
    public boolean equals(Object o) {
        return true; // TODO: implement equals()
    }

    @Override
    public int hashCode() {
        return 41; // TODO: implement hashCode
    }
}
