package de.fzi.edu.MyWaybook.Database;

import android.location.Location;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * This Class provides Methods for interacting with TrackSegments.
 * Created by rickert on 19.12.2016.
 */

public class TrackSegment {


    private long segment_ID;
    private int mode_ID;
    private Timestamp startTime;
    private Timestamp endTime;
    private long time;
    private double length;
    private Location lastLocation;
    private int track_ID;



    private ArrayList<Location> segmentPoints = new ArrayList<>();

    //constructors

    /**
     * used by the GPX-Reader
     */

    public TrackSegment() {

    }

    /**
     * Constructor for creating a TrackSegment.
     * Sets the chosen Mode and Starttime.
     * @param mode_ID chosen Mode
     */
    public TrackSegment(int mode_ID) {
        setMode_ID(mode_ID);
        time = System.currentTimeMillis();
        Timestamp start = new Timestamp(time);
        setStartTime(start);
    }

    /**
     * Method for finishing a TrackSegment.
     * Sets EndTime and Length of the Segment.
     */
    public void finish() {
        time = System.currentTimeMillis();
        Timestamp end = new Timestamp(time);
        setEndTime(end);
        setLength(calculateLength());


    }

    public void addPoints(Location location) {
        segmentPoints.add(location);
    }

    /**
     * Method for calculating Length of a Segment
     * @return Length as Double
     */
    private double calculateLength() {
        double length = 0;
        Location currentLocation = null;
        Location lastLocation = null;
        if (segmentPoints.size() > 0) {
            for (int i = 0; i < segmentPoints.size(); i++) {

                //Update track length

                currentLocation = segmentPoints.get(i);

                if (lastLocation == null) {
                    lastLocation = currentLocation;
                }


                if (currentLocation != lastLocation) {
                    length = length + currentLocation.distanceTo(lastLocation);
                    lastLocation = currentLocation;
                }
            }
        }
        return length;

    }

    /**
     *
     * @return returns Last Location of the Segment
     */

    public Location getLastLocation(){
        Location lastLocation;
        if(segmentPoints.size()>0) {
            lastLocation = segmentPoints.get(segmentPoints.size() - 1);

            return lastLocation;
        }
        return null;

    }

    /**
     * Method for calculating the DUration of a Segment
     * @return Duration in Double
     */
    public double calculateDuration() {
        double duration;
        duration = endTime.getTime() - startTime.getTime();
        duration = duration/1000;
        DecimalFormat df = new DecimalFormat("#");
        df.format(duration);
        return duration;
    }

    //setter and getter
    public void setSegmentPoints(ArrayList<Location> segmentPoints) {
        this.segmentPoints = segmentPoints;
    }

    public ArrayList<Location> getSegmentPoints() {
        return segmentPoints;
    }

    public int getTrack_ID() {
        return track_ID;
    }

    public void setTrack_ID(int track_ID) {
        this.track_ID = track_ID;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public int getMode_ID() {
        return mode_ID;
    }

    public void setMode_ID(int mode_ID) {
        this.mode_ID = mode_ID;
    }

    public long getSegment_ID() {
        return segment_ID;
    }

    public void setSegment_ID(long segment_ID) {
        this.segment_ID = segment_ID;
    }



    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public Timestamp getStartTime() {
        return startTime;
    }


}
