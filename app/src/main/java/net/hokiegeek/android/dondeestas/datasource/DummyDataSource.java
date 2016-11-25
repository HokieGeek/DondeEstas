package net.hokiegeek.android.dondeestas.datasource;

import net.hokiegeek.android.dondeestas.data.Person;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by andres on 11/23/16.
 */

public class DummyDataSource implements DataSource {

    private Map<Integer, Person> people;

    private List<DataUpdateListener> listeners;

    private DummyDataSource() {
        people = new HashMap<>();
        listeners = new ArrayList<>();
    }

    public static DummyDataSource newInstance() {
        return new DummyDataSource();
    }

    public void updatePeople(List<Person> newPeople) {
        boolean updated = false;
        if (newPeople != null) {
            for (Person p : newPeople) {
                if (p != null) {
                    people.put(p.getId(), p);
                    updated = true;
                }
            }
        }
        if (updated) {
            fireOnDataSourceUpdate();
        }
    }

    @Override
    public void addListener(DataUpdateListener l) {
        synchronized (listeners) {
            if (!listeners.contains(l)) {
                listeners.add(l);
            }
        }
    }

    @Override
    public boolean removeListener(DataUpdateListener l) {
        synchronized (listeners) {
            if (listeners.contains(l)) {
                listeners.remove(l);
                return true;
            }
        }
        return false;
    }

    private void fireOnDataSourceUpdate() {
        for (DataUpdateListener l : listeners) {
            l.onDataUpdate();
        }
    }

    @Override
    public List<Person> getPeople() {
        return new ArrayList<>(people.values());
    }

    @Override
    public List<Person> getPeopleByIdList(List<Integer> ids) {
        List<Person> l = new ArrayList<>();
        synchronized (people) {
            for (Integer id : ids) {
                if (people.containsKey(id)) {
                    l.add(people.get(id));
                }
            }
        }
        return l;
    }

    @Override
    public Person getPersonById(Integer id) {
        synchronized (people) {
            if (!people.containsKey(id)) {
                return people.get(id);
            }
        }
        return null;
    }
}
