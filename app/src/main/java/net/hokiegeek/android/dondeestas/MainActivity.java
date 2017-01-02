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

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements
        OnFragmentLoadedListener,
        PersonFragment.OnListFragmentInteractionListener,
        PersonFragment.OnAddFollowingListener,
        DataUpdateListener,
        SharedPreferences.OnSharedPreferenceChangeListener,
        LocationListener
{
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
        // startService(new Intent(this, LocationPublisher.class));

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPref.registerOnSharedPreferenceChangeListener(this);
        String dbServer = sharedPref.getString(SettingsFragment.KEY_SERVER, "");
        String userId = sharedPref.getString(SettingsFragment.KEY_USER_ID, "");

        if ("".equals(dbServer) || "".equals(userId)) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        } else {
            initializeData(dbServer, userId);
        }
    }

    protected void initializeData(String dbServer, String userId) {
        Log.v(Util.TAG, "initializeData("+dbServer+", "+userId+")");
        if (!"".equals(dbServer) && !"".equals(userId)) {
            // Setup the data model
            dataModel = new Model();
            dataModel.addListener(this);
            dataModel.initialize(new DbSource(dbServer), userId);
        }
    }

    @Override
    protected void onStart() {
        if (dataModel != null) {
            Log.v(Util.TAG, "Activity.onStart(): visible = "+dataModel.getVisible());
            locationPublisher.enable(dataModel.getVisible());
            this.updateFragments();
        }
        super.onStart();
    }

    @Override
    protected void onStop() {
        // TODO: remove?
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        // TODO: confirm this
        locationPublisher.enable(false);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        if (dataModel != null) {
            locationPublisher.enable(dataModel.getVisible());
            setVisibilityIcon(dataModel.getVisible(), menu.findItem(R.id.action_visibility));
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
        if (fragment instanceof MapFragment) {
            mapFragment = (MapFragment) fragment;
        }
        if (fragment instanceof PersonFragment) {
            followingFragment = (PersonFragment) fragment;
        }

        this.updateFragments();

        if (dataModel != null && dataModel.getFollowing().size() == 1) {
            mapFragment.zoomToMarkers();
        }
    }

    @Override
    public void onDataUpdate() {
        if (dataModel != null) {
            locationPublisher.enable(dataModel.getVisible());
        }
        invalidateOptionsMenu();
        this.updateFragments();
    }

    private void updateFragments() {
        if (mapFragment != null && dataModel != null) {
            mapFragment.updateMarkers(Util.PersonListToMarkerOptionList(dataModel.getFollowing()));
        }

        if (followingFragment != null && dataModel != null) {
            followingFragment.updateItems(dataModel.getFollowing());
        }
    }

    @Override
    public void onListFragmentInteraction(Person item) {
        Log.v(Util.TAG, "Activity.onListFragmentInteraction(): "+item.toString());
        if (mapFragment != null) {
            mapFragment.centerOnMarker(item.getId());
            mViewPager.setCurrentItem(SectionsPagerAdapter.MAP_POSITION);
        }
    }

    @Override
    public void onFollowPeople(List<String> ids) {
        if (dataModel != null) {
            List<String> toFollow = new ArrayList<>();
            toFollow.addAll(dataModel.getFollowingIds());
            toFollow.addAll(ids);
            dataModel.setFollowing(toFollow);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (SettingsFragment.KEY_SERVER.equals(key) || SettingsFragment.KEY_USER_ID.equals(key)) {
            initializeData(sharedPreferences.getString(SettingsFragment.KEY_SERVER, ""), sharedPreferences.getString(SettingsFragment.KEY_USER_ID, ""));
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        // Log.v(Util.TAG, "Location changed: "+location.toString());
        if (dataModel != null) {
            dataModel.setPosition(Util.LocationToPosition(location));
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public static final int MAP_POSITION = 0;
        public static final int FOLLOWING_POSITION = 1;

        public SectionsPagerAdapter(FragmentManager fm) { super(fm); }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case MAP_POSITION: return MapFragment.newInstance();
                case FOLLOWING_POSITION: return PersonFragment.newInstance();
            }
            return null;
        }

        @Override
        public int getCount() { return 2; }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case MAP_POSITION: return getString(R.string.tab_name_map);
                case FOLLOWING_POSITION: return getString(R.string.tab_name_following);
            }
            return null;
        }
    }
}
