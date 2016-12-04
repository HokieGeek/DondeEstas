package net.hokiegeek.android.dondeestas.datasource;

import android.util.Log;

import net.hokiegeek.android.dondeestas.Util;
import net.hokiegeek.android.dondeestas.data.Person;
import net.hokiegeek.android.dondeestas.data.PersonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by andres on 11/29/16.
 */

public class DbSource implements DataSource {

    private static final String TAG = "DONDE";

    private static final String PATH_SEP = "/";
    private static final String PATH_GET_PERSON = "person";
    private static final String PATH_UPDATE_LOCATION = "update";

    private String url;

    public DbSource(String dbUrl) {
        this.url = dbUrl;
    }

    @Override
    public List<Person> getPeopleByIdList(List<String> ids) {
        Log.v(TAG, "getPeopleByIdList()");
        JSONObject resp = this.req(PATH_GET_PERSON, createPersonDataRequest(ids));
        return this.getPersonListFromJson(resp);
    }

    @Override
    public Person getPersonById(String id) {
        Log.v(TAG, "getPersonById()");
        List<Person> people = this.getPeopleByIdList(Arrays.asList(id));
        if (people.isEmpty()) {
            return null;
            // return new PersonBuilder().build(); // TODO: debug only
        } else {
            return people.get(0);
        }
    }

    @Override
    public boolean updatePerson(Person p) {
        Log.v(TAG, "updatePerson()");
        JSONObject resp = this.req(PATH_UPDATE_LOCATION, Util.PersonToJson(p));
        return false;
    }

    protected List<Person> getPersonListFromJson(JSONObject j) {
        List<Person> p = new ArrayList<>();
        try {
            JSONArray a = j.getJSONArray("People");
            for (int i = 0; i < a.length(); i++) {
                p.add(Util.PersonFromJson(a.getJSONObject(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            return p;
        }
    }

    protected JSONObject createPersonDataRequest(List<String> ids) {
        JSONObject j = new JSONObject();
        try {
            JSONArray a = new JSONArray();
            for (int i = 0; i < ids.size(); i++) {
               a.put(ids.get(i));
            }
            j.put("ids", a);
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            return j;
        }
    }

    protected JSONObject req(String path, JSONObject data) {
        HttpURLConnection connection = null;
        try {
            // URL url = new URL("http://requestb.in/18xm5rg1");//+PATH_SEP+path);
            URL url = new URL(this.url + PATH_SEP + path);
            Log.v(TAG, "URL = "+url.toString());
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");

            OutputStreamWriter osw = new OutputStreamWriter(connection.getOutputStream());
            osw.write(data.toString());
            osw.flush();
            osw.close();

            Log.v(TAG, "STATUS: "+connection.getResponseMessage());
        } catch (MalformedURLException e) {
            Log.v(TAG, "ERROR: URL: "+this.url);
        } catch (IOException e) {
            Log.v(TAG, "ERROR: IO: "+e.getMessage());
            // e.printStackTrace();
        } catch (NullPointerException e) {
            Log.v(TAG, "NPE: "+e.getMessage());
        } catch (Exception e) {
            Log.v(TAG, "Exception: "+e.getClass().getSimpleName());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }
}