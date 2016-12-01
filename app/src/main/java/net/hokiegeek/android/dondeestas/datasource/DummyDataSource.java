package net.hokiegeek.android.dondeestas.datasource;

import net.hokiegeek.android.dondeestas.data.Person;
import net.hokiegeek.android.dondeestas.data.PersonBuilder;

import java.util.ArrayList;
import java.util.Date;
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

        List<Person> people = new ArrayList<>();
        people.add(new PersonBuilder()
                .id(1)
                .name("Keri")
                .position(new Date(), 39.189658, -77.279528, 0.0)
                .build());

        people.add(new PersonBuilder()
                .id(2)
                .name("Olivia")
                .position(new Date(), 39.1888622, -77.287454, 0.0)
                .build());

        this.updatePeople(people);
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

        /*
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        List<Person> people = new ArrayList<>();
                        people.add(new PersonBuilder()
                                .id(0)
                                .name("Andres")
                                .position(38.975095, -77.195674, 0.0)
                                .build());

                        updatePeople(people);
                    }
                },
                20000
        );
        */
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
    public List<Person> getPeopleByIdList(List<String> ids) {
        List<Person> l = new ArrayList<>();
        synchronized (people) {
            for (String id : ids) {
                if (people.containsKey(id)) {
                    l.add(people.get(id));
                }
            }
        }
        return l;
    }

    @Override
    public Person getPersonById(String id) {
        synchronized (people) {
            if (!people.containsKey(id)) {
                return people.get(id);
            }
        }
        return null;
    }

    @Override
    public boolean updatePerson(p Person) {
        return false;
    }
}
