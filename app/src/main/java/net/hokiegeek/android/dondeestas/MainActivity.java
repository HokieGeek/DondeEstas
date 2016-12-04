package net.hokiegeek.android.dondeestas;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Build;
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
import net.hokiegeek.android.dondeestas.datasource.DataSource;
import net.hokiegeek.android.dondeestas.datasource.DataUpdateListener;
import net.hokiegeek.android.dondeestas.datasource.DbSource;
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

    private LocationPublisher locationPublisher;

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

        locationPublisher = new LocationPublisher(this);

        initializeData(dbServer, userId);
    }

    protected void initializeData(String dbServer, String userId) {
        // Setup the data model
        // TODO: There has got to be a better way...
        if (!"http".equals(dbServer.substring(0,3))) {
            dbServer = "http://" + dbServer;
        }
        DataSource db = new DbSource(dbServer);
        dataModel = new Model(db, userId);
    }

    @Override
    protected void onStart() {
        Log.v(TAG, "onStart()");
        locationPublisher.start();
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.v(TAG, "onResume()");
        super.onResume();
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
        controlLocationUpdates(dataModel.getVisible(), menu.findItem(R.id.action_visibility));
        return true;
    }

    private void controlLocationUpdates(boolean enable, MenuItem item) {
        // String message = "";
        int icon = -1;
        if (enable) {
            // message = "Reporting location";
            locationPublisher.start();
            icon = R.drawable.ic_action_visibility;
        } else {
            // message = "Not reporting location";
            locationPublisher.stop();
            icon = R.drawable.ic_action_visibility_off;
        }

        // item.setChecked(enable);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            item.setIcon(getResources().getDrawable(icon, this.getTheme()));
        } else {
            item.setIcon(getResources().getDrawable(icon));
        }

        // Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_visibility) {
            Log.v(TAG, "Checked?: "+item.isChecked());
            dataModel.setVisible(!dataModel.getVisible());
            controlLocationUpdates(dataModel.getVisible(), item);
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
        Log.v(TAG, "onDataUpdate()");
        invalidateOptionsMenu();
        this.updateFragments();
    }

    private void updateFragments() {
        Log.v(TAG, "updateFragments()");
        if (mapFragment != null) {
            mapFragment.updateMarkers(Util.PersonListToMarkerOptionList(dataModel.getFollowing()));
            mapFragment.zoomToMarkers();
        }

        // TODO: peopleFragment
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(KEY_SERVER) || key.equals(KEY_USER_ID)) {
            initializeData(sharedPreferences.getString(KEY_SERVER, ""), sharedPreferences.getString(KEY_USER_ID, ""));
        // } else if (key.equals(KEY_USER_NAME)) {
        //     dataModel.setName(sharedPreferences.getString(KEY_USER_NAME, ""));
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.v(TAG, "Location: "+location.toString());
        // Toast.makeText(this, Util.LocationToPosition(location).toString(), Toast.LENGTH_SHORT).show();

        dataModel.setPosition(Util.LocationToPosition(location));
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