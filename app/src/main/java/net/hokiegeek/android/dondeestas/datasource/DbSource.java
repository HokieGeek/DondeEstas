package net.hokiegeek.android.dondeestas.datasource;

import android.util.Log;

import net.hokiegeek.android.dondeestas.Util;
import net.hokiegeek.android.dondeestas.data.Person;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by andres on 11/29/16.
 */

public class DbSource implements DataSource {

    private static final String PATH_SEP = "/";
    private static final String PATH_GET_PERSON = "person";
    private static final String PATH_UPDATE_LOCATION = "update";

    private String url;

    public DbSource(String dbUrl) {
        this.url = dbUrl;
    }

    @Override
    public List<Person> getPeopleByIdList(List<String> ids) {
        Log.v(Util.TAG, "getPeopleByIdList()");
        Response resp = this.req(PATH_GET_PERSON, createPersonDataRequest(ids));
        JSONObject json = null;
        if (resp.Body == null || "".equals(resp.Body)) {
            Log.d(Util.TAG, "getPeopleByIdList(): Body is empty!");
        } else {
            try {
                json = new JSONObject(resp.Body);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return this.getPersonListFromJson(json);
    }

    @Override
    public Person getPersonById(String id) {
        Log.v(Util.TAG, "getPersonById()");
        List<Person> people = this.getPeopleByIdList(Arrays.asList(id));
        if (people.isEmpty()) {
            Log.v(Util.TAG, "getPersonById(): Did not find a person with ID: "+id);
            return null;
        } else {
            Log.v(Util.TAG, "getPersonById(): Found person with ID: "+id);
            return people.get(0);
        }
    }

    @Override
    public boolean updatePerson(Person p) {
        Log.v(Util.TAG, "updatePerson()");
        Response resp = this.req(PATH_UPDATE_LOCATION, Util.PersonToJson(p));
        return (resp.StatusCode == 200 || resp.StatusCode == 201); // TODO: is there a useful enum for this?
    }

    protected List<Person> getPersonListFromJson(JSONObject j) {
        List<Person> p = new ArrayList<>();
        try {
            if (j != null) {
                JSONArray a = j.getJSONArray("people");
                for (int i = 0; i < a.length(); i++) {
                    p.add(Util.PersonFromJson(a.getJSONObject(i)));
                }
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

    protected Response req(String path, JSONObject data) {
        Response resp = new Response();
        HttpURLConnection connection = null;
        try {
            URL url = new URL(this.url + PATH_SEP + path);
            Log.v(Util.TAG, "URL = "+url.toString());
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");

            OutputStreamWriter osw = new OutputStreamWriter(connection.getOutputStream());
            osw.write(data.toString());
            osw.flush();
            osw.close();
            Log.v(Util.TAG, "data => "+data.toString());

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder result = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null) {
                result.append(line);
            }

            resp.StatusCode = connection.getResponseCode();
            resp.StatusMessage = connection.getResponseMessage();
            resp.Body = result.toString();

            Log.v(Util.TAG, resp.Body);
            Log.v(Util.TAG, "STATUS: "+resp.StatusMessage);
        } catch (MalformedURLException e) {
            Log.v(Util.TAG, "ERROR: URL: "+this.url);
        } catch (IOException e) {
            Log.v(Util.TAG, "ERROR: IO: "+e.getMessage());
            // e.printStackTrace();
        } catch (NullPointerException e) {
            Log.v(Util.TAG, "NPE: "+e.getMessage());
        } catch (Exception e) {
            Log.v(Util.TAG, "Exception: "+e.getClass().getSimpleName());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            return resp;
        }
    }

    private class Response {
        public int StatusCode;
        public String StatusMessage;
        public String Body;
    }
}