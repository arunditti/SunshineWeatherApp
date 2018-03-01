package com.arunditti.android.sunshineweatherapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.arunditti.android.sunshineweatherapp.data.WeatherContract.WeatherEntry;

/**
 * Created by arunditti on 2/28/18.
 */

public class WeatherDbHelper extends SQLiteOpenHelper {
    // Create a public static final String called DATABASE_NAME with value "weather.db"
    /*
     * This is the name of our database. Database names should be descriptive and end with the
     * .db extension.
     */
    public static final String DATABASE_NAME = "weather.db";

    //  Create a private static final int called DATABASE_VERSION and set it to 1
    private static final int DATABASE_VERSION = 2;

    //Create a constructor that accepts a context and call through to the superclass constructor
    public WeatherDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

//  Override onCreate and create the weather table from within it
    /**
     * Called when the database is created for the first time. This is where the creation of
     * tables and the initial population of the tables should happen.
     *
     * @param sqLiteDatabase The database.
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        /*
         * This String will contain a simple SQL statement that will create a table that will
         * cache our weather data.
         */
        final String SQL_CREATE_WEATHER_TABLE =

                "CREATE TABLE " + WeatherEntry.TABLE_NAME + " (" +

                /*
                 * WeatherEntry did not explicitly declare a column called "_ID". However,
                 * WeatherEntry implements the interface, "BaseColumns", which does have a field
                 * named "_ID". We use that here to designate our table's primary key.
                 */
                        WeatherEntry._ID               + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        WeatherEntry.COLUMN_DATE       + " INTEGER NOT NULL, "                 +

                        WeatherEntry.COLUMN_WEATHER_ID + " INTEGER NOT NULL, "                 +

                        WeatherEntry.COLUMN_MIN_TEMP   + " REAL NOT NULL, "                    +
                        WeatherEntry.COLUMN_MAX_TEMP   + " REAL NOT NULL, "                    +

                        WeatherEntry.COLUMN_HUMIDITY   + " REAL NOT NULL, "                    +
                        WeatherEntry.COLUMN_PRESSURE   + " REAL NOT NULL, "                    +

                        WeatherEntry.COLUMN_WIND_SPEED + " REAL NOT NULL, "                    +
                        WeatherEntry.COLUMN_DEGREES    + " REAL NOT NULL, " +

//              COMPLETED (1) Add a UNIQUE constraint on the date column to replace on conflict
                        /*
                 * To ensure this table can only contain one weather entry per date, we declare
                 * the date column to be unique. We also specify "ON CONFLICT REPLACE". This tells
                 * SQLite that if we have a weather entry for a certain date and we attempt to
                 * insert another weather entry with that date, we replace the old weather entry.
                 */

                        " UNIQUE (" + WeatherEntry.COLUMN_DATE + ") ON CONFLICT REPLACE);";
        /*
         * After we've spelled out our SQLite table creation statement above, we actually execute
         * that SQL with the execSQL method of our SQLite database object.
         */
        sqLiteDatabase.execSQL(SQL_CREATE_WEATHER_TABLE);
    }

// Override onUpgrade, but don't do anything within it yet
    /**
     *
     * @param sqLiteDatabase Database that is being upgraded
     * @param oldVersion     The old database version
     * @param newVersion     The new database version
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        //  Within onUpgrade, drop the weather table if it exists
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + WeatherEntry.TABLE_NAME);
        // call onCreate and pass in the SQLiteDatabase (passed in to onUpgrade)
        onCreate(sqLiteDatabase);
    }
}
