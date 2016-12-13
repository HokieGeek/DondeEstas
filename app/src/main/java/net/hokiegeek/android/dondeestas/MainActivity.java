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
import net.hokiegeek.android.dondeestas.data.Person;
import net.hokiegeek.android.dondeestas.datasource.DataUpdateListener;
import net.hokiegeek.android.dondeestas.datasource.DbSource;

import com.google.android.gms.location.LocationListener;

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
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        locationPublisher = new LocationPublisher(this);
        locationPublisher.addListener(this);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String dbServer = sharedPref.getString(SettingsFragment.KEY_SERVER, "");
        String userId = sharedPref.getString(SettingsFragment.KEY_USER_ID, "");

        // TODO: popup settings when have no prefs
        if ("".equals(dbServer) || "".equals(userId)) {
            // Intent intent = new Intent(this, SettingsActivity.class);
            // startActivity(intent);
            Log.v(TAG, "Faking out the prefs");
            dbServer = "http://hokiegeek.net:8585";
            userId = "andres";
            sharedPref.edit().putString(SettingsFragment.KEY_SERVER, dbServer).apply();
            sharedPref.edit().putString(SettingsFragment.KEY_USER_ID, userId).apply();
        }
        initializeData(dbServer, userId);
    }

    protected void initializeData(String dbServer, String userId) {
        // Setup the data model
        dataModel = new Model(); //new DbSource(dbServer), userId);
        dataModel.addListener(this);
        dataModel.initialize(new DbSource(dbServer), userId);
    }

    @Override
    protected void onStart() {
        Log.v(TAG, "Activity.onStart()");
        if (dataModel != null) {
            locationPublisher.enable(dataModel.getVisible());
        }
        super.onStart();
    }

    @Override
    protected void onStop() {
        Log.v(TAG, "Activity.onStop()");
        locationPublisher.enable(false);
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        if (dataModel != null) {
            setVisibilityIcon(dataModel.getVisible(), menu.findItem(R.id.action_visibility));
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.v(TAG, "Activity.onOptionsItemSelected()");
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_visibility) {
            if (dataModel != null) {
                dataModel.setVisible(!dataModel.getVisible());
                locationPublisher.enable(dataModel.getVisible());
                setVisibilityIcon(dataModel.getVisible(), item);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setVisibilityIcon(boolean enable, MenuItem item) {
        int icon;
        if (enable) {
            icon = R.drawable.ic_action_visibility;
        } else {
            icon = R.drawable.ic_action_visibility_off;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            item.setIcon(getResources().getDrawable(icon, this.getTheme()));
        } else {
            item.setIcon(getResources().getDrawable(icon));
        }
    }

    @Override
    public void onFragmentLoaded(Fragment fragment) {
        Log.v(TAG, "Activity.onFragmentLoaded()");

        if (fragment instanceof MapFragment) {
            mapFragment = (MapFragment) fragment;
        }
        if (fragment instanceof PersonFragment) {
            followingFragment = (PersonFragment) fragment;
        }

        this.updateFragments();
    }

    @Override
    public void onDataUpdate() {
        Log.v(TAG, "Activity.onDataUpdate()");
        invalidateOptionsMenu();
        this.updateFragments();
    }

    private void updateFragments() {
        Log.v(TAG, "Activity.updateFragments()");
        if (mapFragment != null && dataModel != null) {
            mapFragment.updateMarkers(Util.PersonListToMarkerOptionList(dataModel.getFollowing()));
            mapFragment.zoomToMarkers();
        }

        if (followingFragment != null && dataModel != null) {
            followingFragment.updateItems(dataModel.getFollowing());
        }
    }

    @Override
    public void onListFragmentInteraction(Person item) {
        Log.v(TAG, "Activity.onListFragmentInteraction()");
        Toast.makeText(this, item.toString(), Toast.LENGTH_SHORT).show(); // TODO
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.v(TAG, "Activity.onSharedPreferencesChanged()");
        if (key.equals(SettingsFragment.KEY_SERVER) || key.equals(SettingsFragment.KEY_USER_ID)) {
            initializeData(sharedPreferences.getString(SettingsFragment.KEY_SERVER, ""), sharedPreferences.getString(SettingsFragment.KEY_USER_ID, ""));
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.v(TAG, "Location changed: "+location.toString());
        if (dataModel != null) {
            dataModel.setPosition(Util.LocationToPosition(location));
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) { super(fm); }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: return (mapFragment = MapFragment.newInstance());
                case 1: return (followingFragment = PersonFragment.newInstance());
            }
            return null;
        }

        @Override
        public int getCount() { return 2; }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0: return getString(R.string.tab_name_map);
                case 1: return getString(R.string.tab_name_following);
            }
            return null;
        }
    }
}
