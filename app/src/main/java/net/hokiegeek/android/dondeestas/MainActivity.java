package net.hokiegeek.android.dondeestas;

import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import net.hokiegeek.android.dondeestas.data.Model;
import net.hokiegeek.android.dondeestas.datasource.DataSource;
import net.hokiegeek.android.dondeestas.datasource.DummyDataSource;
import net.hokiegeek.android.dondeestas.dummy.DummyContent;

import java.util.Vector;

public class MainActivity extends AppCompatActivity
        implements
        MapFragment.OnFragmentLoadedListener,
        PersonFragment.OnListFragmentInteractionListener
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

        // Setup the data model
        // TODO: not done yet!
        DataSource ds = DummyDataSource.newInstance();
        dataModel = new Model(ds);

        /*
        List<MarkerOptions> markers = new ArrayList<>();
        markers.add(new MarkerOptions().position(new LatLng(38.975095, -77.195674)).title("Andres"));
        markers.add(new MarkerOptions().position(new LatLng(39.1888622, -77.287454)).title("Olivia"));
        markers.add(new MarkerOptions().position(new LatLng(39.189658, -77.279528)).title("Keri"));

        mf.updateMarkers(markers);
        mf.zoomToMarkers();
        */
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
        Log.v(TAG, "onFragmentLoaded");

        if (fragment instanceof MapFragment) {
            MapFragment mf = (MapFragment) fragment;
            // TODO
            /*
            Vector<MarkerOptions> markers = new Vector<>();
            markers.add(new MarkerOptions().position(new LatLng(38.975095, -77.195674)).title("Andres"));
            markers.add(new MarkerOptions().position(new LatLng(39.1888622, -77.287454)).title("Olivia"));
            markers.add(new MarkerOptions().position(new LatLng(39.189658, -77.279528)).title("Keri"));

            mf.updateMarkers(markers);
            mf.zoomToMarkers();
            */
        }
    }

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {
        Log.v(TAG, "onListFragmentInteraction");
        Toast.makeText(this, item.toString(), Toast.LENGTH_SHORT).show();
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
                    return (mapFragment = MapFragment.newInstance());
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
