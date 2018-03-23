package de.fzi.edu.MyWaybook.Helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import de.fzi.edu.MyWaybook.Database.Track;

/**
 * Helper Class for building the Database
 * Created by rickert on 25.11.2016.
 */
public class DataBaseHelper extends SQLiteOpenHelper {

    //Logcat tag
    private static final String LOG_TAG = DataBaseHelper.class.getSimpleName();
    //Database name
    private static final String DB_NAME = "tracks.db";
    //Database Version
    private static final int DB_VERSION = 10;

    //Singleton Instance
    private static DataBaseHelper sInstance;

    //Table Names
    public static final String TABLE_TRACKS = "tracks";
    public static final String TABLE_SEGMENTS = "segments";
    public static final String TABLE_MODES = "modes";
    public static final String TABLE_PURPOSES = "purposes";


    //TRACKS Table - column names
    public static final String COLUMN_TRACK_ID = "ID";
    public static final String COLUMN_PURPOSE = "Purpose";


    //SEGMENTS Table - column names
    public static final String COLUMN_SEGMENT_ID = "ID";
    public static final String COLUMN_MODE = "Transportmode";
    public static final String COLUMN_STARTTIME = "Starttime";
    public static final String COLUMN_ENDTIME = "Endtime";
    public static final String COLUMN_LENGTH = "Length";
    public static final String COLUMN_TRACK = "Track_ID";

    //MODES Table - column names
    public static final String COLUMN_MODE_ID = "ID";
    public static final String COLUMN_MODE_NAME = "Transportmode";
    public static final String COLUMN_MODE_ICON = "Icon";

    //PURPOSES Table - column names
    public static final String COLUMN_PURPOSE_ID = "ID";
    public static final String COLUMN_PURPOSE_NAME = "Purpose";
    public static final String COLUMN_PURPOSE_ICON = "Icon";


    //Table Create statements

    //TRACKS Table create statements
    public static final String SQL_CREATE_TABLE_TRACKS =
            "CREATE TABLE " + TABLE_TRACKS + "(" +
                    COLUMN_TRACK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_PURPOSE + " INTEGER); ";


    //SEGMENTS Table create statements
    public static final String SQL_CREATE_TABLE_SEGMENTS =
            "CREATE TABLE " + TABLE_SEGMENTS + "(" +
                    COLUMN_SEGMENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_MODE + " INTEGER, " +
                    COLUMN_STARTTIME + " TIMESTAMP, " +
                    COLUMN_ENDTIME + " TIMESTAMP, " +
                    COLUMN_LENGTH + " DOUBLE ," +
                    COLUMN_TRACK + " INTEGER," +
                    "FOREIGN KEY (" + COLUMN_TRACK + ") REFERENCES " + TABLE_TRACKS + " (" + COLUMN_TRACK_ID + "));";

    //MODES Table create statements
    public static final String SQL_CREATE_TABLE_MODES =
            "CREATE TABLE " + TABLE_MODES + "(" +
                    COLUMN_MODE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_MODE_NAME + " STRING, " +
                    COLUMN_MODE_ICON + " STRING); ";

    //PURPOSES Table create statements
    public static final String SQL_CREATE_TABLE_PURPOSES =
            "CREATE TABLE " + TABLE_PURPOSES + "(" +
                    COLUMN_PURPOSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_PURPOSE_NAME + " STRING, " +
                    COLUMN_PURPOSE_ICON + " STRING); ";


    public static synchronized  DataBaseHelper getInstance(Context context){
        if(sInstance == null){
            sInstance = new DataBaseHelper(context);

        }
        return sInstance;
    }


    private DataBaseHelper(Context context) {

        super(context, DB_NAME, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {


        try {
            db.execSQL(SQL_CREATE_TABLE_TRACKS);
            db.execSQL(SQL_CREATE_TABLE_SEGMENTS);
            db.execSQL(SQL_CREATE_TABLE_MODES);
            db.execSQL(SQL_CREATE_TABLE_PURPOSES);
            Log.e(LOG_TAG, "Database created");


        } catch (Exception ex) {
            Log.e(LOG_TAG, "Error while creating Database: " + ex.getMessage());

        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


        Log.w(DataBaseHelper.class.getName(), "Upgrading database from version "
                + oldVersion
                + "to "
                + newVersion
                + ", which destroys all data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRACKS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SEGMENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MODES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PURPOSES);

        onCreate(db);

    }





    public int updateTrack(Track track){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TRACK_ID, track.getTrack_ID());
        values.put(COLUMN_PURPOSE, track.getPurpose());

        return db.update(TABLE_TRACKS,values,COLUMN_TRACK_ID+"=?",
                new String[]{String.valueOf(track.getTrack_ID())});

    }




}


