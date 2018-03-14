package com.arunditti.android.sunshineweatherapp.sync;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.arunditti.android.sunshineweatherapp.data.WeatherContract;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.util.concurrent.TimeUnit;

/**
 * Created by arunditti on 3/12/18.
 */

public class SunshineSyncUtils {

    //Add constant values to sync Sunshine every 3-4 hours
    private static final int SYNC_INTERVAL_HOURS = 3;
    private static final int SYNC_INTERVAL_SECONDS = (int) TimeUnit.HOURS.toSeconds(SYNC_INTERVAL_HOURS);
    private static final int SYNC_FLEXTIME_SECONDS = SYNC_INTERVAL_SECONDS / 3;

    private static boolean sInitialized;

    //Add a sync tag to identify our sync job
    private static final String SUNSHINE_SYNC_TAG = "sunshine-sync";
    //Create a methos to schedule our periodic weather sync
    static void scheduleFirebaseJobDispatcherSync(final Context context) {

        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        /* Create the Job to periodically sync Sunshine */
        Job syncSunshineJob = dispatcher.newJobBuilder()
                /* The Service that will be used to sync Sunshine's data */
                .setService(SunshineFirebaseJobService.class)
                /* Set the UNIQUE tag used to identify this Job */
                .setTag(SUNSHINE_SYNC_TAG)
                /*
                 * Network constraints on which this Job should run. We choose to run on any
                 * network, but you can also choose to run only on un-metered networks or when the
                 * device is charging. It might be a good idea to include a preference for this,
                 * as some users may not want to download any data on their mobile plan. ($$$)
                 */
                .setConstraints(Constraint.ON_ANY_NETWORK)
                /*
                 * setLifetime sets how long this job should persist. The options are to keep the
                 * Job "forever" or to have it die the next time the device boots up.
                 */
                .setLifetime(Lifetime.FOREVER)
                /*
                 * We want Sunshine's weather data to stay up to date, so we tell this Job to recur.
                 */
                .setRecurring(true)
                /*
                 * We want the weather data to be synced every 3 to 4 hours. The first argument for
                 * Trigger's static executionWindow method is the start of the time frame when the
                 * sync should be performed. The second argument is the latest point in time at
                 * which the data should be synced. Please note that this end time is not
                 * guaranteed, but is more of a guideline for FirebaseJobDispatcher to go off of.
                 */
                .setTrigger(Trigger.executionWindow(
                        SYNC_INTERVAL_SECONDS,
                        SYNC_INTERVAL_SECONDS + SYNC_FLEXTIME_SECONDS))
                /*
                 * If a Job with the tag with provided already exists, this new job will replace
                 * the old one.
                 */
                .setReplaceCurrent(true)
                /* Once the Job is ready, call the builder's build method to return the Job */
                .build();

   /* Schedule the Job with the dispatcher */
        dispatcher.schedule(syncSunshineJob);

    }

    //Create a synchronized public static void method called initialize
    synchronized public static void initialize(final Context context) {

        //Only execute this body if sInitialize is false
        if (sInitialized = true) {
            return;
        }

        //If the method body is executed. set initialize to true
        sInitialized = true;

        //Call the method you created to schedule a periodic weather sync

        //This method call triggers Sunshine to create its task to synchronize weather data periodically.
        scheduleFirebaseJobDispatcherSync(context);

         /*
         * We need to check to see if our ContentProvider has data to display in our forecast
         * list. However, performing a query on the main thread is a bad idea as this may
         * cause our UI to lag. Therefore, we create a thread in which we will run the query
         * to check the contents of our ContentProvider.
         */
        Thread checkForEmpty = new Thread(new Runnable() {
            @Override
            public void run() {
                //Uri for every row of weather data in our weather table
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

                //make sure to close the cursor to avoid memory leaks
                cursor.close();
            }
        });
        checkForEmpty.start();
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

