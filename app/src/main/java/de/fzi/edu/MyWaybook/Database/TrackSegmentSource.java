package de.fzi.edu.MyWaybook.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import de.fzi.edu.MyWaybook.Helper.DataBaseHelper;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * This Class provides Methods for connecting the Class "TrackSegment" with the Database
 * Created by rickert on 16.01.2017.
 */

public class TrackSegmentSource {

    private SQLiteDatabase database;
    private DataBaseHelper databaseHelper;
    private String[] allColumns ={DataBaseHelper.COLUMN_SEGMENT_ID,DataBaseHelper.COLUMN_MODE,DataBaseHelper.COLUMN_STARTTIME,DataBaseHelper.COLUMN_ENDTIME,DataBaseHelper.COLUMN_LENGTH, DataBaseHelper.COLUMN_TRACK};
    private String LOG_TAG;

    /**
     * Method for creating an instance of the DataBaseHelper Class
     * @param context
     */

    public TrackSegmentSource(Context context) {
        databaseHelper = DataBaseHelper.getInstance(context);
    }

    /**
     * Method for opening the Database
     */

    public void open(){
        database = databaseHelper.getWritableDatabase();
        database.setForeignKeyConstraintsEnabled(true);

    }

    /**
     * Method for closing the Database
     */
    public void close(){databaseHelper.close();
    }

    /**
     * Method for creating a Database Entry of a TrackSegment
     * @param modeID mode of the TrackSegment
     * @param start starttime of the TrackSegment
     * @param end endtime of the TrackSegment
     * @param length length of the TrackSegment
     * @param trackID ID of the corresponding Track
     * @return returns a Tracksegment with all parameters
     */

    public TrackSegment createTrackSegmentSource(int modeID, Timestamp start, Timestamp end, double length, long trackID){
        ContentValues values = new ContentValues();
        values.put(DataBaseHelper.COLUMN_MODE, modeID);
        values.put(DataBaseHelper.COLUMN_STARTTIME, String.valueOf(start));
        values.put(DataBaseHelper.COLUMN_ENDTIME, String.valueOf(end));
        values.put(DataBaseHelper.COLUMN_LENGTH, length);
        values.put(DataBaseHelper.COLUMN_TRACK,trackID);

        long insertId = database.insert(DataBaseHelper.TABLE_SEGMENTS,null,values);
        Cursor cursor = database.query(DataBaseHelper.TABLE_SEGMENTS, allColumns, DataBaseHelper.COLUMN_SEGMENT_ID+"="+insertId,null,null,null,null);
        cursor.moveToFirst();
        TrackSegment newTrackSegment = cursorToTrackSegment(cursor);
        cursor.close();
        Log.d(LOG_TAG, "DataSourceHelper: TrackSegment " + newTrackSegment.getSegment_ID() + " erzeugt");
        return newTrackSegment;
    }

    /**
     * Method for deleting a single TrackSegment (not in use yet)
     * @param trackSegment
     */

    public void deleteTrackSegment(TrackSegment trackSegment){
        long id = trackSegment.getSegment_ID();
        database.delete(DataBaseHelper.TABLE_SEGMENTS, DataBaseHelper.COLUMN_SEGMENT_ID+"="+id, null);
    }

    /**
     * Deletes all entries in the table with a specific Track ID
     * @param track_ID Id of the Track the Segment is assigned to
     */
    public void deleteCascade(int track_ID){
        database.delete(DataBaseHelper.TABLE_SEGMENTS, DataBaseHelper.COLUMN_TRACK+"="+track_ID, null);

    }

    /**
     *
     * @param track_ID
     * @return returns a ArrayList with Tracksegments from a Track specified by Track ID
     */
    public List<TrackSegment> getAllTrackSegmentsByTrack(int track_ID){
        ArrayList<TrackSegment> trackSegmentList = new ArrayList<TrackSegment>();


        String query = "SELECT * FROM " + DataBaseHelper.TABLE_SEGMENTS + " WHERE " + DataBaseHelper.COLUMN_TRACK + " =  \"" + track_ID + "\"";

        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                TrackSegment trackSegment = cursorToTrackSegment(cursor);
                trackSegmentList.add(trackSegment);
            } while (cursor.moveToNext());
            cursor.close();
            db.close();
        }
        return trackSegmentList;
    }

    /**
     *
     * @param track_ID
     * @return returns a String formatted as a Date from a Tracksegment specified by Track ID
     */

    public String getDateOfTrack(int track_ID){
        Timestamp date = null;
        String query = "SELECT * FROM " + DataBaseHelper.TABLE_SEGMENTS + " WHERE " + DataBaseHelper.COLUMN_TRACK + " =  \"" + track_ID + "\"";

        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(query,null);
        if(cursor!=null){
            cursor.moveToFirst();
            date = Timestamp.valueOf(cursor.getString(3));
        }
        cursor.close();
        db.close();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        String date2 = dateFormat.format(date);
        return date2;
    }


    private TrackSegment cursorToTrackSegment(Cursor cursor){
        TrackSegment trackSegment = new TrackSegment();
        trackSegment.setSegment_ID(cursor.getLong(0));
        trackSegment.setMode_ID(cursor.getInt(1));
        trackSegment.setStartTime(Timestamp.valueOf(cursor.getString(2)));
        trackSegment.setEndTime(Timestamp.valueOf(cursor.getString(3)));
        trackSegment.setLength(cursor.getDouble(4));
        trackSegment.setTrack_ID(cursor.getInt(5));
        return trackSegment;

    }

    /**
     *
     * @return returns an ArrayList containing all Segments
     */

    public List<TrackSegment> getAllSegments() {
        List<TrackSegment> tracksegments = new ArrayList<TrackSegment>();
        Cursor cursor = database.query(DataBaseHelper.TABLE_SEGMENTS,allColumns,null,null,null,null,null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            TrackSegment tracksegment = cursorToTrackSegment(cursor);
            tracksegments.add(tracksegment);
            cursor.moveToNext();
        }
        cursor.close();
        return tracksegments;
    }
}
