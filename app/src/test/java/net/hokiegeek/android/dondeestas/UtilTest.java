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

      Location l = new Location("UnitTest");
      l.setLatitude(latitude);
      l.setLongitude(longitude);
      l.setAltitude(elevation);

      Position p = Util.LocationToPosition(l);

      assertEquals(p.latitude, l.getLatitude());
      assertEquals(p.longitude, l.getLongitude());
      assertEquals(p.elevation, l.getAltitude());
      // TODO: check the date field?
    }

    @Test
    public void TestPositionToLatLng() {
        Position p = new Position(new Date(), 1.0, 2.0, 3.0);
        LatLng l = Util.PositionToLatLng(p);
        // TODO: finish
    }
    
    // JSONObject PositionToJson(Position p)
    // Position PositionFromJson(JSONObject j)
    // JSONObject PersonToJson(Person p)
    // Person PersonFromJson(JSONObject j)
    // MarkerOptions PersonToMarkerOption(Person p)
    // List<MarkerOptions> PersonListToMarkerOptionList(List<Person> people)
}
