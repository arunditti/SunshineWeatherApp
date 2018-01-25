package com.arunditti.android.sunshineweatherapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.arunditti.android.sunshineweatherapp.data.SunshinePreferences;
import com.arunditti.android.sunshineweatherapp.utilities.NetworkUtils;
import com.arunditti.android.sunshineweatherapp.utilities.OpenWeatherJsonUtils;

import java.net.URL;
import java.util.Scanner;
import java.util.SimpleTimeZone;

public class MainActivity extends AppCompatActivity {

    //Create a field to store weather display TextView
    private TextView mWeatherTextView;

    // Add a TextView variable for the error message display
    private TextView mErrorMessage;

    //Add a ProgressBar variable to show and hide the progress bar
    private ProgressBar mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
          Using findViewById, we get a reference to our TextView from xml. This allows us to
         * do things like set the text of the TextView.
         */
        mWeatherTextView = (TextView) findViewById(R.id.weather_data);

        // Find the TextView for the error message using findViewById
        mErrorMessage = (TextView) findViewById(R.id.error);

        // Find the ProgressBar using findViewById
        mLoadingIndicator = (ProgressBar) findViewById(R.id.progress);

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

    //Create a method called showWeatherDataView that will hide the error message and show the weather data
    private void showWeatherDataView() {
        mErrorMessage.setVisibility(View.INVISIBLE);
        mWeatherTextView.setVisibility(View.VISIBLE);
    }

    // Create a method called showErrorMessage that will hide the weather data and show the error message
    private void showErrorMessage() {
        mWeatherTextView.setVisibility(View.INVISIBLE);
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
                String[] simpleJsonWeatherData = OpenWeatherJsonUtils.getSimpleWeatherStringFromJson(MainActivity.this, jsonWeatherResponse);
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
                /*
                 * Iterate through the array and append the Strings to the TextView. The reason why we add
                 * the "\n\n\n" after the String is to give visual separation between each String in the
                 * TextView. Later, we'll learn about a better way to display lists of data.
                 */
                for(String weatherString : weatherData) {
                    mWeatherTextView.append((weatherString) + "\n\n\n") ;
                }
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
            mWeatherTextView.setText("");
            loadWeatherData();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
