package de.fzi.edu.MyWaybook;


import android.*;
import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;

import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;

import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;


import de.fzi.edu.MyWaybook.Fragments.ModeDialogFragment;
import de.fzi.edu.MyWaybook.Fragments.PurposeDialogFragment;


import de.fzi.edu.MyWaybook.Helper.TrackHandler;
import de.fzi.edu.MyWaybook.Services.LocationTracker;
import de.fzi.edu.MyWaybook.constants.State;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

/**
 * The Default starting Activity. Contains a Google Map in which the current recording will be drawn.
 * The User can change Mode and Purpose, start and stop the Location Service.
 * A Navigation Drawer is implemented to access the other Activities
 */

public class HomeScreen extends AppCompatActivity implements AdapterView.OnItemClickListener, OnMapReadyCallback, View.OnClickListener, ModeDialogFragment.OnFragmentInteractionListener, PurposeDialogFragment.OnFragmentInteractionListener {

    /**
     * Declaring the variables for Tracking
     */
    private GoogleMap myMap;
    private int currentMode;
    private int currentPurpose;
    private TrackHandler trackHandler;
    private State STATE;
    private LocationManager manager;
    private static final int REQUEST_FINE_LOCATION = 0;
    private boolean locationPermissionEnabled = false;
    private static final int REQUEST_COARSE_LOCATION = 1;
    private static final int REQUEST_WRITE_STORAGE = 2;
    private static final int REQUEST_READ_STORAGE = 3;


    /**
     * Declaring variables for Views and Buttons
     */
    private Button btnStartTrack;
    private Button btnStartService;
    private ImageView ivMode;
    private ImageView ivPurpose;


    /**
     * Declaring Variables for Navigationdrawer
     */
    private ListView myDrawerList;
    private ArrayAdapter<String> myAdapter;
    private ActionBarDrawerToggle myDrawerToggle;
    private DrawerLayout myDrawerLayout;
    String[] activityTitleArray;

    /**
     * Setting up the View and initializing the Google Map.
     * Filling the tables for Mode and Purpose Icons.
     * Setting default STATE of the LocationService to OFF.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homescreen);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        FragmentManager myFragmentManager = getSupportFragmentManager();
        SupportMapFragment mySupportMapFragment
                = (SupportMapFragment) myFragmentManager.findFragmentById(R.id.map2);
        mySupportMapFragment.getMapAsync(this);

        ivPurpose = (ImageView) findViewById(R.id.purposeIV);
        ivMode = (ImageView) findViewById(R.id.modeIV);

        btnStartTrack = (Button) findViewById(R.id.new_Track_btn);
        btnStartTrack.setOnClickListener(this);

        btnStartService = (Button) findViewById(R.id.start_Service_btn);
        btnStartService.setOnClickListener(this);

        myDrawerList = (ListView) findViewById(R.id.navList);
        myDrawerList.setOnItemClickListener(this);

        activityTitleArray = getResources().getStringArray(R.array.activity_names);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        addDrawerItems();
        setupDrawer();

        trackHandler = new TrackHandler(this);
        trackHandler.fillModeTable();
        trackHandler.fillPurposeTable();

        STATE = State.TRACKING_OFF;

        btnStartService.setClickable(false);
        btnStartService.setAlpha(0.5F);

        manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            showGpsConfirmationDialog();
        }
        requestLocationPermission();



    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    protected void onStart() {
        super.onStart();

    }

    protected void onStop() {
        super.onStop();

    }

    /**
     * Stop the Service, when the application is killed.
     */
    @Override
    protected void onDestroy() {
        Intent stopIntent = new Intent(this, LocationTracker.class);
        this.stopService(stopIntent);
        super.onDestroy();

    }

    /**
     * Called when the startup is complete, to finally sync the ActionBarDrawerToggle
     *
     * @param savedInstanceState
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        myDrawerToggle.syncState();

    }

    /**
     * Adapt to configurationchanges
     *
     * @param newConfig
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        myDrawerToggle.onConfigurationChanged(newConfig);

    }

    /**
     * Method for adding Items to the NavigationDrawer
     * Uses a textview xml File for the Layout of one Item
     */

    private void addDrawerItems() {
        myAdapter = new ArrayAdapter<String>(this, R.layout.navigation_drawer_list, activityTitleArray);
        myDrawerList.setAdapter(myAdapter);

    }

    /**
     * Method for setting up the NavigationDrawer
     */

    private void setupDrawer() {

        myDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_Layout);
        myDrawerToggle = new ActionBarDrawerToggle(this, myDrawerLayout,
                R.string.drawer_open, R.string.drawer_close) {


            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle(R.string.drawer_title_opened);
                invalidateOptionsMenu();
            }


            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(R.string.drawer_title_homescreen_closed);
                invalidateOptionsMenu();
            }
        };
        myDrawerToggle.setDrawerIndicatorEnabled(true);
        myDrawerLayout.addDrawerListener(myDrawerToggle);

    }

    /**
     * Method for requesting location permission
     */
    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(HomeScreen.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION);
        ActivityCompat.requestPermissions(HomeScreen.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_COARSE_LOCATION);

    }


    /**
     * Callback received when a permission request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_FINE_LOCATION) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                locationPermissionEnabled = true;
                myMap.setMyLocationEnabled(true);
                btnStartTrack.setClickable(true);

            } else {
                locationPermissionEnabled = false;
                btnStartTrack.setClickable(false);
                Toast.makeText(this, R.string.location_permission_check, Toast.LENGTH_LONG).show();

            }

        }

    }


    /**
     * Handling Clickevents of the two Buttons on the HomeScreen
     * Depending on the current STATE of the LocationTracker, shows the DialogFragment.
     * If no Service is currently running, the User can change Mode and Purpose at will.
     * Starting the Location Service via the Start Button will call the TrackHandler.
     * If the Service is running, the User may only change the current Mode (e.g changing from Bike to Bus).
     * If the Service is already running, the User may click the Stop Button to end the current recording.
     *
     * @param v the clicked View
     */
    @Override
    public void onClick(View v) {
        ModeDialogFragment modeDialogFragment = new ModeDialogFragment();
        PurposeDialogFragment purposeDialogFragment = new PurposeDialogFragment();

        switch (v.getId()) {

            case R.id.new_Track_btn:

                if (STATE == State.TRACKING_OFF) {
                    purposeDialogFragment.show(getFragmentManager(), "");
                } else if (STATE == State.TRACKING_ON) {
                    modeDialogFragment.show(getFragmentManager(), "");
                }
                break;

            case R.id.start_Service_btn:

                if (STATE == State.TRACKING_OFF) {
                    trackHandler.startLocationTracker(myMap, currentMode, currentPurpose);
                    btnStartService.setText(R.string.button_stop_homescreen);
                    btnStartService.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stop_96px_4, 0, 0, 0);
                    STATE = State.TRACKING_ON;
                    btnStartTrack.setText(R.string.button_change_mode_homescreen);

                } else if (STATE == State.TRACKING_ON) {
                    showSaveConfirmation();
                }
                break;
        }

    }

    /**
     * Closes the Navigationdrawer, if it is open
     * If it is closed, the user is shown a confirmation to close the application
     */
    @Override
    public void onBackPressed() {
        if (this.myDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.myDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            showBackPressDialog();
        }
    }

    /**
     * Handles Interactions of the DialogFragment for Purposes.
     * The User is asked to choose the Purpose of the Track.
     * The Purpose may only be chosen before starting a recording and cannot be changed during a recording.
     * If necessary, the User may change the Purpose afterwards in the TrackStats Activity.
     * Choosing a Purpose starts the DialogFragment for Modes.
     *
     * @param purpose_Icon_ID is used to find the drawables for the ImageView
     * @param purpose_ID      is used define the Purpose of the current Track
     */

    @Override
    public void onFragmentInteraction(int purpose_Icon_ID, int purpose_ID) {

        ModeDialogFragment modeDialogFragment = new ModeDialogFragment();
        ivPurpose.setImageResource(purpose_Icon_ID);
        currentPurpose = purpose_ID;
        btnStartTrack.setText(R.string.button_change_input_homescreen);
        if (STATE == State.TRACKING_OFF) {
            modeDialogFragment.show(getFragmentManager(), "");

        }
    }

    /**
     * Handles Interactions of the DIalogFragment for Modes.
     * The User is asked to choose the Mode of the Track.
     * The Mode can be changed during a recording, but doing so will call the TrackHandler and end the current TrackSegment.
     * TODO: changing Modes afterwards
     *
     * @param mode_Icon_ID is used to find the drawables for the ImageView
     * @param mode_ID      is used to define the Mode of the current TrackSegment.
     */
    @Override
    public void onFragmentInteraction2(int mode_Icon_ID, int mode_ID) {
        ivMode.setImageResource(mode_Icon_ID);
        currentMode = mode_ID;


        if ( manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            btnStartService.setClickable(true);
            btnStartService.setAlpha(1F);
        } else {
            Toast.makeText(HomeScreen.this,R.string.GPS_not_enabled,Toast.LENGTH_LONG).show();
        }
        trackHandler.changeMode(currentMode);
    }

    /**
     * Method for handling clicks in the NavigationDrawer. Currently working only static(chosen by position)
     *
     * @param parent   parent adapter
     * @param view     clicked view
     * @param position number of position
     * @param id       id of view
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 1:
                Intent toLastTracks = new Intent(this, LastTracks.class);
                startActivity(toLastTracks);
                break;
            case 2:
                Intent toStatistics = new Intent(this, Statistics.class);
                startActivity(toStatistics);
                break;
            case 3:
                Intent toImprint = new Intent(this, Imprint.class);
                startActivity(toImprint);
                break;
        }

    }

    /**
     * Called when a menu item is selected
     *
     * @param item selected item
     * @return true, when an item is clicked
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


        if (id == R.id.action_settings) {
            return true;
        }


        if (myDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Method for activating the Google Map
     *
     * @param googleMap googleMap to be used
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        myMap = googleMap;
    }

    /**
     * Shows a confirmation for saving the Track or deleting it.
     * Saving the Track will reset the Interface and stop the LocationService.
     * Choosing to not save will ask the User whether to stop or to continue with the Track.
     */
    private void showSaveConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        builder.setTitle(R.string.app_name);
        builder.setMessage(R.string.saveConfirmation_message);

        builder.setPositiveButton(R.string.saveConfirmation_yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                STATE = State.TRACKING_OFF;

                btnStartService.setText(R.string.button_start_homescreen);
                btnStartService.setCompoundDrawablesWithIntrinsicBounds(R.drawable.play2, 0, 0, 0);
                btnStartService.setClickable(false);
                btnStartService.setAlpha(0.5F);

                btnStartTrack.setText(R.string.button_default_homescreen);

                ivMode.setImageResource(0);
                ivPurpose.setImageResource(0);

                trackHandler.stopLocationTracker();
                trackHandler.writeToDatabase();

                Toast.makeText(HomeScreen.this,R.string.track_saved,Toast.LENGTH_SHORT).show();

                dismissData();

            }
        });
        builder.setNegativeButton(R.string.saveConfirmation_no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                showContinueConfirmation();

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Shows a confirmation for stopping or continuing with the track.
     */

    private void showContinueConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        builder.setTitle(R.string.app_name);
        builder.setMessage(R.string.continueConfirmation_message);

        builder.setPositiveButton(R.string.continueConfirmation_continue, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();


            }
        });
        builder.setNegativeButton(R.string.continueConfirmation_exit, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                STATE = State.TRACKING_OFF;

                btnStartService.setText(R.string.button_start_homescreen);
                btnStartService.setCompoundDrawablesWithIntrinsicBounds(R.drawable.play2, 0, 0, 0);
                btnStartService.setClickable(false);
                btnStartService.setAlpha(0.5F);

                ivMode.setImageResource(0);
                ivPurpose.setImageResource(0);

                btnStartTrack.setText(R.string.button_default_homescreen);

                trackHandler.stopLocationTracker();

                dismissData();

            }
        });
        AlertDialog alert = builder.create();
        alert.show();

    }

    /**
     * Shows a confirmation on pressing the Back Button.
     */

    private void showBackPressDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        builder.setTitle(R.string.app_name);
        builder.setMessage(R.string.backPress_message);

        builder.setPositiveButton(R.string.backPress_yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                HomeScreen.super.onBackPressed();


            }
        });
        builder.setNegativeButton(R.string.backPress_no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();


            }
        });
        AlertDialog alert = builder.create();
        alert.show();

    }




    private void showGpsConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        builder.setMessage(R.string.gpsConfirmation_message)
                .setCancelable(false)
                .setPositiveButton(R.string.gpsConfirmation_yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton(R.string.gpsConfirmation_no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Clears the Data after saving it in the Database
     */
    private void dismissData() {
        trackHandler.dismissData();

    }


}



