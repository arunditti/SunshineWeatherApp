package com.arunditti.android.sunshineweatherapp.sync;


import android.content.Context;
import android.os.AsyncTask;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.firebase.jobdispatcher.RetryStrategy;


/**
 * Created by arunditti on 3/13/18.
 */

public class SunshineFirebaseJobService extends JobService {

    //Declare an AsyncTAsk field called mFetchWeatherTask
    private AsyncTask<Void, Void, Void> mFetchWeatherTask;

    //Override onStartJob and within it, spawn off a separate ASyncTask to sync weather data
    @Override
    public boolean onStartJob(final JobParameters params) {
        mFetchWeatherTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                Context context = getApplicationContext();
                SunshineSyncTask.syncWeather(context);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                //Once weather data is synced, call jobFinished with the appropriate arguments
                jobFinished(params, false);
            }
        };
        mFetchWeatherTask.execute();
        return false;
    }

    //Override onStopJob, cancel the AsyncTAsk if its not null and return true
    @Override
    public boolean onStopJob(JobParameters params) {
        if(mFetchWeatherTask != null) {
            mFetchWeatherTask.cancel(true);
        }
        return true;
    }
}
