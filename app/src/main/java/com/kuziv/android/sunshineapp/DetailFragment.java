package com.kuziv.android.sunshineapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kuziv.android.sunshineapp.data.WeatherContract;
import com.kuziv.android.sunshineapp.data.WeatherContract.WeatherEntry;

public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = "DetailFragment";

    private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";

    private static final int DETAIL_LOADER_ID = 0;

    private static final String[] FORECAST_COLUMNS = {
            WeatherEntry.TABLE_NAME + "." + WeatherEntry._ID,
            WeatherEntry.COLUMN_DATE,
            WeatherEntry.COLUMN_SHORT_DESC,
            WeatherEntry.COLUMN_MAX_TEMP,
            WeatherEntry.COLUMN_MIN_TEMP,
            WeatherEntry.COLUMN_HUMIDITY,
            WeatherEntry.COLUMN_PRESSURE,
            WeatherEntry.COLUMN_WIND_SPEED,
            WeatherEntry.COLUMN_DEGREES,
            WeatherEntry.COLUMN_WEATHER_ID,
            // This works because the WeatherProvider returns location data joined with
            // weather data, even though they're stored in two different tables.
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING
    };

    public static final int COL_WEATHER_ID = 0;
    public static final int COL_WEATHER_DATE = 1;
    public static final int COL_WEATHER_DESC = 2;
    public static final int COL_WEATHER_MAX_TEMP = 3;
    public static final int COL_WEATHER_MIN_TEMP = 4;
    public static final int COL_WEATHER_HUMIDITY = 5;
    public static final int COL_WEATHER_PRESSURE = 6;
    public static final int COL_WEATHER_WIND_SPEED = 7;
    public static final int COL_WEATHER_DEGREES = 8;
    public static final int COL_WEATHER_CONDITION_ID = 9;

    private ShareActionProvider mShareActionProvider;
    private String mForecast;
    private DetailViewHolder viewHolder;

    public DetailFragment() {
        Log.d(LOG_TAG, "new DetailFragment()");
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        // Initialise my view holder
        viewHolder = new DetailViewHolder(view);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.detail_fragment, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold into it to set/change the share intent.
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // If onLoadFinished happens before this, we can go ahead and set the share intent now.
        if (mForecast != null) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }
    }

    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mForecast + FORECAST_SHARE_HASHTAG);
        return shareIntent;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG, "In onCreateLoader()");
        Intent intent = getActivity().getIntent();
        if (intent == null) {
            return null;
        }

        return new CursorLoader(
                getActivity(),
                intent.getData(),
                FORECAST_COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.v(LOG_TAG, "onLoadFinished() is called");

        if (!cursor.moveToFirst()) {
            return;
        }

        int weatherId = cursor.getInt(COL_WEATHER_CONDITION_ID);
        viewHolder.image.setImageResource(Utility.getArtResourceForWeatherCondition(weatherId));

        String date = Utility.formatDate(cursor.getLong(COL_WEATHER_DATE));
        viewHolder.date.setText(date);

        String weatherDescription = cursor.getString(COL_WEATHER_DESC);
        viewHolder.forecast.setText(weatherDescription);

        boolean isMetric = Utility.isMetric(getActivity());

        String high = Utility.formatTemperature(getContext(), cursor.getDouble(COL_WEATHER_MAX_TEMP), isMetric);
        viewHolder.max.setText(high);

        String low = Utility.formatTemperature(getContext(), cursor.getDouble(COL_WEATHER_MIN_TEMP), isMetric);
        viewHolder.min.setText(low);

        String humidity = String.valueOf(cursor.getInt(COL_WEATHER_HUMIDITY));
        viewHolder.humidity.setText(humidity);

        String pressure = String.valueOf(cursor.getInt(COL_WEATHER_PRESSURE));
        viewHolder.pressure.setText(pressure);

        String wind_speed = String.valueOf(cursor.getInt(COL_WEATHER_WIND_SPEED));
        viewHolder.wind_speed.setText(wind_speed);

        // If onCreateOptionsMenu has already happened, we need to update the share intent now.
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }

    public static final class DetailViewHolder {

        private TextView day;
        private TextView date;
        private TextView max;
        private TextView min;
        private ImageView image;
        private TextView forecast;
        private TextView humidity;
        private TextView pressure;
        private TextView wind_speed;

        public DetailViewHolder(View view) {
            day = (TextView) view.findViewById(R.id.list_item_day_textview); //???
            date = (TextView) view.findViewById(R.id.list_item_date_textview);
            max = (TextView) view.findViewById(R.id.list_item_hight_textview);
            min = (TextView) view.findViewById(R.id.list_item_low_textview);
            image = (ImageView) view.findViewById(R.id.list_item_icon);
            forecast = (TextView) view.findViewById(R.id.list_item_forecast_textview);
            humidity = (TextView) view.findViewById(R.id.detail_humidity_textview);
            pressure = (TextView) view.findViewById(R.id.detail_pressure_textview);
            wind_speed = (TextView) view.findViewById(R.id.detail_wind_textview);
        }

    }
}
