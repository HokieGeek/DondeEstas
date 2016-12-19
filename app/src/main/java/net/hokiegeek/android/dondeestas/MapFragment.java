package net.hokiegeek.android.dondeestas;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MapFragment.OnFragmentLoadedListener} interface
 * to handle interaction events.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment
        implements OnMapReadyCallback,
                   ActivityCompat.OnRequestPermissionsResultCallback
{
    private static final String TAG = "DONDE";

    private MapView mapView;

    private GoogleMap map;

    private List<Marker> markers;

    private FloatingActionButton fabGather;

    /**
     * Request code for location permission request.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private OnFragmentLoadedListener mLoadedListener;

    public MapFragment() {
        markers = new ArrayList<>();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MapFragment.
     */
    public static MapFragment newInstance() {
        Log.v(TAG, "MapFragment.newInstance()");
        MapFragment fragment = new MapFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_map, container, false);

        // Gets the MapView from the XML layout and creates it
        mapView = (MapView) v.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        // Gets to GoogleMap from the MapView and does initialization stuff
        mapView.getMapAsync(this);

        fabGather = (FloatingActionButton) v.findViewById(R.id.fabFitMarkers);
        fabGather.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                zoomToMarkers();
            }
         });

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentLoadedListener) {
            mLoadedListener = (OnFragmentLoadedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentLoadedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mLoadedListener = null;
    }

    @Override
    public void onMapReady(final GoogleMap map) {
        this.map = map;

        try {
            enableMyLocation();

            // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
            MapsInitializer.initialize(this.getActivity());

            this.map.getUiSettings().setMapToolbarEnabled(false); // TODO: Would be better to relocate them

            mLoadedListener.onFragmentLoaded(this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null) {
            mapView.onResume();
        }
    }

    @Override
    public void onPause() {
        if (mapView != null) {
            mapView.onPause();
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (mapView != null) {
            try {
                mapView.onDestroy();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapView != null) {
            mapView.onLowMemory();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mapView != null) {
            mapView.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        boolean permissionDenied = true;
        for (int i = 0; i < permissions.length; i++) {
            if (Manifest.permission.ACCESS_FINE_LOCATION.equals(permissions[i])) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    enableMyLocation();
                    permissionDenied = false;
                }
                break;
            }
        }

        if (permissionDenied) {
            Toast.makeText(this.getContext(), "Did not receive permissions.", Toast.LENGTH_LONG);
        }
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(this.getContext(), "No permissions!", Toast.LENGTH_SHORT).show();
            // Permission to access the location is missing.
            ActivityCompat.requestPermissions(this.getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else if (map != null) {
            // Access to the location has been granted to the app.
            map.getUiSettings().setMyLocationButtonEnabled(true);
            map.setMyLocationEnabled(true);
        }
    }

    public void updateMarkers(List<MarkerOptions> markerOptions) {
        Log.v(TAG, "updateMarkers()");
        if (!markerOptions.isEmpty()) {
            map.clear();
            markers.clear();
            for (MarkerOptions mark : markerOptions) {
                markers.add(map.addMarker(mark));
            }
        }

        if (markerOptions.isEmpty()) {
            fabGather.setVisibility(View.INVISIBLE);
        } else {
            fabGather.setVisibility(View.VISIBLE);
        }
    }

    public void zoomToMarkers() {
        Log.v(TAG, "zoomToMarkers()");
        if (!markers.isEmpty()) {
            CameraUpdate cu;
            if (markers.size() == 1) {
                cu = CameraUpdateFactory.newLatLngZoom(markers.get(0).getPosition(), 10);
            } else {
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for (Marker marker : markers) {
                    builder.include(marker.getPosition());
                }
                LatLngBounds bounds = builder.build();
                // Then obtain a movement description object by using the factory: CameraUpdateFactory:

                cu = CameraUpdateFactory.newLatLngBounds(bounds, 190); // Number == padding in pixels
            }
            map.animateCamera(cu);
        }
    }
}
