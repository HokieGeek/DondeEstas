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

    private Map<String, Person> people;

    private DummyDataSource() {
        people = new HashMap<>();

        List<Person> people = new ArrayList<>();
        people.add(new PersonBuilder()
                .id("One")
                .name("Keri")
                .position(new Date(), 39.189658, -77.279528, 0.0)
                .build());

        people.add(new PersonBuilder()
                .id("Two")
                .name("Olivia")
                .position(new Date(), 39.1888622, -77.287454, 0.0)
                .build());

        this.updatePeople(people);
    }

    public static DummyDataSource newInstance() {
        return new DummyDataSource();
    }

    public void updatePeople(List<Person> newPeople) {
        if (newPeople != null) {
            for (Person p : newPeople) {
                if (p != null) {
                    people.put(p.getId(), p);
                }
            }
        }
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
    public boolean updatePerson(Person p) {
        // TODO: ??
        return false;
    }
}
