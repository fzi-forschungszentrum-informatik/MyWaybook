package de.fzi.edu.MyWaybook.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import de.fzi.edu.MyWaybook.Helper.DataBaseHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rickert on 16.01.2017.
 */

public class TrackSource {

    private SQLiteDatabase database;
    private DataBaseHelper databaseHelper;
    private String[] allColumns ={DataBaseHelper.COLUMN_TRACK_ID, DataBaseHelper.COLUMN_PURPOSE};
    private String LOG_TAG;
    private boolean isTracknew = true;

    /**
     * Method for creating an Instance of the DataBase Helper Class
     * @param context
     */

    public TrackSource (Context context) {
        databaseHelper = DataBaseHelper.getInstance(context);
        }

    /**
     * Method for opening the Database
     */
    public void open(){
        database = databaseHelper.getWritableDatabase();
    }

    /**
     * Method for closing the Database
     */
    public void close(){databaseHelper.close();
    }

    /**
     * Method for creating a Database entry for creating a Track
     * @param purpose_ID
     * @return Track with all parameters
     */

    public Track createTrackSource(int purpose_ID){
        ContentValues values = new ContentValues();
        values.put(DataBaseHelper.COLUMN_PURPOSE, purpose_ID);

        long insertId = database.insert(DataBaseHelper.TABLE_TRACKS,null,values);
        Cursor cursor = database.query(DataBaseHelper.TABLE_TRACKS, allColumns, DataBaseHelper.COLUMN_TRACK_ID+"="+insertId,null,null,null,null);

        cursor.moveToFirst();
        Track newTrack = cursorToTrack(cursor);
        cursor.close();
        Log.d(LOG_TAG, "DataSourceHelper: Track " + newTrack.getTrack_ID() + " erzeugt");


        return newTrack;
    }

    /**
     * Method for changing the Purpose of a Track that is already in the Database
     * @param purpose_ID ID of the new purpose
     * @param track_ID ID of the Track to be changed
     */

    public void changePurposeInDatabase(int purpose_ID, int track_ID){
        ContentValues values = new ContentValues();

        values.put(DataBaseHelper.COLUMN_PURPOSE, purpose_ID);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        db.update(DataBaseHelper.TABLE_TRACKS,values,"ID=" +track_ID,null);
        db.close();
    }

    /**
     * Method for deleting a single Track from the Database
     * @param track_ID ID of the Track to be deleted
     */
    public void deleteTrack(int track_ID){
        database.delete(DataBaseHelper.TABLE_TRACKS, DataBaseHelper.COLUMN_TRACK_ID+"="+track_ID, null);
    }

    /**
     * Method for getting all Tracks stored in the Database
     * @return retunrs a List of all Tracks
     */

    public List<Track> getAllTracks(){
        List<Track> tracks = new ArrayList<Track>();
            Cursor cursor = database.query(DataBaseHelper.TABLE_TRACKS,allColumns,null,null,null,null,null);
            cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            Track track = cursorToTrack(cursor);
            tracks.add(track);
            cursor.moveToNext();
        }
        cursor.close();
        return tracks;

    }

    /**
     * Method for extracting the ID of a Track
     * @return ID of the Track to be saved
     */

    public int getCurrentTrack(){
        int currentTrackID;
        Cursor cursor = database.query(DataBaseHelper.TABLE_TRACKS,allColumns,null,null,null,null,null);
        cursor.moveToLast();
        Track track = cursorToTrack(cursor);
        currentTrackID = track.getTrack_ID();
        cursor.close();
        return currentTrackID;

    }

    /**
     * crusor method for the Track
     * @param cursor
     * @return returns a Track
     */


    private Track cursorToTrack(Cursor cursor){
        Track track = new Track();
        track.setTrack_ID(cursor.getInt(0));
        track.setPurpose(cursor.getInt(1));
        return track;

    }
}

