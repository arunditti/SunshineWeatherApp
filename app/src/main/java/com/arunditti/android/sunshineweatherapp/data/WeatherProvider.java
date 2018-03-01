package com.arunditti.android.sunshineweatherapp.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.arunditti.android.sunshineweatherapp.utilities.SunshineDateUtils;

import java.util.SimpleTimeZone;

/**
 * Created by arunditti on 2/28/18.
 */

public class WeatherProvider extends ContentProvider {

    private WeatherDbHelper mDbHelper;

    public static final int CODE_WEATHER = 100;
    public static final int CODE_WEATHER_WITH_DATE = 101;

    //Instantiate a statci UriMatcher using the buildUriMatcher
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    //Write a method called buildUriMatcher where we match URI's to their numeric ID
    public static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(WeatherContract.CONTENT_AUTHORITY, WeatherContract.PATH_WEATHER, CODE_WEATHER);
        matcher.addURI(WeatherContract.CONTENT_AUTHORITY, WeatherContract.PATH_WEATHER + "/#",CODE_WEATHER_WITH_DATE);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        // Instantiate mDbHelper
        mDbHelper = new WeatherDbHelper(getContext());
        return true;
    }

    public int bulkInsert(Uri uri, ContentValues[] values) {

        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        switch (sUriMatcher.match(uri)) {
            //Only perform our implementation of bulkInsert if the URI matches the CODE_WEATHER code
            case CODE_WEATHER:
                db.beginTransaction();
                int rowsInserted = 0;
                try {
                    for(ContentValues value : values) {
                        long weatherDate = value.getAsLong(WeatherContract.WeatherEntry.COLUMN_DATE);

                        if(!SunshineDateUtils.isDateNormalized(weatherDate)) {
                            throw new IllegalArgumentException("Date must be normalized to insert");
                        }

                        long _id = db.insert(WeatherContract.WeatherEntry.TABLE_NAME, null, value);
                        if(_id != -1) {
                            rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();

                } finally {

                }

                if(rowsInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                //Return the number of rows inserted from our implementation of bulkInsert
                return rowsInserted;

                //If the URI does match match CODE_WEATHER, return the super implementation of bulkInsert
                default:
                    return super.bulkInsert(uri, values);
        }
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor;
        int match = sUriMatcher.match(uri);
        switch (match) {
        case CODE_WEATHER:
            cursor = database.query(WeatherContract.WeatherEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
             break;
            case CODE_WEATHER_WITH_DATE:
                selection = WeatherContract.WeatherEntry._ID + "=?";
                String normalizedUtcDateString = uri.getLastPathSegment();
                String[] selectionArguments = new String[]{normalizedUtcDateString};
                cursor = database.query(WeatherContract.WeatherEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArguments,
                        null,
                        null,
                        sortOrder);
                break;

            default:
                    throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    /**
     * You do not need to call this method. This is a method specifically to assist the testing
     * framework in running smoothly. You can read more at:
     * http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
     */
    @Override
    @TargetApi(11)
    public void shutdown() {
        mDbHelper.close();
        super.shutdown();
    }
}
