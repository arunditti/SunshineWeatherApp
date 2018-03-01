package com.arunditti.android.sunshineweatherapp.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by arunditti on 2/28/18.
 */

public class WeatherContract {

    public static final String CONTENT_AUTHORITY = "com.arunditti.android.sunshineweatherapp";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_WEATHER = "weather";

    public static final class WeatherEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_WEATHER)
                .build();

        //reate a public static final String call TABLE_NAME with the value "weather"
        public static final String TABLE_NAME = "weather";

        //Create a public static final String call COLUMN_DATE with the value "date"
        public static final String COLUMN_DATE = "date";

        //  Create a public static final String call COLUMN_WEATHER_ID with the value "weather_id"
        /* Weather ID as returned by API, used to identify the icon to be used */
        public static final String COLUMN_WEATHER_ID = "weather_id";

        //  Create a public static final String call COLUMN_MIN_TEMP with the value "min"
        /* Min and max temperatures in °C for the day (stored as floats in the database) */
        public static final String COLUMN_MIN_TEMP = "min";
        // Create a public static final String call COLUMN_MAX_TEMP with the value "max"
        public static final String COLUMN_MAX_TEMP = "max";

        // Create a public static final String call COLUMN_HUMIDITY with the value "humidity"
        /* Humidity is stored as a float representing percentage */
        public static final String COLUMN_HUMIDITY = "humidity";

        // Create a public static final String call COLUMN_PRESSURE with the value "pressure"
        /* Pressure is stored as a float representing percentage */
        public static final String COLUMN_PRESSURE = "pressure";

        //Create a public static final String call COLUMN_WIND_SPEED with the value "wind"
        /* Wind speed is stored as a float representing wind speed in mph */
        public static final String COLUMN_WIND_SPEED = "wind";

        //Create a public static final String call COLUMN_DEGREES with the value "degrees"
        /*
         * Degrees are meteorological degrees (e.g, 0 is north, 180 is south).
         * Stored as floats in the database.
         *
         * Note: These degrees are not to be confused with temperature degrees of the weather.
         */
        public static final String COLUMN_DEGREES = "degrees";
    }
}
