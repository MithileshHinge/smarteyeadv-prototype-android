package com.example.app1;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class ActivityLogDatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "ActivityLog";

    // Log table name
    private static final String TABLE_ACTIVITY_LOG = "ActivityLogTable";

    // Log Table Columns names
    private static final String
            KEY_ID = "id",
            KEY_NAME = "name",
            KEY_DATE = "date",
            KEY_BKMRK = "bkmrk",
            KEY_THUMBPATH = "thumbpath";



    public ActivityLogDatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_ACTIVITY_LOG_TABLE = "CREATE TABLE IF NOT EXISTS "
                + TABLE_ACTIVITY_LOG + "("
                + KEY_ID + " INTEGER PRIMARY KEY ,"
                + KEY_NAME + " TEXT ,"
                + KEY_DATE + " TEXT ,"
                + KEY_BKMRK + " INTEGER ,"
                + KEY_THUMBPATH + " TEXT "
                + ")";

        db.execSQL(CREATE_ACTIVITY_LOG_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACTIVITY_LOG);

        // Create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    // Adding new row
    void addRow(ActivityLogDatabaseRow rowEntry) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, rowEntry.getName());
        values.put(KEY_DATE, rowEntry.getDateTime());
        values.put(KEY_BKMRK, rowEntry.isBookmarked());
        values.put(KEY_THUMBPATH, rowEntry.getThumbpath());
        System.out.println("DATABASE IMAGE NAME" + rowEntry.getThumbpath());

        // Inserting Row
        db.insert(TABLE_ACTIVITY_LOG, null, values);
        db.close(); // Closing database connection
    }

    // Getting single row
    ActivityLogDatabaseRow getRow(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_ACTIVITY_LOG, new String[]{KEY_ID, KEY_NAME, KEY_DATE, KEY_BKMRK, KEY_THUMBPATH}, KEY_ID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        ActivityLogDatabaseRow rowEntry = new ActivityLogDatabaseRow(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getInt(3), cursor.getString(4));
        cursor.close();

        // return row
        return rowEntry;
    }

    // Getting All Rows
    public List<ActivityLogDatabaseRow> getAllRows() {
        List<ActivityLogDatabaseRow> rows = new ArrayList<ActivityLogDatabaseRow>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_ACTIVITY_LOG;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ActivityLogDatabaseRow rowEntry = new ActivityLogDatabaseRow();
                rowEntry.setID(Integer.parseInt(cursor.getString(0)));
                rowEntry.setName(cursor.getString(1));
                rowEntry.setDateTime(cursor.getString(2));
                rowEntry.setBookmarked(cursor.getInt(3));
                rowEntry.setThumbpath(cursor.getString(4));

                // Adding rowEntry to list
                rows.add(rowEntry);
            } while (cursor.moveToNext());
        }

        // return row list
        return rows;
    }

    // Updating single row
    public int updateRow(ActivityLogDatabaseRow rowEntry) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, rowEntry.getName());
        values.put(KEY_DATE, rowEntry.getDateTime());
        values.put(KEY_BKMRK, rowEntry.isBookmarked());
        values.put(KEY_THUMBPATH, rowEntry.getThumbpath());

        // updating row
        return db.update(TABLE_ACTIVITY_LOG, values, KEY_ID + " = ?", new String[]{String.valueOf(rowEntry.getID())});
    }

    // Deleting single row
    public void deleteRow(ActivityLogDatabaseRow rowEntry) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ACTIVITY_LOG, KEY_ID + " = ?", new String[]{String.valueOf(rowEntry.getID())});
        db.close();
    }


    // Getting Row Count
    public int getRowCount() {
        String countQuery = "SELECT  * FROM " + TABLE_ACTIVITY_LOG;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }
}
