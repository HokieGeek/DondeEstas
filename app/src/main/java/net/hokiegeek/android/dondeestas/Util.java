package net.hokiegeek.android.dondeestas;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import net.hokiegeek.android.dondeestas.data.Person;
import net.hokiegeek.android.dondeestas.data.Position;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andres on 11/24/16.
 */

public final class Util {
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
