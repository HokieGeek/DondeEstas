package net.hokiegeek.android.dondeestas.datasource;

import net.hokiegeek.android.dondeestas.data.Person;

import java.util.List;

/**
 * Created by andres on 11/23/16.
 */

public interface DataSource {
    List<Person> getPeopleById(List<Integer> ids);

    Person getPersonById(Integer id);
}
