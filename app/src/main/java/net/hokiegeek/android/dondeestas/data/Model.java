package net.hokiegeek.android.dondeestas.data;

import android.os.AsyncTask;
import android.util.Log;

import net.hokiegeek.android.dondeestas.datasource.DataSource;
import net.hokiegeek.android.dondeestas.datasource.DataUpdateListener;
import net.hokiegeek.android.dondeestas.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by andres on 11/23/16.
 */

public class Model {
    private Person user;
    private List<Person> whitelisted;
    private List<Person> following;

    private DataSource dataSource;
    private List<DataUpdateListener> listeners;

    private ScheduledExecutorService scheduleTaskExecutor;

    public Model() {
        synchronized(this) {
            this.whitelisted = new ArrayList<>();
            this.following = new ArrayList<>();
            this.listeners = new ArrayList<>();
            this.scheduleTaskExecutor = Executors.newScheduledThreadPool(5);
        }
    }

    public Model(DataSource dataSource, String userId) {
        this();
        initialize(dataSource, userId);
    }

    public void initialize(DataSource source, String userId) {
        dataSource = source;
        new GetUserTask().execute(userId);

        // This schedule a task to run every 10 seconds:
        scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
            public void run() {
                synchronized (user) {
                    if (user.getFollowing().size() > 0) {
                        new GetFollowingTask().execute(user.getFollowing());
                    }
                }
            }
        }, 15, 10, TimeUnit.SECONDS); // TODO: make this configurable
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
            if ("".equals(user.getName())) {
                return user.getId();
            } else {
                return user.getName();
            }
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

    public List<String> getFollowingIds() {
        if (user == null) return new ArrayList<>();
        synchronized (user) {
            return user.getFollowing();
        }
    }

    private void updateUser(Person p) {
        new PersonUpdateTask().execute(p.clone());
    }

    private void setUser(Person p) {
        // This is awkward
        if (user == null) {
            user = p.clone();
        } else {
            synchronized (user) {
                user = p.clone();
            }
        }

        synchronized (user) {
            if (user.getFollowing().size() > 0) {
                new GetFollowingTask().execute(user.getFollowing());
            }
            if (user.getWhitelist().size() > 0) {
                new GetWhitelistTask().execute(user.getWhitelist());
            }
        }
        fireOnDataUpdate();
    }

    public void setName(String n) {
        if (user != null) {
            PersonBuilder params = user.params.clone();
            params.name(n);
            updateUser(params.build());
        }
    }

    public void setPosition(Position p) {
        if (user != null) {
            PersonBuilder params = user.params.clone();
            params.position(p.clone());
            updateUser(params.build());
        }
    }

    public void setVisible(Boolean v) {
        if (user != null) {
            PersonBuilder params = user.params.clone();
            params.visible(v);
            updateUser(params.build());
        }
    }

    public void setFollowing(List<String> ids) {
        if (user != null) {
            PersonBuilder params = user.params.clone();
            params.following(ids);
            updateUser(params.build());
        }
    }

    public void addFollowing(String id) {
        if (user != null) {
            PersonBuilder params = user.params.clone();
            params.following(id);
            updateUser(params.build());
        }
    }

    public void setWhitelist(List<String> ids) {
        if (user != null) {
            PersonBuilder params = user.params.clone();
            params.whitelist(ids);
            updateUser(params.build());
        }
    }

    public void addWhitelist(String id) {
        if (user != null) {
            PersonBuilder params = user.params.clone();
            params.whitelist(id);
            updateUser(params.build());
        }
    }

    private class PersonUpdateTask extends AsyncTask<Person, Void, Boolean> {
        private Person p;

        @Override
        protected Boolean doInBackground(Person... params) {
            p = params[0];
            return dataSource.updatePerson(p);
        }

        @Override
        protected void onPostExecute(Boolean updated) {
            if (updated) {
                setUser(this.p);
            }
            super.onPostExecute(updated);
        }
    }

    private class GetUserTask extends AsyncTask<String, Void, Person> {
        private String requestedId;
        @Override
        protected Person doInBackground(String... params) {
            requestedId = params[0];
            return dataSource.getPersonById(requestedId);
        }

        @Override
        protected void onPostExecute(Person person) {
            if (person != null) {
                setUser(person);
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
