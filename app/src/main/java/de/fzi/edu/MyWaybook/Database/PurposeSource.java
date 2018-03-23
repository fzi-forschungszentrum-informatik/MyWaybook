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
 * This Class provides Methods for connecting the Class "Purpose" with the database.
 *
 * Created by rickert on 18.01.2017.
 */

public class PurposeSource {

    private SQLiteDatabase database;
    private DataBaseHelper databaseHelper;
    private String[] allColumns = {DataBaseHelper.COLUMN_PURPOSE_ID, DataBaseHelper.COLUMN_PURPOSE_NAME, DataBaseHelper.COLUMN_PURPOSE_ICON};
    private String LOG_TAG;

    public PurposeSource(Context context) {
        databaseHelper = DataBaseHelper.getInstance(context);
    }

    /**
     * Method for opening the Database
     */
    public void open() {
        database = databaseHelper.getWritableDatabase();

    }

    /**
     * Method for closing the Database
     */

    public void close() {
        databaseHelper.close();
    }

    /**
     * Method for filling the Prupose tble with Data
     * @param purposeName selected Name to insert
     * @param purposeIcon selected Name to insert
     * @return
     */
    public Purpose createPurposeSource(String purposeName, String purposeIcon) {
        ContentValues values = new ContentValues();
        values.put(DataBaseHelper.COLUMN_PURPOSE_NAME, purposeName);
        values.put(DataBaseHelper.COLUMN_PURPOSE_ICON, purposeIcon);

        long insertId = database.insert(DataBaseHelper.TABLE_PURPOSES, null, values);
        Cursor cursor = database.query(DataBaseHelper.TABLE_PURPOSES, allColumns, DataBaseHelper.COLUMN_PURPOSE_ID + "=" + (int)insertId, null, null, null, null);
        cursor.moveToFirst();
        Purpose newPurpose = cursorToPurpose(cursor);
        cursor.close();
        Log.d(LOG_TAG, "DataSourceHelper: Purpose '" + newPurpose.getPurpose_Name() + "' created");
        return newPurpose;
    }

    /**
     * cursor Method
     * @param cursor
     * @return returns a purpose with parameters
     */

    private Purpose cursorToPurpose(Cursor cursor) {
        Purpose purpose = new Purpose();
        purpose.setPurpose_ID(cursor.getInt(0));
        purpose.setPurpose_Name(cursor.getString(1));
        purpose.setPurpose_Icon(cursor.getString(2));
        return purpose;

    }

    /**
     * gets all Data from Purpose Table
     * @return returns a List
     */

    public List<Purpose> getAllPurposes() {
        List<Purpose> purposes = new ArrayList<Purpose>();
        Cursor cursor = database.query(DataBaseHelper.TABLE_PURPOSES, allColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Purpose purpose = cursorToPurpose(cursor);
            purposes.add(purpose);
            cursor.moveToNext();
        }
        cursor.close();
        return purposes;
    }

    /**
     * Method for getting a purpose Icon ID from Database
     * @param purpose_ID Id of the purpose
     * @return  String with the purpose Icon ID
     */

    public String getPurposeIcon (int purpose_ID){
        String purposeIcon = null;
        String query = "SELECT * FROM " + DataBaseHelper.TABLE_PURPOSES + " WHERE " + DataBaseHelper.COLUMN_PURPOSE_ID + " =  \"" + purpose_ID + "\"";

        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(query,null);
        if(cursor!=null){
            cursor.moveToFirst();
            purposeIcon = cursor.getString(2);
        }
        cursor.close();
        db.close();
        return purposeIcon;
    }

    /**
     * Method for checking if Table is full
     * @param tableName name of hte Table
     * @param openDb true if db is open
     * @return
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

        if (cursor.getCount() > 0 ) {
            cursor.close();
            Log.d(LOG_TAG, "Table with purposes already exists.");
            return true;

        } else {
            cursor.close();
            Log.d(LOG_TAG, "Table with purposes is empty. Filling table.");
            return false;
        }

    }
}
