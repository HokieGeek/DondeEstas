package net.hokiegeek.android.dondeestas;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
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

import net.hokiegeek.android.dondeestas.data.Model;
import net.hokiegeek.android.dondeestas.data.Person;
import net.hokiegeek.android.dondeestas.data.User;
import net.hokiegeek.android.dondeestas.datasource.DataSource;
import net.hokiegeek.android.dondeestas.datasource.DataUpdateListener;
import net.hokiegeek.android.dondeestas.datasource.DbSource;
import net.hokiegeek.android.dondeestas.datasource.DummyDataSource;
import net.hokiegeek.android.dondeestas.dummy.DummyContent;

import com.google.android.gms.location.LocationListener;

import static net.hokiegeek.android.dondeestas.SettingsActivity.*;

public class MainActivity extends AppCompatActivity
        implements
        MapFragment.OnFragmentLoadedListener,
        PersonFragment.OnListFragmentInteractionListener,
        DataUpdateListener,
        SharedPreferences.OnSharedPreferenceChangeListener,
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

    private PersonFragment followingFragment;

    private Model dataModel;

    private boolean requestingLocationUpdates;

    private LocationPublisher locationPublisher;

    private User user;

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

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String dbServer = sharedPref.getString(KEY_SERVER, "");
        String userId = sharedPref.getString(KEY_USER_ID, "");

        initializeData(dbServer, userId);
    }

    protected void initializeData(String dbServer, String userId) {
        locationPublisher = new LocationPublisher(this);

        // Setup the data model
        DataSource db = new DbSource(dbServer);
        dataModel = new Model(db);

        // Update the user
        Person user = dataModel.getPersonById(userId);
        if (user != null) {
            this.user = new User(user);
        }

        requestingLocationUpdates = user.getVisible(); // TODO: Read this from the data model
    }

    @Override
    protected void onStart() {
        Log.v(TAG, "onStart()");
        locationPublisher.start();
        super.onStart();
    }

    @Override
    protected void onStop() {
        Log.v(TAG, "onStop()");
        locationPublisher.stop();
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

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_visibility) {
            requestingLocationUpdates = !requestingLocationUpdates;
            String message = "";
            if (requestingLocationUpdates) {
                message = "Reporting location";
                locationPublisher.start();
                item.setChecked(true);
                // TODO: "enable" the button
            } else {
                message = "Not reporting location";
                locationPublisher.stop();
                item.setChecked(false);
                // TODO: "disable" the button
            }

            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(KEY_SERVER) || key.equals(KEY_USER_ID)) {
            initializeData(sharedPreferences.getString(KEY_SERVER, ""), sharedPreferences.getString(KEY_USER_ID, ""));
        } else if (key.equals(KEY_USER_NAME)) {
            // TODO: Update in the database
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.v(TAG, "Location: "+location.toString());
        Toast.makeText(this, Util.LocationToPosition(location).toString(), Toast.LENGTH_SHORT).show();

        user.setPosition(Util.LocationToPosition(location));
        dataModel.updatePerson(user.getPerson());
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
                case 1:
                    return (followingFragment = PersonFragment.newInstance(1));
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