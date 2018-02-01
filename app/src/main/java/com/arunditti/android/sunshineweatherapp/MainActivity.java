package com.arunditti.android.sunshineweatherapp;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.arunditti.android.sunshineweatherapp.ForecastAdapter.ForecastAdapterOnClickHandler;
import com.arunditti.android.sunshineweatherapp.data.SunshinePreferences;
import com.arunditti.android.sunshineweatherapp.utilities.NetworkUtils;
import com.arunditti.android.sunshineweatherapp.utilities.OpenWeatherJsonUtils;

import java.net.URL;
import java.util.Scanner;
import java.util.SimpleTimeZone;


// Implement ForecastAdapterOnClickHandler from the MainActivity
public class MainActivity extends AppCompatActivity implements ForecastAdapterOnClickHandler {

    //Create a field to store weather display TextView
    private RecyclerView mRecyclerView;

    // Add a TextView variable for the error message display
    private TextView mErrorMessage;

    //Add a ProgressBar variable to show and hide the progress bar
    private ProgressBar mLoadingIndicator;

    private ForecastAdapter mForecastAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
          Using findViewById, we get a reference to our TextView from xml. This allows us to
         * do things like set the text of the TextView.
         */
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_forecast);

        // Find the TextView for the error message using findViewById
        mErrorMessage = (TextView) findViewById(R.id.error);

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
        mForecastAdapter = new ForecastAdapter(this);

        //Use mRecyclerView.setAdapter and pass in mForecastAdapter
        mRecyclerView.setAdapter(mForecastAdapter);
        /* Once all of our views are setup, we can load the weather data. */
        loadWeatherData();
    }

    //This is a method that will get the user's preferred location and execute your new AsyncTask
    private void loadWeatherData() {
        // Call showWeatherDataView before executing the AsyncTask
        showWeatherDataView();
        String location = SunshinePreferences.getPreferredWeatherLocation(this);
        new FetchWeatherTask().execute(location);
    }

    // Override ForecastAdapterOnClickHandler's onClick method
    @Override
    public void onClick(String weatherForDay) {
        Context context = this;
        // Show a Toast when an item is clicked, displaying that item's weather data
        Toast.makeText(context, weatherForDay, Toast.LENGTH_SHORT).show();
    }

    //Create a method called showWeatherDataView that will hide the error message and show the weather data
    private void showWeatherDataView() {
        mErrorMessage.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    // Create a method called showErrorMessage that will hide the weather data and show the error message
    private void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessage.setVisibility(View.VISIBLE);
    }

    public class FetchWeatherTask extends AsyncTask<String, Void, String[]> {

        // Within your AsyncTask, override the method onPreExecute and show the loading indicator
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected String[] doInBackground(String... params) {
            if(params.length == 0) {
                return null;
            }
            String location = params[0];
            URL weatherRequestUrl = NetworkUtils.buildUrl(location);

            try {
                String jsonWeatherResponse = NetworkUtils.getResponseFromHttpUrl(weatherRequestUrl);
                String[] simpleJsonWeatherData = OpenWeatherJsonUtils.getSimpleWeatherStringsFromJson(MainActivity.this, jsonWeatherResponse);
                return simpleJsonWeatherData;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String[] weatherData) {
            // As soon as the data is finished loading, hide the loading indicator
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if(weatherData != null) {
                // If the weather data was not null, make sure the data view is visible
                showWeatherDataView();
                mForecastAdapter.setWeatherData(weatherData);
            } else {
                showErrorMessage();
            }
        }
    }

    // Create a menu resource in res/menu/ called forecast.xml
    // Add one item to the menu with an ID of action_refresh
    // Set the title of the menu item to "Refresh" using strings.xml

    // Override onCreateOptionsMenu to inflate the menu for this Activity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        inflater.inflate(R.menu.forecast, menu);
        /* Return true so that the menu is displayed in the Toolbar */
        return true;
    }

    // Override onOptionsItemSelected to handle clicks on the refresh button
    @Override
    public  boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_refresh) {
           // Instead of setting the text to "", set the adapter to null before refreshing
           mForecastAdapter.setWeatherData(null);
            loadWeatherData();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
