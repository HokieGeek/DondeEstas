package net.hokiegeek.android.dondeestas.data;

import net.hokiegeek.android.dondeestas.datasource.DataSource;
import net.hokiegeek.android.dondeestas.datasource.DataSourceListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by andres on 11/23/16.
 */

public class Model implements DataSource, DataSourceListener {

    private DataSource dataSource;
    private List<UpdateListener> listeners;

    public Model(DataSource dataSource) {
        this.dataSource = dataSource;
        this.listeners = new ArrayList<>();
    }

    void addListener(UpdateListener l) {
        synchronized (listeners) {
            if (!listeners.contains(l)) {
                listeners.add(l);
            }
        }
    }

    boolean removeListener(UpdateListener l) {
        synchronized (listeners) {
            if (listeners.contains(l)) {
                listeners.remove(l);
                return true;
            }
        }
        return false;
    }

    @Override
    public List<Person> getPeopleById(List<Integer> ids) {
        return dataSource.getPeopleById(ids);
    }

    @Override
    public Person getPersonById(Integer id) {
        return dataSource.getPersonById(id);
    }

    @Override
    public void onDataSourceUpdate() {
        for (UpdateListener l : listeners) {
            l.onModelUpdate();
        }
    }

    public interface UpdateListener {
        void onModelUpdate();
    }
}