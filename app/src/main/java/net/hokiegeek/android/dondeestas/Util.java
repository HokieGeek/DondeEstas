package net.hokiegeek.android.dondeestas;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import net.hokiegeek.android.dondeestas.data.Person;
import net.hokiegeek.android.dondeestas.data.PersonBuilder;
import net.hokiegeek.android.dondeestas.data.Position;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by andres on 11/24/16.
 */

public final class Util {
    public static final Position LocationToPosition(Location l) {
        if (l == null) {
            return new Position(new Date(), 0.0, 0.0, 0.0);
        }
        return new Position(new Date(), l.getLatitude(), l.getLongitude(), l.getAltitude());
    }

    public static final JSONObject PositionToJson(Position p) {
        JSONObject j = new JSONObject();
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        try {
            j.put("latitude", p.latitude);
            j.put("longitude", p.longitude);
            j.put("elevation", p.elevation);
            j.put("tov", fmt.format(p.tov));
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            return j;
        }
    }

    public static final Position PositionFromJson(JSONObject j) {
        Position p = null;
        try {
            p = new Position(new Date(), // TODO: Parse the date correctly
                    j.getDouble("latitude"),
                    j.getDouble("longitude"),
                    j.getDouble("elevation"));
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            return p;
        }
    }

    public static final JSONObject PersonToJson(Person p) {
        JSONObject j = new JSONObject();
        try {
            j.put("id", p.getId());
            j.put("name", p.getName());
            j.put("position", PositionToJson(p.getPosition()));
            j.put("visible", p.getVisible());

            JSONArray arr = new JSONArray();
            for (int i = 0; i < p.getWhitelist().size(); i++) {
               arr.put(p.getWhitelist().get(i));
            }
            j.put("whitelist", arr);

            arr = new JSONArray();
            for (int i = 0; i < p.getFollowing().size(); i++) {
                arr.put(p.getFollowing().get(i));
            }
            j.put("following", arr);
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            return j;
        }
    }

    public static final Person PersonFromJson(JSONObject j) {
        PersonBuilder builder = new PersonBuilder();
        try {
            builder.id(j.getString("id"))
                   .name(j.getString("name"))
                   .position(PositionFromJson(j.getJSONObject("position")))
                   .visible(j.getBoolean("visible"));

            JSONArray arr = j.getJSONArray("whitelist");
            for (int i = 0; i < arr.length(); i++) {
                builder.whitelist(arr.getString(i));
            }

            arr = j.getJSONArray("following");
            for (int i = 0; i < arr.length(); i++) {
                builder.following(arr.getString(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            return builder.build();
        }
    }

    public static final LatLng PositionToLatLng(Position p) {
        return new LatLng(p.latitude, p.longitude);
    }

    public static final MarkerOptions PersonToMarkerOption(Person p) {
        return new MarkerOptions().position(PositionToLatLng(p.getPosition()))
                    .title(p.getName());
    }

    public static final List<MarkerOptions> PersonListToMarkerOptionList(List<Person> people) {
        List<MarkerOptions> markers = new ArrayList<>();
        for (Person p : people) {
            markers.add(PersonToMarkerOption(p));
        }
        return markers;
    }
}
