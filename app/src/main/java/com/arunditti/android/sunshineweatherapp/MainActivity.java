package com.arunditti.android.sunshineweatherapp;

import android.content.ContentProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telecom.Call;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.arunditti.android.sunshineweatherapp.data.SunshinePreferences;
import com.arunditti.android.sunshineweatherapp.data.WeatherContract;
import com.arunditti.android.sunshineweatherapp.sync.SunshineSyncUtils;
import com.arunditti.android.sunshineweatherapp.utilities.NetworkUtils;
import com.arunditti.android.sunshineweatherapp.utilities.OpenWeatherJsonUtils;

import java.net.URL;


// Implement the loaderCallbacks from the MainActivity
public class MainActivity extends AppCompatActivity implements
        ForecastAdapter.ForecastAdapterOnClickHandler,
        LoaderCallbacks<Cursor>{

    //Create a String array containing the names of the desired data columns from our ContentProvider
    public static final String[] MAIN_FORECAST_PROJECTION = {
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
    };

    //Create constant int values representing each column name's position above
    public static final int INDEX_WEATHER_DATE = 0;
    public static final int INDEX_WEATHER_MAX_TEMP = 1;
    public static final int INDEX_WEATHER_MIN_TEMP = 2;
    public static final int INDEX_WEATHER_CONDITION_ID = 3;



    private static final String TAG = MainActivity.class.getSimpleName();

    //Create a field to store weather display TextView
    private RecyclerView mRecyclerView;

    // Add a TextView variable for the error message display
    private TextView mErrorMessage;

    //Add a ProgressBar variable to show and hide the progress bar
    private ProgressBar mLoadingIndicator;

    private ForecastAdapter mForecastAdapter;

    private int mPosition = RecyclerView.NO_POSITION;

    private static final int FORECAST_LOADER_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setElevation(0f);

       // FakeDataUtils.insertFakeData(this);

        /*
          Using findViewById, we get a reference to our TextView from xml. This allows us to
         * do things like set the text of the TextView.
         */
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_forecast);

        // Find the ProgressBar using findViewById
        mLoadingIndicator = (ProgressBar) findViewById(R.id.progress);

        //Create layoutManager, a LinearLayoutManager with VERTICAL orientation and shouldReverseLayout == false
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        //Set the LayoutManager on mRecyclerView
        mRecyclerView.setLayoutManager(layoutManager);

        // Use setHasFixedSize(true) on mRecyclerView to designate that all items in the list will have the same size
        mRecyclerView.setHasFixedSize(true);

        /*
         * The ForecastAdapter is responsible for linking our weather data with the Views that
         * will end up displaying our weather data.
         */
        mForecastAdapter = new ForecastAdapter(this, this);

        /* Setting the adapter attaches it to the RecyclerView in our layout. */
        mRecyclerView.setAdapter(mForecastAdapter);

        // Remove the code for the AsyncTask and initialize the AsyncTaskLoader
        int loaderId = FORECAST_LOADER_ID;

        showLoading();
        /*
         * Ensures a loader is initialized and active. If the loader doesn't already exist, one is
         * created and (if the activity/fragment is currently started) starts the loader. Otherwise
         * the last created loader is re-used.
         */
        getSupportLoaderManager().initLoader(FORECAST_LOADER_ID, null, this);

        //Call SunshineSyncUtils's startImmediateSync method
        SunshineSyncUtils.startImmediateSync(this);
    }
    /**
     * Instantiate and return a new Loader for the given ID.
     *
     * @param id The ID whose loader is to be created.
     * @param loaderArgs Any arguments supplied by the caller.
     *
     * @return Return a new Loader instance that is ready to start loading.
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, final Bundle loaderArgs) {

        switch (id) {
            case FORECAST_LOADER_ID:
            Uri forecastQueryUri = WeatherContract.WeatherEntry.CONTENT_URI;
            String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
            String selection = WeatherContract.WeatherEntry.getSqlSelectForTodayOnwards();

            return new CursorLoader(this,
                    forecastQueryUri,
                    MAIN_FORECAST_PROJECTION,
                    selection,
                    null,
                    sortOrder);
            default:
                throw new IllegalArgumentException("Loader is not implemented: " + id);
        }
    }

    // When the load is finished, show either the data or an error message if there is no data
    /**
     * Called when a previously created loader has finished its load.
     *
     * @param loader The Loader that has finished.
     * @param data The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //Call mForecastAdapter's swapCursor method and pass in the new Cursor
       mForecastAdapter.swapCursor(data);
        // If mPosition equals RecyclerView.NO_POSITION, set it to 0
        if(mPosition == RecyclerView.NO_POSITION) {
            mPosition = 0;
        }
        // Smooth scroll the RecyclerView to mPosition
        mRecyclerView.smoothScrollToPosition(mPosition);
        //      COMPLETED (31) If the Cursor's size is not equal to 0, call showWeatherDataView
        if(data.getCount() != 0) {
            showWeatherDataView();
        }

    }

    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.  The application should at this point
     * remove any references it has to the Loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //Call mForecastAdapter's swapCursor method and pass in null
        mForecastAdapter.swapCursor(null);
    }

    /**
     * This method uses the URI scheme for showing a location found on a map in conjunction with
     * an implicit Intent. This super-handy intent is detailed in the "Common Intents" page of
     * Android's developer site:
     *
     * @see "http://developer.android.com/guide/components/intents-common.html#Maps"
     * <p>
     * Protip: Hold Command on Mac or Control on Windows and click that link to automagically
     * open the Common Intents page
     */
    private void openLocationInMap() {
        String addressString = SunshinePreferences.getPreferredWeatherLocation(this);
        Uri geoLocation = Uri.parse("geo:0,0?q=" + addressString);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.d(TAG, "Couldn't call " + geoLocation.toString() + ", no receiving apps installed!");
        }
    }

    //This method is for responding to clicks from our list.
    @Override
    public void onClick(long date) {
        Intent weatherDetailIntent = new Intent(MainActivity.this, DetailActivity.class);
//      COMPLETED (39) Refactor onClick to pass the URI for the clicked date with the Intent
        Uri uriForDateClicked = WeatherContract.WeatherEntry.buildWeatherUriWithDate(date);
        weatherDetailIntent.setData(uriForDateClicked);
        startActivity(weatherDetailIntent);
    }

    /**
     * This method will make the View for the weather data visible and
     * hide the error message.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private void showWeatherDataView() {
         /* First, hide the loading indicator */
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        /* Then, make sure the weather data is visible */
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    // Create a method called showLoading that shows the loading indicator and hides the data
    private void showLoading() {
        /* Then, hide the weather data */
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Finally, show the loading indicator */
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        inflater.inflate(R.menu.forecast, menu);
        /* Return true so that the menu is displayed in the Toolbar */
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_map) {
            openLocationInMap();
            return true;
        }

        if (id == R.id.action_settings) {
            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(startSettingsActivity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
