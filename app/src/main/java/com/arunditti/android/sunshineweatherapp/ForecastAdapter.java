package com.arunditti.android.sunshineweatherapp;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        void onClick(String weatherForDay);
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

        //Read date from the cursor
        int dateInMillisIdex = mCursor.getColumnIndex(WeatherEntry.COLUMN_DATE);
        long dateInMillis = mCursor.getLong(dateInMillisIdex);
        //FGet human readable string using Utility method
        String dateString = SunshineDateUtils.getFriendlyDateString(mContext, dateInMillis, false);

        //Use weather Id to get the proper desrciption
        int weatherIdIndex = mCursor.getColumnIndex(WeatherEntry.COLUMN_WEATHER_ID);
        int weatherId = mCursor.getInt(weatherIdIndex);
        String description = SunshineWeatherUtils.getStringForWeatherCondition(mContext, weatherId);

        //Read high temperature from cursor
        int highInCelsiusIndex = mCursor.getColumnIndex(WeatherEntry.COLUMN_MAX_TEMP);
        double highInCelsius = mCursor.getInt(highInCelsiusIndex);
        //Read low temperature from cursor
        int lowInCelsiusIndex = mCursor.getColumnIndex(WeatherEntry.COLUMN_MIN_TEMP);
        double lowInCelsius = mCursor.getInt(lowInCelsiusIndex);


        String highTemperature = SunshineWeatherUtils.formatTemperature(mContext, highInCelsius);
        String lowTemperature = SunshineWeatherUtils.formatTemperature(mContext, lowInCelsius);

        String weatherSummary = dateString + " - " + description + " - " + highTemperature + " / " + lowTemperature;

//      COMPLETED (8) Display the summary that you created above
        forecastAdapterViewHolder.weatherSummary.setText(weatherSummary);

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

        final TextView weatherSummary;

        public ForecastAdapterViewHolder(View view) {
            super(view);
            weatherSummary = (TextView) view.findViewById(R.id.tv_weather_data);

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
            String weatherForDay = weatherSummary.getText().toString();
            mClickHandler.onClick(weatherForDay);

        }
    }
}
