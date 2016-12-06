package net.hokiegeek.android.dondeestas;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import net.hokiegeek.android.dondeestas.data.Position;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.util.Date;
import java.util.List;


import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class UtilTest {
    @Test
    public void TestLocationToPosition() throws Exception {
        Double latitude = 1.0;
        Double longitude = 2.0;
        Double elevation = 3.0;

        /*
        l = new Location("UnitTest");
        l.setLatitude(latitude);
        l.setLongitude(longitude);
        l.setAltitude(elevation);

        Position p = Util.LocationToPosition(l);

        assertTrue(p.latitude.equals(l.getLatitude()));
        assertTrue(p.longitude.equals(l.getLongitude()));
        assertTrue(p.elevation.equals(l.getAltitude()));
        // TODO: check the date field?
        */
    }

    @Test
    public void TestPositionToLatLng() {
        Position p = new Position(new Date(), 1.0, 2.0, 3.0);
        LatLng l = Util.PositionToLatLng(p);

        assertTrue(p.latitude.equals(l.latitude));
        assertTrue(p.longitude.equals(l.longitude));
    }

    @Test
    public void TestPositionToJson() {
        Position p = new Position(new Date(), 1.0, 2.0, 3.0);
        JSONObject j = Util.PositionToJson(p);

        /*
        try {
            assertTrue(p.latitude.equals(j.getDouble("latitude")));
            assertTrue(p.longitude.equals(j.getDouble("longitude")));
            assertTrue(p.elevation.equals(j.getDouble("elevation")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        */
    }

    @Test
    public void TestPositionFromJson() {
        Position expected = new Position(new Date(), 1.0, 2.0, 3.0);
        JSONObject j = Util.PositionToJson(expected);
        Position found = Util.PositionFromJson(j);

        assertTrue(expected.equals(found));
    }

    // JSONObject PersonToJson(Person p)
    // Person PersonFromJson(JSONObject j)
    // MarkerOptions PersonToMarkerOption(Person p)
    // List<MarkerOptions> PersonListToMarkerOptionList(List<Person> people)
}
