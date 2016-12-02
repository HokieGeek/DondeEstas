package net.hokiegeek.android.dondeestas.data;

import android.util.Log;

import java.util.List;

/**
 * Created by andres on 12/01/16.
 */
public class User {
    private static final String TAG = "DONDE";

    private Person person;

    private Model model;

    public User(Person p, Model m) {
        person = p;
        model = m;
    }

    private void setPerson(Person p) {
        person = p;
        model.updatePerson(person);
    }

    public String getId() {
        return person.getId();
    }

    public String getName() {
        return person.getName();
    }

    public Position getPosition() {
        return person.getPosition();
    }

    public Boolean getVisible() {
        return person.getVisible();
    }

    public List<String> getWhitelist() {
        return person.getWhitelist();
    }

    public List<String> getFollowing() {
        return person.getFollowing();
    }

    public void setId(String id) {
        PersonBuilder params = person.params.clone();
        params.id(id);
        setPerson(params.build());
    }

    public void setName(String n) {
        PersonBuilder params = person.params.clone();
        params.name(n);
        setPerson(params.build());
    }

    public void setPosition(Position p) {
        Log.v(TAG, "TODO: updatePosition");
        PersonBuilder params = person.params.clone();
        params.position(p);
        setPerson(params.build());
    }

    public void setVisible(Boolean v) {
        PersonBuilder params = person.params.clone();
        params.visible(v);
        setPerson(params.build());
    }

    public void setFollowing(List<String> ids) {
        PersonBuilder params = person.params.clone();
        params.following(ids);
        setPerson(params.build());
    }

    public void addFollowing(String id) {
        PersonBuilder params = person.params.clone();
        params.following(id);
        setPerson(params.build());
    }

    public void setWhitelist(List<String> ids) {
        PersonBuilder params = person.params.clone();
        params.whitelist(ids);
        setPerson(params.build());
    }

    public void addWhitelist(String id) {
        PersonBuilder params = person.params.clone();
        params.whitelist(id);
        setPerson(params.build());
    }
}
