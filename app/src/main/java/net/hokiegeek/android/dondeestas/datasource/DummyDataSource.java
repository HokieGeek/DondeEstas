package net.hokiegeek.android.dondeestas.datasource;

import net.hokiegeek.android.dondeestas.data.Person;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by andres on 11/23/16.
 */

public class DummyDataSource implements DataSource {

    private Map<Integer, Person> people;

    private List<DataSourceListener> listeners;

    private DummyDataSource() {
        listeners = new ArrayList<>();
    }

    public DummyDataSource newInstance() {
        return new DummyDataSource();
    }

    public void updatePeople(List<Person> newPeople) {
        boolean updated = false;
        for (Person p : newPeople) {
            if (p != null) {
                people.put(p.id, p);
                updated = true;
            }
        }
        if (updated) {
            fireOnDataSourceUpdate();
        }
    }

    public void addListener(DataSourceListener l) {
        synchronized (listeners) {
            if (!listeners.contains(l)) {
                listeners.add(l);
            }
        }
    }

    public void removeListener(DataSourceListener l) {
        synchronized (listeners) {
            if (listeners.contains(l)) {
                listeners.remove(l);
            }
        }
    }

    private void fireOnDataSourceUpdate() {
        for (DataSourceListener l : listeners) {
            l.onDataSourceUpdate();
        }
    }

    @Override
    public List<Person> getPeopleById(List<Integer> ids) {
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
