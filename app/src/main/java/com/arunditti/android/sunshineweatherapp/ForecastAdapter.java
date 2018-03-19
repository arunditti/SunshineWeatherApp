package com.arunditti.android.sunshineweatherapp;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.arunditti.android.sunshineweatherapp.utilities.SunshineDateUtils;
import com.arunditti.android.sunshineweatherapp.utilities.SunshineWeatherUtils;
import com.arunditti.android.sunshineweatherapp.data.WeatherContract.WeatherEntry;

/**
 * Created by arunditti on 1/26/18.
 */

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastAdapterViewHolder> {

    private static final String TAG = ForecastAdapter.class.getSimpleName();
    private final Context mContext;
    private Cursor mCursor;

    // Created a final private ForecastAdapterOnClickHandler called mClickHandler
    /*
     * An on-click handler that we've defined to make it easy for an Activity to interface with
     * our RecyclerView
     */
    private final ForecastAdapterOnClickHandler mClickHandler;

    /**
     * The interface that receives onClick messages.
     */

    public interface ForecastAdapterOnClickHandler {
        //Refactor onClick to accept a long as its parameter rather than a String
        void onClick(long date);
    }

    // Added a ForecastAdapterOnClickHandler as a parameter to the constructor and store it in mClickHandler
    /**
     * Creates a ForecastAdapter.
     *
     * @param mContext
     * @param clickHandler The on-click handler for this adapter. This single handler is called
     */

    public ForecastAdapter(Context mContext, ForecastAdapterOnClickHandler clickHandler) {
        this.mContext = mContext;
        mClickHandler = clickHandler;
    }

    @Override
    public ForecastAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.forecast_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new ForecastAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ForecastAdapterViewHolder forecastAdapterViewHolder, int position) {
;
        mCursor.moveToPosition(position);

         /* Use the weatherId to obtain the proper description */
        int weatherId = mCursor.getInt(MainActivity.INDEX_WEATHER_CONDITION_ID);

        /*******************
         * Weather icon *
         *******************/
       int weatherImageId;
       weatherImageId = SunshineWeatherUtils.getSmallArtResourceIdForWeatherCondition(weatherId);
       forecastAdapterViewHolder.weatherIconView.setImageResource(weatherImageId);

        /****************
         * Weather Date *
         ****************/
//      Generate a weather summary with the date, description, high and low
        /* Read date from the cursor */
        long dateInMillis = mCursor.getLong(MainActivity.INDEX_WEATHER_DATE);
        /* Get human readable string using our utility method */
        String dateString = SunshineDateUtils.getFriendlyDateString(mContext, dateInMillis, false);
        //Display friendly date string
        forecastAdapterViewHolder.dateView.setText(dateString);

        /***********************
         * Weather Description *
         ***********************/
        String description = SunshineWeatherUtils.getStringForWeatherCondition(mContext, weatherId);
        //Create the accessibility (ally) String from weather description
        String descriptionA11y = mContext.getString(R.string.a11y_forecast, description);
        //Set the text and content description
        forecastAdapterViewHolder.descrptionView.setText(description);
        forecastAdapterViewHolder.descrptionView.setContentDescription(descriptionA11y);

        /**************************
         * High (max) temperature *
         **************************/
        /* Read high temperature from the cursor (in degrees celsius) */
        double highInCelsius = mCursor.getDouble(MainActivity.INDEX_WEATHER_MAX_TEMP);
        String highString = SunshineWeatherUtils.formatTemperature(mContext, highInCelsius);
        //Create the accessibility (ally) String from weather description
        String highAlly = mContext.getString(R.string.a11y_high_temp, highString);

        //Set the text and content description
        forecastAdapterViewHolder.highTemperatureView.setText(highString);
        forecastAdapterViewHolder.highTemperatureView.setContentDescription(highAlly);

        /*************************
         * Low (min) temperature *
         *************************/
        /* Read low temperature from the cursor (in degrees celsius) */
        double lowInCelsius = mCursor.getDouble(MainActivity.INDEX_WEATHER_MIN_TEMP);

        String lowString = SunshineWeatherUtils.formatTemperature(mContext, lowInCelsius);
        String lowAlly = mContext.getString(R.string.a11y_low_temp, lowString);
        //Set the text and content description (for accessibility purpose)
        forecastAdapterViewHolder.lowTemperatureView.setText(lowString);
        forecastAdapterViewHolder.lowTemperatureView.setContentDescription(lowAlly);

    }

    @Override
    public int getItemCount() {
        if(mCursor == null) {
            return 0;
        }

        return mCursor.getCount();
    }

    // Create a new method that allows you to swap Cursors.
    void swapCursor(Cursor newCursor){
        mCursor = newCursor;
        //      COMPLETED (12) After the new Cursor is set, call notifyDataSetChanged
        notifyDataSetChanged();
    }

    // Implement OnClickListener in the ForecastAdapterViewHolder class
    /**
     * Cache of the children views for a forecast list item.
     */

    public class ForecastAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        //Replace the weatherSummary TextView with individual weather detail TextViews
        final ImageView weatherIconView;
        final TextView dateView;
        final TextView descrptionView;
        final TextView highTemperatureView;
        final TextView lowTemperatureView;

        public ForecastAdapterViewHolder(View view) {
            super(view);

           // Get references to all new views and delete this line
            weatherIconView = (ImageView) view.findViewById(R.id.weather_icon);
            dateView = (TextView) view.findViewById(R.id.date);
            descrptionView = (TextView) view.findViewById(R.id.weather_description);
            highTemperatureView = (TextView) view.findViewById(R.id.high_temperature);
            lowTemperatureView = (TextView) view.findViewById(R.id.low_temperature);

            // Call setOnClickListener on the view passed into the constructor (use 'this' as the OnClickListener)
            view.setOnClickListener(this);
        }

        /**
         * This gets called by the child views during a click.
         *
         * @param view The View that was clicked
         */
        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
//          COMPLETED (37) Instead of passing the String for the clicked item, pass the date from the cursor
            mCursor.moveToPosition(adapterPosition);
            long dateInMillis = mCursor.getLong(MainActivity.INDEX_WEATHER_DATE);
            mClickHandler.onClick(dateInMillis);
        }
    }
}
