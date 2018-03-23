package de.fzi.edu.MyWaybook;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

import de.fzi.edu.MyWaybook.Helper.TrackHandler;

/**
 * This class is used to display statistics about the data stored in the database. A TabLayout lets the User
 * choose between daily, weekly and complete summaries. (Maybe monthly, if its necessary/useful)
 */

public class Statistics extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ListView mDrawerList;
    private TrackHandler trackHandler;
    private TextView completeLength;
    private TextView weeklyLength;
    private TextView dailyLength;
    private TextView completeDuration;
    private TextView weeklyDuration;
    private TextView dailyDuration;
    private ArrayAdapter<String> mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private String mActivityTitle;
    String[] activityTitleArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        trackHandler = new TrackHandler(this);

        mDrawerList = (ListView) findViewById(R.id.navList);
        mDrawerList.setOnItemClickListener(this);

        activityTitleArray = getResources().getStringArray(R.array.activity_names);

        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();

        addDrawerItems();
        setupDrawer();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mDrawerLayout.openDrawer(mDrawerList);

        TabHost host = (TabHost)findViewById(R.id.tabHost);
        host.setup();



        //Tab 1
        TabHost.TabSpec spec = host.newTabSpec("one");
        spec.setContent(R.id.tab1);
        spec.setIndicator("Woche");
        host.addTab(spec);

        //Tab 2

        spec = host.newTabSpec("Two");
        spec.setContent(R.id.tab2);
        spec.setIndicator("Heute");
        host.addTab(spec);

        //Tab 3
        spec = host.newTabSpec("Three");
        spec.setContent(R.id.tab3);
        spec.setIndicator("Gesamt");
        host.addTab(spec);

        host.setCurrentTab(1);

        completeLength = (TextView) findViewById(R.id.tv_completeLength_value);
        completeLength.setText(trackHandler.getCompleteLength());

        completeDuration =(TextView) findViewById(R.id.tv_completeDuration_value);
        completeDuration.setText(trackHandler.getCompleteDuration());






    }
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();

    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    private void addDrawerItems() {

        mAdapter = new ArrayAdapter<String>(this, R.layout.navigation_drawer_list, activityTitleArray);
        mDrawerList.setAdapter(mAdapter);
    }

    private void setupDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_Layout1);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.drawer_open, R.string.drawer_close) {


            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle(R.string.drawer_title_opened);


                invalidateOptionsMenu();
            }


            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu();
            }
        };
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.addDrawerListener(mDrawerToggle);

    }

    //closes the drawer
    @Override
    public void onBackPressed() {
        if (this.mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                Intent toHome = new Intent(this, HomeScreen.class);
                startActivity(toHome);
                break;
            case 1:
                Intent toLastTracks = new Intent(this, LastTracks.class);
                startActivity(toLastTracks);
                break;
            case 3:
                Intent toImprint = new Intent(this, Imprint.class);
                startActivity(toImprint);
                break;
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        // Activate the navigation drawer toggle
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
