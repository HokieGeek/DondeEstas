package net.hokiegeek.android.dondeestas.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by andres on 11/23/16.
 */

public class Model {

    private Map<Integer, Person> people;

    private List<UpdateListener> listeners;

    private Model() {
        people = new HashMap<>();
        listeners = new ArrayList<>();
    }

    public Model newInstance() {
        return new Model();
    }

    public List<Person> getPeople() {
        return null;
        // return people.entrySet();
    }

    public Person getPerson(Integer id) {
        if (!people.containsKey(id)) {

        }
    }

    void addListener(UpdateListener l) {
       if (!listeners.contains(l)) {
           listeners.add(l);
       }
    }

    boolean removeListener(UpdateListener l) {
        if (listeners.contains(l)) {
            listeners.remove(l);
            return true;
        }
        return false;
    }

    public interface UpdateListener {
        void onModelUpdate();
    }
}
