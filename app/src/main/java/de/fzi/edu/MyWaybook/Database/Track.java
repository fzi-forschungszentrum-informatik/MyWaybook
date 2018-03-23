package de.fzi.edu.MyWaybook.Database;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import de.fzi.edu.MyWaybook.Helper.GPXReader;
import de.fzi.edu.MyWaybook.Helper.GPXWriter;
import de.fzi.edu.MyWaybook.R;



import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.File;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Class for Tracks. Provides methods for interaction with Tracks.
 * Created by rickert on 19.12.2016.
 */

public class Track implements Serializable {

    private int track_ID;
    private int purpose_ID;
    private ArrayList<TrackSegment> segments = new ArrayList<>();
    private Polyline line;
    private Location lastLocation;
    private TrackSegment segment;

    //constructors

    public Track() {
    }

    /**
     * Constructor used for creating a Track. Sets the chosen Purpose and creates a new Segment afterwards. A new Track will always create a new Segment.
     *
     * @param purpose_ID
     * @param mode_ID
     */
    public Track(int purpose_ID, int mode_ID) {
        setPurpose(purpose_ID);
        createTrackSegment(mode_ID);
    }

    /**
     * Adds the new Location to the current Segment
     *
     * @param location
     */

    public void addLocationToSegment(Location location) {
        if (segment != null) {
            segment.addPoints(location);
        }
    }
    /**
     * Method for changing modes
     *
     * @param mode_ID
     */

    public void changeMode(int mode_ID) {
        if (segment != null) {
            finishSegment();
            createTrackSegment(mode_ID);
        }
    }

    public String getDate() {
        String date;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        date = dateFormat.format(segments.get(0).getStartTime());
        return date;
    }
    /**
     * Method for calculating the length of a Track
     * Sums up all lengths
     *
     * @return returns a length in 'm' or 'km' as a String
     */

    public String calculateTrackLength() {
        double trackLength = 0;
        for (int i = 0; i < segments.size(); i++) {
            trackLength += segments.get(i).getLength();
        }
        if (trackLength > 1000) {
            double trackLengthInKm = trackLength / 1000;
            DecimalFormat df = new DecimalFormat("#.##");
            String trackLengthString = df.format(trackLengthInKm) + " Kilometer";
            return trackLengthString;
        } else {
            DecimalFormat df = new DecimalFormat("#.##");
            String trackLengthString = df.format(trackLength) + " Meter";
            return trackLengthString;
        }
    }
    /**
     * Method for calculating the DUration of a Track
     * Sums up all durations
     *
     * @return returns a String
     */

    public String calculateTrackDuration() {
        long trackDuration = 0;
        for (int i = 0; i < segments.size(); i++) {
            trackDuration += segments.get(i).calculateDuration();
        }
        String trackDuration2 = String.format("%02d:%02d:%02d", trackDuration / 3600, ((trackDuration % 3600) / 60), trackDuration % 60);
        return trackDuration2;
    }

    /**
     * Method for creating a TrackSegment.
     * If it is not the first Segment created, the last Position of the previous Segment is set as the first
     * Position in this Segment.
     *
     * @param mode_ID chosen Mode
     */

    private void createTrackSegment(int mode_ID) {
        if (segment == null) {
            segment = new TrackSegment(mode_ID);
            if (segments.size() > 0) {
                segment.addPoints(lastLocation);
            }
        }
    }

    /**
     * Method for writing to the Database. Writes Tracks and Segments to the Database.
     * Creates a .gpx File to store Location Data.
     *
     * @param trackSource
     * @param trackSegmentSource
     * @param context
     */

    public void writeToDatabase(TrackSource trackSource, TrackSegmentSource trackSegmentSource, Context context) {
        writeTrackToDatabase(trackSource);
        writeSegmentsToDatabase(trackSegmentSource);
        File gpxFileFolder = new File(context.getFilesDir(), "tracks");
        if(!gpxFileFolder.exists()){
            gpxFileFolder.mkdirs();
        }
        File gpxFile = new File(context.getFilesDir()+ "/tracks", track_ID+".gpx");

        GPXWriter.writePath(gpxFile, "Track " + track_ID, segments);
    }

    /**
     * Method for writing a Track to the Database.
     * Sets Track ID after writing to the Database.
     *
     * @param trackSource given by TrackHandler
     */

    private void writeTrackToDatabase(TrackSource trackSource) {
        trackSource.open();
        trackSource.createTrackSource(getPurpose());
        Log.i("TAG","Track saved");
        setTrack_ID(trackSource.getCurrentTrack());
        trackSource.close();
        addTrackIdToSegments(track_ID);
    }

    /**
     * Method for writing a Track Segment to the Database.
     * Writes all segments into the Database.
     *
     * @param trackSegmentSource given by TrackHandler
     */

    private void writeSegmentsToDatabase(TrackSegmentSource trackSegmentSource) {
        trackSegmentSource.open();
        for (int i = 0; i < segments.size(); i++) {
            TrackSegment loopSegment = segments.get(i);
            trackSegmentSource.createTrackSegmentSource(loopSegment.getMode_ID(), loopSegment.getStartTime(), loopSegment.getEndTime(), loopSegment.getLength(), loopSegment.getTrack_ID());
        }
        Log.i("TAG","Segemnts saved");
        trackSegmentSource.close();
    }

    /**
     * Sets Track ID for the current Segment
     *
     * @param trackID
     */
    private void addTrackIdToSegments(int trackID) {
        for (int i = 0; i < segments.size(); i++) {
            segments.get(i).setTrack_ID(trackID);
        }
    }

    /**
     * Method for drawing the Track in the HomeScreen Activity
     *
     * @param myMap
     */
    public void drawTrack(GoogleMap myMap) {
        double lat;
        double lon;
        PolylineOptions options = new PolylineOptions().width(15).color(Color.RED);
        for (int i = 0; i < segment.getSegmentPoints().size(); i++) {
            Location location = segment.getSegmentPoints().get(i);
            lat = location.getLatitude();
            lon = location.getLongitude();
            LatLng latlng = new LatLng(lat, lon);
            options.add(latlng);
        }
        line = myMap.addPolyline(options);
    }

    /**
     * Method for drawing the Track in the TrackStats Activity. Uses different Colors for different Modes.
     * Adds Marker at Start and End
     *
     * @param map       Map on the TrackStats Activity
     * @param context   context of the Activity
     * @param mapLayout mapLayout of the TrackStats Activity
     */

    public void drawCompleteTrack(GoogleMap map, Context context, LinearLayout mapLayout) {
        if (segments.get(0).getSegmentPoints().size() < 2) {
            mapLayout.setVisibility(View.INVISIBLE);
        } else {
            Location startLocation = segments.get(0).getSegmentPoints().get(0);
            LatLng startLatLng = new LatLng(startLocation.getLatitude(), startLocation.getLongitude());
            ArrayList<Location> list = segments.get(segments.size() - 1).getSegmentPoints();
            Location endLocation = list.get(list.size() - 1);
            LatLng endLatLng = new LatLng(endLocation.getLatitude(), endLocation.getLongitude());
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (int j = 0; j < segments.size(); j++) {
                TrackSegment temporarySegment = segments.get(j);
                int mode = temporarySegment.getMode_ID();
                switch (mode) {
                    case 1:
                        //Material Design Green 500
                        PolylineOptions lineWalking = new PolylineOptions().width(15).color(Color.parseColor("#4CAF50"));
                        for (int i = 0; i < temporarySegment.getSegmentPoints().size(); i++) {
                            drawMode(temporarySegment, i, lineWalking, builder);
                        }
                        map.addPolyline(lineWalking);
                        break;
                    case 2:
                        //Material Design Light Green 500
                        PolylineOptions lineBike = new PolylineOptions().width(15).color(Color.parseColor("#8BC34A"));
                        for (int i = 0; i < temporarySegment.getSegmentPoints().size(); i++) {
                            drawMode(temporarySegment, i, lineBike, builder);
                        }
                        map.addPolyline(lineBike);
                        break;
                    case 3:
                        //Material Design Orange 500
                        PolylineOptions lineDriver = new PolylineOptions().width(15).color(Color.parseColor("#FF9800"));
                        for (int i = 0; i < temporarySegment.getSegmentPoints().size(); i++) {
                            drawMode(temporarySegment, i, lineDriver, builder);
                        }
                        map.addPolyline(lineDriver);
                        break;
                    case 4:
                        //Material Design Yellow 500
                        PolylineOptions linePassenger = new PolylineOptions().width(15).color(Color.parseColor("#4CAF50"));
                        for (int i = 0; i < temporarySegment.getSegmentPoints().size(); i++) {
                            drawMode(temporarySegment, i, linePassenger, builder);
                        }
                        map.addPolyline(linePassenger);
                        break;
                    case 5:
                        //Material Design Amber 500
                        PolylineOptions lineBus = new PolylineOptions().width(15).color(Color.parseColor("#FFC107"));
                        for (int i = 0; i < temporarySegment.getSegmentPoints().size(); i++) {
                            drawMode(temporarySegment, i, lineBus, builder);
                        }
                        map.addPolyline(lineBus);
                        break;
                    case 6:
                        //Material Design Red 500
                        PolylineOptions lineTrain = new PolylineOptions().width(15).color(Color.parseColor("#F44336"));
                        for (int i = 0; i < temporarySegment.getSegmentPoints().size(); i++) {
                            drawMode(temporarySegment, i, lineTrain, builder);
                        }
                        map.addPolyline(lineTrain);
                        break;
                    case 7:
                        //Material Design Deep Orange 500
                        PolylineOptions lineTram = new PolylineOptions().width(15).color(Color.parseColor("#FF5722"));
                        for (int i = 0; i < temporarySegment.getSegmentPoints().size(); i++) {
                            drawMode(temporarySegment, i, lineTram, builder);
                        }
                        map.addPolyline(lineTram);
                        break;
                }
            }
            map.addMarker(new MarkerOptions().position(startLatLng).title("Start").icon(BitmapDescriptorFactory.fromResource(R.drawable.icona)));
            map.addMarker(new MarkerOptions().position(endLatLng).title("Ende").icon(BitmapDescriptorFactory.fromResource(R.drawable.iconb)));
            map.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 60));
        }
    }

    /**
     * Method used in drawCompleteTrack.
     * Used for building specific PolyLines
     *
     * @param segment  currently selected Segment
     * @param i        current Iteration
     * @param lineMode currently selected Mode
     * @param builder  builder
     */

    private void drawMode(TrackSegment segment, int i, PolylineOptions lineMode, LatLngBounds.Builder builder) {
        double lat = segment.getSegmentPoints().get(i).getLatitude();
        double lon = segment.getSegmentPoints().get(i).getLongitude();
        LatLng latLng = new LatLng(lat, lon);
        lineMode.add(latLng);
        builder.include(latLng);
    }

    /**
     * Used as public method for finishing Segments.
     */
    public void finish() {
        finishSegment();
    }

    /**
     * Private method for finishing a Segment. Adds segments to the Array of the Track and reads the last Location.
     */
    private void finishSegment() {
        segment.finish();
        segments.add(segment);
        lastLocation = segment.getLastLocation();
        segment = null;
    }

    /**
     * Method for clearing local data, mainly segments.
     */
    public void deleteLocals() {
        segments.clear();
    }

    /**
     * Method for setting the ID of a Track
     *
     * @param track_ID ID for the Track
     */
    public void setTrack_ID(int track_ID) {
        this.track_ID = track_ID;
    }

    /**
     * Method for setting the Purpose ID of a Track
     *
     * @param purpose_ID Purpose ID for the Track
     */
    public void setPurpose(int purpose_ID) {
        this.purpose_ID = purpose_ID;
    }

    /**
     * Method for getting the ID of a Track
     *
     * @return returns Track ID as int
     */
    public int getTrack_ID() {
        return track_ID;
    }

    /**
     * Method for getting the Purpose ID of a Track
     *
     * @return returns Purpose ID as int
     */
    public int getPurpose() {
        return purpose_ID;
    }

    /**
     * Method for getting all the segments held by the Track
     *
     * @return returns and Array of segments
     */
    public ArrayList<TrackSegment> getSegments() {
        return segments;
    }

    /**
     * Method for reading the .gpx File in the TrackStats Activity and getting Data from Database.
     *
     * @param context      context of the Activity
     * @param segmentArray Array to be used
     */

    public void setSegments(Context context, ArrayList<TrackSegment> segmentArray) {
        this.segments = segmentArray;
        String path = context.getFilesDir().toString() + "/tracks/" + getTrack_ID() + ".gpx";
        File file = new File(path);
        ArrayList<TrackSegment> temporarySegments = GPXReader.readGPX(file);
        for (int i = 0; i < temporarySegments.size(); i++) {
            segments.get(i).setSegmentPoints(temporarySegments.get(i).getSegmentPoints());
        }
    }

}
