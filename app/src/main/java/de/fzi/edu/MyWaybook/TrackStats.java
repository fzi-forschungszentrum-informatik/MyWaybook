package de.fzi.edu.MyWaybook;

import android.*;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.fzi.edu.MyWaybook.Database.Track;
import de.fzi.edu.MyWaybook.Fragments.PurposeDialogFragment;
import de.fzi.edu.MyWaybook.Helper.TrackHandler;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

/**
 * This Activity is used to display information about a chosen Track. This Activity can only be accessed through the LastTracks Activity.
 *
 */

public class TrackStats extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener, PurposeDialogFragment.OnFragmentInteractionListener {

    private ImageView ivTrack;
    private TextView tvDate;
    private TextView tvLength;
    private TextView tvDuration;
    private LinearLayout layoutMode;
    private LinearLayout mapLayout;
    private Button btnDelete;
    private Button btnEdit;
    private Track track;
    private TrackHandler trackHandler;
    private GoogleMap myMap;
    private Context context = this;
    private final int REQUEST_FINE_LOCATION = 0;
    private final int REQUEST_COARSE_LOCATION =1;


    @Override

    /**
     * Filling Views with content.
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_stats);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        FragmentManager myFragmentManager = getSupportFragmentManager();
        SupportMapFragment mySupportMapFragment
                = (SupportMapFragment) myFragmentManager.findFragmentById(R.id.mapTrack);
        mySupportMapFragment.getMapAsync(this);

        trackHandler = new TrackHandler(this);
        track = (Track) getIntent().getExtras().getSerializable("Track");
        trackHandler.setTrackSegments(track, this);

        mapLayout = (LinearLayout) findViewById(R.id.map_layout);


        ivTrack = (ImageView) findViewById(R.id.icon_iv);
        ivTrack.setImageResource(trackHandler.getPurposeIcon(track, this));

        tvDate = (TextView) findViewById(R.id.tv_info2);
        tvDate.setText(trackHandler.getDate(track));

        tvLength = (TextView) findViewById(R.id.tv_length2);
        tvLength.setText(trackHandler.getLength(track));

        tvDuration = (TextView) findViewById(R.id.tv_duration2);
        tvDuration.setText(trackHandler.getDuration(track));

        layoutMode = (LinearLayout) findViewById(R.id.mode_container);
        trackHandler.setModeIcons(track, layoutMode, this);

        btnDelete = (Button) findViewById(R.id.btn_delete);
        btnDelete.setText(R.string.button_delete_trackStats);
        btnDelete.setOnClickListener(this);

        btnEdit = (Button) findViewById(R.id.btn_edit);
        btnEdit.setText(R.string.button_edit_trackStats);
        btnEdit.setOnClickListener(this);

        requestLocationPermission();


    }

    /**
     * Initializing Google Map for further use. Draws the Track when the map finishes loading.
     * @param googleMap
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {

        myMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        myMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                trackHandler.drawCompleteTrack(myMap, track, context, mapLayout);
            }
        });

    }

    /**
     * Called, when the User exits the Screen. always directs the User to LastTracks Activity.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent back = new Intent(this, LastTracks.class);
        startActivity(back);
    }

    /**
     * Handling clickevents. User is able to edit the Purpose. THe User can delete the Track.
     * When the Edit Button is pressed, Delete Button is disabled but the Purpose becomes clickable for editing.
     * @param v
     */

    @Override
    public void onClick(View v) {
        PurposeDialogFragment purposeDialogFragment = new PurposeDialogFragment();
        switch (v.getId()) {
            case R.id.btn_edit:
                btnDelete.setClickable(false);
                btnDelete.setAlpha(0.5F);
                ivTrack.setOnClickListener(this);
                ivTrack.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                break;


            case R.id.btn_delete:
                showDeleteConfirmation();
                break;

            case R.id.icon_iv:
                purposeDialogFragment.show(getFragmentManager(), "");
                break;


        }

    }

    /**
     * Method for requesting location permission
     */
    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(TrackStats.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION);
        ActivityCompat.requestPermissions(TrackStats.this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_COARSE_LOCATION);

    }

    /**
     * Method for confirmation when the User wants to delete the Track.
     */

    private void showDeleteConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        builder.setTitle(R.string.app_name);
        builder.setMessage(R.string.deleteConfirmation_message);

        builder.setPositiveButton(R.string.deleteConfirmation_yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                trackHandler.deleteTrack(track.getTrack_ID());
                finish();
                dialog.dismiss();

            }
        });
        builder.setNegativeButton(R.string.deleteConfirmation_no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();


            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Method for handling the actions when the User wants to edit the Purpose.
     * @param purpose_Icon_ID ID of the drawable
     * @param purpose_ID Id of the Purpose
     */

    @Override
    public void onFragmentInteraction(int purpose_Icon_ID, int purpose_ID) {

        ivTrack.setImageResource(purpose_Icon_ID);
        trackHandler.changePurposeInDatabase(track.getTrack_ID(), purpose_ID);
        btnDelete.setClickable(true);
        btnDelete.setAlpha(1.0F);
        ivTrack.setBackgroundColor(0);

    }
}
