package com.arunditti.android.sunshineweatherapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    //Create a field to store weather display TextView
    private TextView mWeatherTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
          Using findViewById, we get a reference to our TextView from xml. This allows us to
         * do things like set the text of the TextView.
         */
        mWeatherTextView = (TextView) findViewById(R.id.weather_data);

        //This String array contains dummy weather data.
        String[] dummyWeatherData = {
                "Today, Jan22 - Clear - 17°C / 15°C",
                "Tomorrow - Cloudy - 19°C / 15°C",
                "Thursday - Rainy - 16°C / 8°C",
                "Friday - Partly Cloudy - 15°C / 10°C",
                "Saturday - Thunderstorms - 16°C / 7°C",
                "Sunday - Rainy- 30°C / 11°C",
                "Monday - Thunderstorms - 21°C / 9°C"};

         //Iterate through the array and append the Strings to the TextView.

        for(String dummyWeatherDay: dummyWeatherData) {
             mWeatherTextView.append(dummyWeatherDay + "\n\n\n");
        }
    }
}
