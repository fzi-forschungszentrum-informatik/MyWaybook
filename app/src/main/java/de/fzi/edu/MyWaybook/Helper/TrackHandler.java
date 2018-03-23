package de.fzi.edu.MyWaybook.Helper;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import de.fzi.edu.MyWaybook.Database.ModeSource;
import de.fzi.edu.MyWaybook.Database.PurposeSource;
import de.fzi.edu.MyWaybook.Database.Track;
import de.fzi.edu.MyWaybook.Database.TrackSegment;
import de.fzi.edu.MyWaybook.Database.TrackSegmentSource;
import de.fzi.edu.MyWaybook.Database.TrackSource;

import de.fzi.edu.MyWaybook.R;
import de.fzi.edu.MyWaybook.Services.LocationTracker;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by rickert on 19.12.2016.
 * This class provides various methods for interaction with User inputs. Inputs related to Database Operations
 * are passed down to the Track Class. Outputs from the Database are processed in this class. Locations are received
 * in this class. Provides methods for filling the tables of modes and purposes on an initial start.
 */

public class TrackHandler {
    private LocationHandler handler = new LocationHandler();

    // Declaring variables for Locations
    private Track track;
    private long time;
    Location newLocation;
    Location oldLocation;


    public Context context;
    private GoogleMap myMap;
    private TrackSource trackSource;
    private TrackSegmentSource trackSegmentSource;
    private ModeSource modeSource;
    private PurposeSource purposeSource;
    // Arrays for modes and Purposes
    private String[] modeNames;
    private String[] modeIcons;
    private String[] purposeNames;
    private String[] purposeIcons;


    /**
     * Public constructor for this class.
     *
     * @param context of HomeScreen
     */
    public TrackHandler(Context context) {
        this.context = context;

        trackSource = new TrackSource(context);
        trackSegmentSource = new TrackSegmentSource(context);
        modeSource = new ModeSource(context);
        purposeSource = new PurposeSource(context);


    }

    /**
     * @return returns all currently saved TrackSegments from the Database.
     * Used for getting Information about entire Length and Duration.
     */
    private ArrayList getCompleteData() {
        ArrayList allSegments;
        trackSegmentSource.open();
        allSegments = (ArrayList) trackSegmentSource.getAllSegments();
        trackSegmentSource.close();
        return allSegments;

    }

    /**
     * @return returns the entire Length of all TrackSegments combined
     */

    public String getCompleteLength() {
        ArrayList<TrackSegment> allSegments = getCompleteData();
        double completeLength = 0;
        for (int i = 0; i < allSegments.size(); i++) {
            completeLength += allSegments.get(i).getLength();
        }
        if (completeLength > 1000) {
            double trackLengthinKm = completeLength / 1000;
            DecimalFormat df = new DecimalFormat("#.##");
            String completeLengthString = df.format(trackLengthinKm) + " Kilometer";
            return completeLengthString;
        } else {
            DecimalFormat df = new DecimalFormat("#.##");
            String completeLengthString = df.format(completeLength) + " Meter";
            return completeLengthString;

        }

    }

    /**
     * @return returns the entire Duration of all TrackSegments combined
     */

    public String getCompleteDuration() {
        ArrayList<TrackSegment> allSegments = getCompleteData();
        long completeDuration = 0;
        for (int i = 0; i < allSegments.size(); i++) {
            completeDuration += allSegments.get(i).calculateDuration();
        }
        String completeDuration2 = String.format("%02d:%02d:%02d", completeDuration / 3600, ((completeDuration % 3600) / 60), completeDuration % 60);
        return completeDuration2;
    }

    /**
     * receives messages from the Location Service
     */
    private class LocationHandler extends Handler {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            Location location = (Location) msg.obj;
            if (compareLocations(location)) {
                try {
                    handleLocation(location);
                    startDrawTrack(myMap);
                } catch (Exception e) {
                    Log.e("Error:", e.getMessage());
                }
            }
        }
    }

    /**
     * Method for creating a new Track, which can later be saved into the Database.
     *
     * @param purpose_ID chosen by user
     * @param mode_ID    chosen by user
     */
    private void createTrack(int purpose_ID, int mode_ID) {
        track = new Track(purpose_ID, mode_ID);
    }

    /**
     * Used for getting the Time when the track is started
     */
    public void start() {
        time = System.currentTimeMillis();
    }

    /**
     * Method for starting the Location Service. Creates the current Track for the recording.
     *
     * @param myMap      GoogleMap of HomeScreen
     * @param mode_ID    chosen by User
     * @param purpose_ID chosen by User
     */
    public void startLocationTracker(GoogleMap myMap, int mode_ID, int purpose_ID) {
        createTrack(purpose_ID, mode_ID);

        Intent startIntent = new Intent(context, LocationTracker.class);
        context.startService(startIntent);
        LocationTracker.updateHandler = handler;
        this.myMap = myMap;
    }

    /**
     * Method for stopping the Location Service. Finishes the Tracksegments and Track
     */
    public void stopLocationTracker() {
        Intent stopIntent = new Intent(context, LocationTracker.class);
        context.stopService(stopIntent);
        LocationTracker.updateHandler = null;
        finishTrack();//change
    }

    /**
     * Method for finishing the Track
     */
    private void finishTrack() {
        track.finish();
    }

    /**
     * Method for changing the current Mode. Doing so will finish the current
     * Tracksegment and start a new one.
     *
     * @param mode_ID the newly chosen Mode
     */
    public void changeMode(int mode_ID) {
        if (track != null) {
            track.changeMode(mode_ID);
        }
    }

    /**
     * Opens the Database to delete a Track specified by ID. Will also delete all TrackSegments assigned to this Track.
     *
     * @param track_ID Id of the Track to be deleted
     */
    public void deleteTrack(int track_ID) {
        trackSource.open();
        trackSource.deleteTrack(track_ID);
        trackSource.close();
        trackSegmentSource.open();
        trackSegmentSource.deleteCascade(track_ID);
        trackSegmentSource.close();
    }

    /**
     * Method for writing into the Database
     */
    public void writeToDatabase() {
        track.writeToDatabase(trackSource, trackSegmentSource, context);
    }

    /**
     * @return returns an ArrayList with all currently stored Data in the Database
     */
    public ArrayList readFromDatabase() {
        ArrayList allTracks;
        trackSource.open();
        allTracks = (ArrayList) trackSource.getAllTracks();
        trackSource.close();
        return allTracks;
    }

    /**
     * Draws the Track in a separated Activity
     *
     * @param myMap     Map of the current Activity
     * @param track     Track to be drawn
     * @param context   Context of current Activity
     * @param mapLayout Layout for the Map
     */
    public void drawCompleteTrack(GoogleMap myMap, Track track, Context context, LinearLayout mapLayout) {
        track.drawCompleteTrack(myMap, context, mapLayout);
    }

    /**
     * Method for setting Icons for Modes in TrackStats Activity
     *
     * @param track   Track to get Modes from
     * @param layout  Layout for the ImageViews
     * @param context Context of the Activity
     */
    public void setModeIcons(Track track, LinearLayout layout, Context context) {
        for (int i = 0; i < track.getSegments().size(); i++) {
            ImageView iv = new ImageView(context);
            int icon_ID = context.getResources().getIdentifier(modeSource.getModeIcon(track.getSegments().get(i).getMode_ID()), "drawable", context.getPackageName());
            iv.setImageResource(icon_ID);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
            iv.setLayoutParams(layoutParams);
            layout.addView(iv);
        }

    }

    /**
     * Method for changing the Purpose of an already saved Track. Used in TrackStats Activity
     *
     * @param track_ID   ID of the Track
     * @param purpose_ID Id of the new Purpose
     */
    public void changePurposeInDatabase(int track_ID, int purpose_ID) {
        trackSource.changePurposeInDatabase(purpose_ID, track_ID);
    }

    /**
     * Fills the Mode Table with Data. Table is used for storing information about Modes, including drawables
     * Happens once, when the application is opened for the very first time
     */
    public void fillModeTable() {
        modeNames = context.getResources().getStringArray(R.array.default_modes);
        modeIcons = context.getResources().getStringArray(R.array.default_mode_icons);
        if (!modeSource.isTableFull("modes", true)) {
            modeSource.open();
            for (int i = 0; i < modeNames.length; i++) {
                modeSource.createModeSource(modeNames[i], modeIcons[i]);
            }
            modeSource.close();
        }
    }

    /**
     * Fills the Purpose Table with Data. Table is used for storing information about Purposes, including drawables
     * Happens once, when the application is opened for the very first time
     */
    public void fillPurposeTable() {
        purposeNames = context.getResources().getStringArray(R.array.default_purposes);
        purposeIcons = context.getResources().getStringArray(R.array.default_purpose_icons);
        if (!purposeSource.isTableFull("purposes", true)) {
            purposeSource.open();
            for (int i = 0; i < purposeNames.length; i++) {
                purposeSource.createPurposeSource(purposeNames[i], purposeIcons[i]);
            }
            purposeSource.close();
        }
    }

    /**
     * @param track   Track to get Purpose Icon ID from
     * @param context
     * @return returns the ID of the drawable of the Purpose Icon of the Track
     */
    public int getPurposeIcon(Track track, Context context) {
        int iconId;
        iconId = context.getResources().getIdentifier(purposeSource.getPurposeIcon(track.getPurpose()), "drawable", context.getPackageName());
        return iconId;
    }

    /**
     * Assigning TrackSegments to the Track
     *
     * @param track
     * @param context
     */
    public void setTrackSegments(Track track, Context context) {
        track.setSegments(context, (ArrayList) trackSegmentSource.getAllTrackSegmentsByTrack(track.getTrack_ID()));
    }

    /**
     * @param track
     * @return returns formatted date of the Track
     */
    public String getDate(Track track) {
        return track.getDate();
    }

    /**
     * @param track
     * @return returns formatted length of the Track in either 'm' oder 'km'
     */
    public String getLength(Track track) {
        return track.calculateTrackLength();
    }

    /**
     * @param track
     * @return returns formatted Duration of the Track
     */

    public String getDuration(Track track) {
        return track.calculateTrackDuration();
    }

    /**
     * Method for comparing the received location with the last one
     *
     * @param location
     * @return returns true if the distance between the two Locations is atleast 50m
     */

    private boolean compareLocations(Location location) {
        double distance = 0;
        if (oldLocation == null) {
            oldLocation = location;
        }
        newLocation = location;
        if (oldLocation != newLocation) {
            distance = newLocation.distanceTo(oldLocation);
        }
        if (distance > 0.5) {
            oldLocation = newLocation;
            return true;
        }
        return false;
    }

    /**
     * Method for adding the Location to the Arraylist of the TrackSegment and animating the camera
     *
     * @param location
     */
    private void handleLocation(Location location) {
        if (track != null) {
            track.addLocationToSegment(location);
        }
        LatLng currentPos = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentPos, 16);
        myMap.animateCamera(cameraUpdate);
    }

    /**
     * Method for drawing the current recording on the Goggle Map
     *
     * @param myMap GoogleMap to draw on
     */
    private void startDrawTrack(GoogleMap myMap) {
        track.drawTrack(myMap);
    }

    /**
     * Deletes local Data after a Track is saved
     */
    public void dismissData() {
        track.deleteLocals();
        track = null;
        myMap.clear();
    }
}
