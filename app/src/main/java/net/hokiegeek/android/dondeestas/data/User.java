package net.hokiegeek.android.dondeestas.data;

/**
 * Created by andres on 12/01/16.
 */
public class User {
    private static final String TAG = "DONDE";

    private Person person;

    public User(Person p) {
        person = p;
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
        person = params.build();
    }

    public void setName(String n) {
        PersonBuilder params = person.params.clone();
        params.name(n);
        person = params.build();
    }

    public void setPosition(Position p) {
        Log.v(TAG, "TODO: updatePosition");
        PersonBuilder params = person.params.clone();
        params.position(p);
        person = params.build();
    }

    public void setVisible(Boolean v) {
        PersonBuilder params = person.params.clone();
        params.position(v);
        person = params.build();
    }

    public void setFollowing(List<String> ids) {
        PersonBuilder params = person.params.clone();
        params.following(ids);
        person = params.build();
    }

    public void addFollowing(String id) {
        PersonBuilder params = person.params.clone();
        params.following(id);
        person = params.build();
    }

    public void setWhitelist(List<String> ids) {
        PersonBuilder params = person.params.clone();
        params.whitelist(ids);
        person = params.build();
    }

    public void addWhitelist(String id) {
        PersonBuilder params = person.params.clone();
        params.whitelist(id);
        person = params.build();
    }
}
