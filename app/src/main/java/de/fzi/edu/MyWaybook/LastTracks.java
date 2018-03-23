package de.fzi.edu.MyWaybook;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;

import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import de.fzi.edu.MyWaybook.Database.PurposeSource;
import de.fzi.edu.MyWaybook.Database.Track;
import de.fzi.edu.MyWaybook.Database.TrackSegmentSource;
import de.fzi.edu.MyWaybook.Database.TrackSource;
import de.fzi.edu.MyWaybook.Helper.TrackAdapter;
import de.fzi.edu.MyWaybook.Helper.TrackHandler;

import java.util.ArrayList;

/**
 * This Activity is used for displaying all currently stored Tracks in a ListView.
 */

public class LastTracks extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ListView mDrawerList;

    private ListView lvTracks;
    private TrackHandler trackHandler;
    private TrackSource trackSource;
    private ArrayList tracks = new ArrayList();
    private ArrayAdapter<String> myAdapter;
    private PurposeSource purposeSource;
    private ActionBarDrawerToggle myDrawerToggle;
    private DrawerLayout myDrawerLayout;
    private String myActivityTitle;
    private TrackSegmentSource segmentSource;
    String[] activityTitleArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last_tracks);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mDrawerList = (ListView) findViewById(R.id.navList);
        mDrawerList.setOnItemClickListener(this);
        activityTitleArray = getResources().getStringArray(R.array.activity_names);

        myDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        myActivityTitle = getTitle().toString();

        lvTracks = (ListView) findViewById(R.id.tracks_lv);

        addDrawerItems();
        setupDrawer();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        trackHandler = new TrackHandler(this);
        tracks = trackHandler.readFromDatabase();

        purposeSource = new PurposeSource(this);
        segmentSource = new TrackSegmentSource(this);

        TrackAdapter trackAdapter = new TrackAdapter(this, tracks, purposeSource, segmentSource);
        lvTracks.setAdapter(trackAdapter);
        lvTracks.setOnItemClickListener(this);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        myDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        myDrawerToggle.onConfigurationChanged(newConfig);
    }

    private void addDrawerItems() {
        myAdapter = new ArrayAdapter<String>(this, R.layout.navigation_drawer_list, activityTitleArray);
        mDrawerList.setAdapter(myAdapter);
    }

    private void setupDrawer() {
        myDrawerToggle = new ActionBarDrawerToggle(this, myDrawerLayout,
                R.string.drawer_open, R.string.drawer_close) {


            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle(R.string.drawer_title_opened);
                invalidateOptionsMenu();
            }


            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(myActivityTitle);
                invalidateOptionsMenu();
            }
        };
        myDrawerToggle.setDrawerIndicatorEnabled(true);
        myDrawerLayout.setDrawerListener(myDrawerToggle);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.navList) {
            switch (position) {

                case 0:
                    Intent toHome = new Intent(this, HomeScreen.class);
                    startActivity(toHome);
                    break;
                case 2:
                    Intent toStatistics = new Intent(this, Statistics.class);
                    startActivity(toStatistics);
                    break;
                case 3:
                    Intent toInfo = new Intent(this, Imprint.class);
                    startActivity(toInfo);
                    break;
            }
            //Handling clicks in the ListView
        } else if (parent.getId() == R.id.tracks_lv) {
            Intent toTrack = new Intent(this, TrackStats.class);
            Track track = (Track) tracks.get(position);

            toTrack.putExtra("Track",track);
            startActivity(toTrack);
            finish();

        }

    }






    @Override
    public void onBackPressed() {
        if (this.myDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.myDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


        if (id == R.id.action_settings) {
            return true;
        }

        // Activate the navigation drawer toggle
        if (myDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}

