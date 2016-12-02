package net.hokiegeek.android.dondeestas.datasource;

import net.hokiegeek.android.dondeestas.data.Person;

import java.util.List;

/**
 * Created by andres on 11/23/16.
 */

public interface DataSource {
    List<Person> getPeople();

    List<Person> getPeopleByIdList(List<String> ids);

    Person getPersonById(String id);

    boolean updatePerson(Person p);

    void addListener(DataUpdateListener l);

    boolean removeListener(DataUpdateListener l);
}
