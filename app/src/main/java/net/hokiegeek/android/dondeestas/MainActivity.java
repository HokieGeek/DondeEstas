package net.hokiegeek.android.dondeestas;

import android.Manifest;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import net.hokiegeek.android.dondeestas.data.Model;
import net.hokiegeek.android.dondeestas.datasource.DataSource;
import net.hokiegeek.android.dondeestas.datasource.DataUpdateListener;
import net.hokiegeek.android.dondeestas.datasource.DummyDataSource;
import net.hokiegeek.android.dondeestas.dummy.DummyContent;

public class MainActivity extends AppCompatActivity
        implements
        MapFragment.OnFragmentLoadedListener,
        PersonFragment.OnListFragmentInteractionListener,
        DataUpdateListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener
{
    private static final String TAG = "DONDE";

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private MapFragment mapFragment;

    private PersonFragment personFragment;

    private Model dataModel;

    private DataSource dataSource;

    private GoogleApiClient googleApiClient;

    private LocationRequest locationRequest;

    private boolean requestingLocationUpdates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        requestingLocationUpdates = true; // TODO: make this toggleable?

        locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        googleApiClient = new GoogleApiClient.Builder(this)
             .addConnectionCallbacks(this)
             .addOnConnectionFailedListener(this)
             .addApi(LocationServices.API)
             .build();

        // Setup the data model
        dataSource = DummyDataSource.newInstance();
        dataModel = new Model(dataSource);
    }

    @Override
    protected void onStart() {
        Log.v(TAG, "onStart()");
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        Log.v(TAG, "onStop()");
        googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            // TODO: allow to input your username key
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentLoaded(Fragment fragment) {
        Log.v(TAG, "onFragmentLoaded()");

        if (fragment instanceof MapFragment) {
            mapFragment = (MapFragment)fragment;

            dataModel.addListener(this);

            this.updateFragments();
        }
    }

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {
        Log.v(TAG, "onListFragmentInteraction()");
        Toast.makeText(this, item.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDataUpdate() {
        this.updateFragments();
    }

    private void updateFragments() {
        Log.v(TAG, "updateFragments()");
        if (mapFragment != null) {
            mapFragment.updateMarkers(Util.PersonListToMarkerOptionList(dataModel.getPeople()));
            mapFragment.zoomToMarkers();
        }

        // TODO: peopleFragment
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.v(TAG, "onConnected()");
        // TODO: Verify location settings
        /*
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>()) {
             @Override
             public void onResult(LocationSettingsResult result) {
                 final Status status = status.getStatus();
                 final LocationSettingsStates = result.getLocationSettingsStates();
                 switch (status.getStatusCode()) {
                     case LocationSettingsStatusCodes.SUCCESS:
                         // All location settings are satisfied. The client can
                         // initialize location requests here. ...
                         break;
                     case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                         // Location settings are not satisfied, but this can be fixed
                         // by showing the user a dialog.
                         try {
                             // Show the dialog by calling startResolutionForResult(),
                             // and check the result in onActivityResult().
                             status.startResolutionForResult(
                                 OuterClass.this,
                                 REQUEST_CHECK_SETTINGS);
                         } catch (IntentSender.SendIntentException e) {
                             // Ignore the error.
                         }
                         break;
                     case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                         // Location settings are not satisfied. However, we have no way
                         // to fix the settings so we won't show the dialog. ...
                         break;
                 }
             }
        });
        */

        if (requestingLocationUpdates) {
            Log.v(TAG, "Have requested location updates");
            startLocationUpdates();
        } else {
            Log.v(TAG, "Not starting location updates");
        }
    }

    protected void startLocationUpdates() {
        Log.v(TAG, "Starting location updates");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "Starting location updates: have permissions");
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        } else {
            Log.v(TAG, "Starting location updates: do not have permissions");
            Toast.makeText(this, "Do not have permissions", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        // TODO
        Log.v(TAG, "onConnectionSuspended()");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // TODO
        Log.v(TAG, "onConnectionFailed()");
    }

    @Override
    public void onLocationChanged(Location location) {
        // TODO
        Toast.makeText(this, "blah", Toast.LENGTH_SHORT).show();
        Log.v(TAG, "Location: ");
        // Log.v(TAG, "Location: "+location.toString());
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return MapFragment.newInstance();
                    // return (mapFragment = MapFragment.newInstance());
                case 1:
                    return (personFragment = PersonFragment.newInstance(1));
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.tab_name_map);
                case 1:
                    return getString(R.string.tab_name_following);
            }
            return null;
        }
    }
}