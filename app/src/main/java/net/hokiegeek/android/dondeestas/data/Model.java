package net.hokiegeek.android.dondeestas.data;

import net.hokiegeek.android.dondeestas.datasource.DataSource;
import net.hokiegeek.android.dondeestas.datasource.DataUpdateListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andres on 11/23/16.
 */

public class Model implements DataSource, DataUpdateListener {
    private static final String TAG = "DONDE";

    private DataSource dataSource;
    private List<DataUpdateListener> listeners;

    public Model(DataSource dataSource) {
        this.listeners = new ArrayList<>();
        this.dataSource = dataSource;
        this.dataSource.addListener(this);
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

    @Override
    public List<Person> getPeople() {
        return dataSource.getPeople();
    }

    @Override
    public List<Person> getPeopleByIdList(List<String> ids) {
        return dataSource.getPeopleByIdList(ids);
    }

    @Override
    public Person getPersonById(String id) {
        return dataSource.getPersonById(id);
    }

    @Override
    public boolean updatePerson(p Person) {
        // TODO: implement
        return false;
    }

    @Override
    public void onDataUpdate() {
        for (DataUpdateListener l : listeners) {
            l.onDataUpdate();
        }
    }
}