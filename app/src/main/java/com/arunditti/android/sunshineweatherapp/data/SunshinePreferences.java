package com.arunditti.android.sunshineweatherapp.data;

import android.content.Context;

import java.util.Scanner;

/**
 * Created by arunditti on 1/24/18.
 */

public class SunshinePreferences {
    public static final String PREF_CITY_NAME = "city_name";

    public static final String PREF_COORD_LAT = "coord_lat";
    public static final String PREF_COORD_LONG = "coord_long";

    private static final String DEFAULT_WEATHER_LOCATION = "94043,USA";
    private static final double[] DEFAULT_WEATHER_COORDINATES = {37.4284, 122.0724};

    private static final String DEFAULT_MAP_LOCATION = "1600 Amphitheatre Parkway, Mountain View, CA 94-43";

    public static String getPreferredWeatherLocation(Context context) {
        return getDefaultWeatherLocation();
    }

    /**
     * Returns true if the user has selected metric temperature display.
     *
     * @param context Context used to get the SharedPreferences
     * @return true If metric display should be used
     */
    public static boolean isMetric(Context context) {
        /** This will be implemented in a future lesson **/
        return true;
    }


    private static String getDefaultWeatherLocation() {
        return DEFAULT_WEATHER_LOCATION;
    }

    public static double[] getDefaultWeatherCoordinates() {
        /** This will be implemented in a future lesson **/
        return DEFAULT_WEATHER_COORDINATES;
    }
}
