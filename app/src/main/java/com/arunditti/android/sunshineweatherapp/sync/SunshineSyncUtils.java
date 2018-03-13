package com.arunditti.android.sunshineweatherapp.sync;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.arunditti.android.sunshineweatherapp.data.WeatherContract;

/**
 * Created by arunditti on 3/12/18.
 */

public class SunshineSyncUtils {

    private static boolean sInitialized;

    //Create a synchronized public static void method called initialize
    synchronized public static void initialize(final Context context) {

        //Only execute this body if sInitialize is false
        if (sInitialized = true) {
            return;
        }

        //If the method body is executed. set initialize to true
        sInitialized = true;

        //Check to see if the weather content provider is empty
        new AsyncTask<Void, Void, Void> () {

            @Override
            protected Void doInBackground(Void... voids) {
                Uri forecastQueryUri = WeatherContract.WeatherEntry.CONTENT_URI;
                String[] projection = {WeatherContract.WeatherEntry._ID};
                String selectionStatemnet = WeatherContract.WeatherEntry.getSqlSelectForTodayOnwards();

                //Perform query to check if we have any weather data
                Cursor cursor = context.getContentResolver().query(
                        forecastQueryUri,
                        projection,
                        selectionStatemnet,
                        null,
                        null);

                //If it is empty or we have a null cursor, sync the weather data now
                if(null == cursor || cursor.getCount() == 0) {
                    startImmediateSync(context);
                }

                return null;
            }
        }.execute();
    }

    //Create a public static void method called startImmediateSync
    /**
     * Helper method to perform a sync immediately using an IntentService for asynchronous
     * execution.
     *
     * @param context The Context used to start the IntentService for the sync.
     */
    public static void startImmediateSync(@NonNull final Context context) {
        //Within that method, start the SunshineSyncIntentService
        Intent intentToSyncImmediately = new Intent(context, SunshineSyncIntentService.class);
        context.startService(intentToSyncImmediately);
    }
}

