package net.hokiegeek.android.dondeestas.data;

import android.os.AsyncTask;
import android.util.Log;

import net.hokiegeek.android.dondeestas.datasource.DataSource;
import net.hokiegeek.android.dondeestas.datasource.DataUpdateListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andres on 11/23/16.
 */

public class Model {
    private static final String TAG = "DONDE";

    private Person user;
    private List<Person> whitelisted;
    private List<Person> following;

    private DataSource dataSource;
    private List<DataUpdateListener> listeners;

    public Model(DataSource dataSource, String userId) {
        this.whitelisted = new ArrayList<>();
        this.following = new ArrayList<>();
        this.listeners = new ArrayList<>();
        this.dataSource = dataSource;

        new GetUserTask().execute(userId);
    }

    public void addListener(DataUpdateListener l) {
        synchronized (listeners) {
            if (!listeners.contains(l)) {
                listeners.add(l);
            }
        }
    }

    public boolean removeListener(DataUpdateListener l) {
        synchronized (listeners) {
            if (listeners.contains(l)) {
                listeners.remove(l);
                return true;
            }
        }
        return false;
    }

    private void fireOnDataUpdate() {
        for (DataUpdateListener l : listeners) {
            l.onDataUpdate();
        }
    }

    public String getId() {
        if (user == null) return "";
        synchronized (user) {
            return user.getId();
        }
    }

    public String getName() {
        if (user == null) return "";
        synchronized (user) {
            return user.getName();
        }
    }

    public Boolean getVisible() {
        if (user == null) return false;
        synchronized (user) {
            return user.getVisible();
        }
    }

    public List<Person> getWhitelist() {
        if (user == null) return new ArrayList<>();
        synchronized (whitelisted) {
            return whitelisted;
        }
    }

    public List<Person> getFollowing() {
        if (user == null) return new ArrayList<>();
        synchronized (following) {
            return following;
        }
    }

    private void setUser(Person p) {
        Log.v(TAG, "Model.setUser()");
        synchronized (user) {
            user = p.clone();
            new PersonUpdateTask().execute(user);
            new GetFollowingTask().execute(user.getFollowing());
            new GetWhitelistTask().execute(user.getWhitelist());
        }
        fireOnDataUpdate();
    }

    public void setName(String n) {
        if (user != null) {
            PersonBuilder params = user.params.clone();
            params.name(n);
            setUser(params.build());
        }
    }

    public void setPosition(Position p) {
        if (user != null) {
            Log.v(TAG, "Model.setPosition()");
            PersonBuilder params = user.params.clone();
            params.position(p);
            setUser(params.build());
        }
    }

    public void setVisible(Boolean v) {
        if (user != null) {
            PersonBuilder params = user.params.clone();
            params.visible(v);
            setUser(params.build());
        }
    }

    public void setFollowing(List<String> ids) {
        if (user != null) {
            PersonBuilder params = user.params.clone();
            params.following(ids);
            setUser(params.build());
        }
    }

    public void addFollowing(String id) {
        if (user != null) {
            PersonBuilder params = user.params.clone();
            params.following(id);
            setUser(params.build());
        }
    }

    public void setWhitelist(List<String> ids) {
        if (user != null) {
            PersonBuilder params = user.params.clone();
            params.whitelist(ids);
            setUser(params.build());
        }
    }

    public void addWhitelist(String id) {
        if (user != null) {
            PersonBuilder params = user.params.clone();
            params.whitelist(id);
            setUser(params.build());
        }
    }

    private class PersonUpdateTask extends AsyncTask<Person, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Person... params) {
            return dataSource.updatePerson(params[0]);
        }
    }

    private class GetUserTask extends AsyncTask<String, Void, Person> {
        private String requestedId;
        @Override
        protected Person doInBackground(String... params) {
            Log.v(TAG, "GetUserTask.doInBackground()");
            requestedId = params[0];
            return dataSource.getPersonById(requestedId);
        }

        @Override
        protected void onPostExecute(Person person) {
            Log.v(TAG, "GetUserTask.onPostExecute()");
            if (person != null) {
                // This is awkward
                if (user == null) {
                    user = person;
                } else {
                    synchronized (user) {
                        user = person;
                    }
                }

                synchronized (user) {
                    new GetFollowingTask().execute(user.getFollowing());
                    new GetWhitelistTask().execute(user.getWhitelist());
                }
                fireOnDataUpdate();
            } else {
                new PersonUpdateTask().execute(new PersonBuilder().id(requestedId).build());
            }
            super.onPostExecute(person);
        }
    }

    private class GetPeopleTask extends AsyncTask<List<String>, Void, List<Person>> {
        @Override
        protected List<Person> doInBackground(List<String>... params) {
            return dataSource.getPeopleByIdList(params[0]);
        }

        @Override
        protected void onPostExecute(List<Person> persons) {
            fireOnDataUpdate();
            super.onPostExecute(persons);
        }
    }

    private class GetWhitelistTask extends GetPeopleTask {
        @Override
        protected void onPostExecute(List<Person> persons) {
            synchronized (whitelisted) {
                whitelisted = persons;
            }
            super.onPostExecute(persons);
        }
    }

    private class GetFollowingTask extends GetPeopleTask {
        @Override
        protected void onPostExecute(List<Person> persons) {
            synchronized (following) {
                following = persons;
            }
            super.onPostExecute(persons);
        }
    }
}