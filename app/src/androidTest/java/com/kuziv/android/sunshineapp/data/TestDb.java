package com.kuziv.android.sunshineapp.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;

public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    /**
     * This function gets called before each test is executed to delete the database.
     * This makes sure that we always have a clean test.
    */
    public void setUp() {
        deleteTheDatabase();
    }

    private void deleteTheDatabase() {
        mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME);
    }

    // This is only test that the Location table has the correct columns
    public void testCreateDb() throws Throwable {
        // HashSet of all of the table names we wish to look for
        final HashSet<String> tableNameHashSet = new HashSet<>();
        tableNameHashSet.add(WeatherContract.LocationEntry.TABLE_NAME);
        tableNameHashSet.add(WeatherContract.WeatherEntry.TABLE_NAME);

        mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new WeatherDbHelper(this.mContext).getWritableDatabase();
        assertTrue("Error: There is no access to database ", db.isOpen());

        // verification have we created the tables we want?
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                cursor.moveToFirst());

        // verification that the tables have been created
        do {
            tableNameHashSet.remove(cursor.getString(0));
        } while (cursor.moveToNext());

        // if this fails, it means that database doesn't contain both the location entry
        // and weather entry tables
        assertTrue("Error: Your database was created without both the location entry and weather entry tables",
                tableNameHashSet.isEmpty());

        // verification do our tables contain the correct columns?
        cursor = db.rawQuery("PRAGMA table_info(" + WeatherContract.LocationEntry.TABLE_NAME + ")", null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                cursor.moveToFirst());

        // Building a HashSet of all of the column names we want to look for
        final HashSet<String> locationColumnHashSet = new HashSet<>();
        locationColumnHashSet.add(WeatherContract.LocationEntry._ID);
        locationColumnHashSet.add(WeatherContract.LocationEntry.COLUMN_CITY_NAME);
        locationColumnHashSet.add(WeatherContract.LocationEntry.COLUMN_COORD_LAT);
        locationColumnHashSet.add(WeatherContract.LocationEntry.COLUMN_COORD_LONG);
        locationColumnHashSet.add(WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING);

        int columnNameIndex = cursor.getColumnIndex("name");
        do {
            String columnName = cursor.getString(columnNameIndex);
            locationColumnHashSet.remove(columnName);
        } while (cursor.moveToNext());

        // if this fails, it means that database doesn't contain all of the required location entry columns
        assertTrue("Error: The database doesn't contain all of the required location entry columns",
                locationColumnHashSet.isEmpty());
        cursor.close();
        db.close();
    }

    public void testLocationTable() {
        // Reference to database
        SQLiteDatabase db = new WeatherDbHelper(this.getContext()).getWritableDatabase();

        // Creating test values (ContentValues) that I want to insert for
        ContentValues locationTestValues = new ContentValues();

        long rowId = insertLocation(db, locationTestValues);
        assertTrue("Error: Inserting test values into database get failed.", rowId != -1);

        // Query the database to receive a Cursor back
        Cursor cursor = db.query(WeatherContract.LocationEntry.TABLE_NAME, null, null, null, null, null, null);
        assertTrue("Error: There is no any rows in LOCATION table", cursor.moveToFirst());

        // Validate data in resulting Cursor with the original ContentValues
        TestUtilities.validateCurrentRecord("ERROR: validating current record", cursor, locationTestValues);

        // Finally, close the cursor and database
        cursor.close();
        db.close();
    }

    public void testWeatherTable() {
        // Reference to database
        SQLiteDatabase db = new WeatherDbHelper(this.getContext()).getWritableDatabase();

        // Creating test values (ContentValues) that I want to insert for
        ContentValues locationTestValues = new ContentValues();

        // Insert ContentValues into database and get a row ID back
        long locationRowId = insertLocation(db, locationTestValues);
        assertTrue("Error: Inserting test values into database get failed.", locationRowId != -1);

        // Use the locationRowId to insert the weather
        ContentValues weatherTestValues = TestUtilities.createWeatherValues(locationRowId);

        // Insert ContentValues into database and get a row ID back
        db.insert(WeatherContract.WeatherEntry.TABLE_NAME, null, weatherTestValues);

        // Query the database and receive a Cursor back
        Cursor cursor = db.query(WeatherContract.WeatherEntry.TABLE_NAME, null, null, null, null, null, null);
        assertTrue("Error: There is no any rows in WEATHER table", cursor.moveToFirst());

        // Validate data in resulting Cursor with the original ContentValues
        TestUtilities.validateCurrentRecord("ERROR: validating current record", cursor, weatherTestValues);

        // Finally, close the cursor and database
        cursor.close();
        db.close();
    }

    private long insertLocation(SQLiteDatabase database, ContentValues locationTestValues) {

        // filling test values (ContentValues) that I want to insert for
        locationTestValues.put(WeatherContract.LocationEntry.COLUMN_CITY_NAME, "Kiev");
        locationTestValues.put(WeatherContract.LocationEntry.COLUMN_COORD_LAT, 100.00);
        locationTestValues.put(WeatherContract.LocationEntry.COLUMN_COORD_LONG, -200.00);
        locationTestValues.put(WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING, "34567");

        // Insert ContentValues into database and get a row ID back
        return database.insert(WeatherContract.LocationEntry.TABLE_NAME, null, locationTestValues);
    }
}
