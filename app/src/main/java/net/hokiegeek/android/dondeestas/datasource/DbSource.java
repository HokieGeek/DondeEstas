package net.hokiegeek.android.dondeestas.datasource;

import net.hokiegeek.android.dondeestas.data.Person;
import net.hokiegeek.android.dondeestas.data.PersonBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by andres on 11/29/16.
 */

public class DbSource implements DataSource {

    private List<DataUpdateListener> listeners;

    private DbSource() {
        listeners = new ArrayList<>();
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

    private void fireOnDataSourceUpdate() {
        for (DataUpdateListener l : listeners) {
            l.onDataUpdate();
        }
    }

    @Override
    public List<Person> getPeople() {
        List<Person> l = new ArrayList<>();
        // TODO: Implement DbSource.getPeople()
        return l;
    }

    @Override
    public List<Person> getPeopleByIdList(List<String> ids) {
        List<Person> l = new ArrayList<>();
        // TODO: Implement DbSource.getPeopleByIdList()
        return l;
    }

    @Override
    public Person getPersonById(String id) {
        return new PersonBuilder().build(); // TODO: Implement DbSource.getPersonById()
    }

    protected void get(String url, String... params) {
        // TODO
        /*
        ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();

        try {
            // Set up HTTP post

            // HttpClient is more then less deprecated. Need to change to URLConnection
            HttpClient httpClient = new DefaultHttpClient();

            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(new UrlEncodedFormEntity(param));
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();

            // Read content & Log
            inputStream = httpEntity.getContent();
        } catch (UnsupportedEncodingException e) {
            Log.e("UnsupportedEncodingException", e1.toString());
            e1.printStackTrace();
        } catch (ClientProtocolException e2) {
            Log.e("ClientProtocolException", e2.toString());
            e2.printStackTrace();
        } catch (IllegalStateException e3) {
            Log.e("IllegalStateException", e3.toString());
            e3.printStackTrace();
        } catch (IOException e4) {
            Log.e("IOException", e4.toString());
            e4.printStackTrace();
        }
        // Convert response to string using String Builder
        try {
            BufferedReader bReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"), 8);
            StringBuilder sBuilder = new StringBuilder();

            String line = null;
            while ((line = bReader.readLine()) != null) {
                sBuilder.append(line + "\n");
            }

            inputStream.close();
            result = sBuilder.toString();

        } catch (Exception e) {
            Log.e("StringBuilding & BufferedReader", "Error converting result " + e.toString());
        }
        */
    }

    protected void post(String url, String... params) {
        // TODO
    }
}