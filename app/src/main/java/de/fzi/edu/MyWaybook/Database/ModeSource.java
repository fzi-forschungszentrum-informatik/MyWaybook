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
 * This Class provides Methods for connecting the Class "Mode" with the database.
 * Created by rickert on 17.01.2017.
 */

public class ModeSource {

    private SQLiteDatabase database;
    private DataBaseHelper databaseHelper;
    private String[] allColumns = {DataBaseHelper.COLUMN_MODE_ID, DataBaseHelper.COLUMN_MODE_NAME, DataBaseHelper.COLUMN_MODE_ICON};
    private String LOG_TAG;

    public ModeSource(Context context) {
        databaseHelper = DataBaseHelper.getInstance(context);
    }

    public void open() {
        database = databaseHelper.getWritableDatabase();

    }

    public void close() {
        databaseHelper.close();
    }

    /**
     * Method for filling the Mode Table with data.
     * @param modeName selected Name to insert
     * @param modeIcon selected Icon to insert
     * @return TODO: not sure
     */
    public Mode createModeSource(String modeName, String modeIcon) {
        ContentValues values = new ContentValues();
        values.put(DataBaseHelper.COLUMN_MODE_NAME, modeName);
        values.put(DataBaseHelper.COLUMN_MODE_ICON, modeIcon);

        long insertId = database.insert(DataBaseHelper.TABLE_MODES, null, values);
        Cursor cursor = database.query(DataBaseHelper.TABLE_MODES, allColumns, DataBaseHelper.COLUMN_MODE_ID + "=" + (int)insertId, null, null, null, null);
        cursor.moveToFirst();
        Mode newMode = cursorToMode(cursor);
        cursor.close();
        Log.d(LOG_TAG, "DataSourceHelper: Mode '" + newMode.getMode_Name() + "' created");
        return newMode;
    }

    /**
     * cursor Method
     * @param cursor
     * @return
     */

    private Mode cursorToMode(Cursor cursor) {
        Mode mode = new Mode();
        mode.setMode_ID(cursor.getInt(0));
        mode.setMode_Name(cursor.getString(1));
        mode.setMode_Icon(cursor.getString(2));
        return mode;

    }

    /**
     *
     * @return returns a List with all modes in the table
     */

    public List<Mode> getAllModes() {
        List<Mode> modes = new ArrayList<Mode>();
        Cursor cursor = database.query(DataBaseHelper.TABLE_MODES, allColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Mode mode = cursorToMode(cursor);
            modes.add(mode);
            cursor.moveToNext();
        }
        cursor.close();
        return modes;
    }

    /**
     *
     * @param mode_ID
     * @return returns a specific Icon ID
     */

    public String getModeIcon (int mode_ID){
        String modeIcon = null;
        String query = "SELECT * FROM " + DataBaseHelper.TABLE_MODES + " WHERE " + DataBaseHelper.COLUMN_MODE_ID + " =  \"" + mode_ID + "\"";

        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(query,null);
        if(cursor!=null){
            cursor.moveToFirst();
            modeIcon = cursor.getString(2);
        }
        cursor.close();
        db.close();
        return modeIcon;
    }

    /**
     * boolean check if the Table is full. Checks if the database is open and if there are entries in it
     * If not, fills the Table with Data otherwise does nothing
     * @param tableName name of the table to be checked
     * @param openDb true if database is open
     * @returns true if Table has data already
     */

    public boolean isTableFull(String tableName, boolean openDb) {
        if (openDb) {
            if (database == null || !database.isOpen()) {
                database = databaseHelper.getReadableDatabase();
            }

            if (!database.isReadOnly()) {
                database.close();
                database = databaseHelper.getReadableDatabase();
            }
        }

        Cursor cursor = database.query(tableName,allColumns,null,null,null,null,null);

        if (cursor.getCount() > 0) {
            cursor.close();
            Log.d(LOG_TAG, "Table with modes already exists.");
            return true;

        } else {
            cursor.close();
            Log.d(LOG_TAG, "Table with modes is empty. Filling Table");
            return false;
        }

    }
}
