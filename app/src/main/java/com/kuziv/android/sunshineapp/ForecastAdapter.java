package com.kuziv.android.sunshineapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ForecastAdapter extends CursorAdapter {

    private final int VIEW_TYPE_TODAY = 0;
    private final int VIEW_TYPE_FUTURE = 1;

    private final int VIEW_TYPE_COUNT = 2;

    private Context context;

    public ForecastAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
        this.context = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        int position = cursor.getPosition();
        int viewType = getItemViewType(position);

        int layoutId = -1;
        switch (viewType) {
            case VIEW_TYPE_TODAY:
                layoutId = R.layout.list_item_forecast_today;
                break;

            case VIEW_TYPE_FUTURE:
                layoutId = R.layout.list_item_forecast;
                break;
        }

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);

        ForecastViewHolder viewHolder = new ForecastViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ForecastViewHolder viewHolder = (ForecastViewHolder)view.getTag();

        int itemViewType = getItemViewType(cursor.getPosition());

        switch (itemViewType) {

            case VIEW_TYPE_TODAY:
                viewHolder.icon.setImageResource(Utility.getArtResourceForWeatherCondition(cursor.getInt(ForecastFragment.COL_WEATHER_CONDITION_ID)));
                break;

            case VIEW_TYPE_FUTURE:
                viewHolder.icon.setImageResource(Utility.getIconResourceForWeatherCondition(cursor.getInt(ForecastFragment.COL_WEATHER_CONDITION_ID)));
                break;
        }

        long dateInMillis = cursor.getLong(ForecastFragment.COL_WEATHER_DATE);
        viewHolder.date.setText(Utility.formatDate(dateInMillis));

        // Read weather forecast from cursor
        String description = cursor.getString(ForecastFragment.COL_WEATHER_DESC);
        // Find TextView and set weather forecast on it
        viewHolder.forecast.setText(description);

        // For accessibility, add a content description to the icon field
        viewHolder.icon.setContentDescription(description);

        // Read user preference for metric or imperial temperature units
        boolean isMetric = Utility.isMetric(context);

        // Read high temperature from cursor
        double high = cursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP);
        viewHolder.highTemp.setText(Utility.formatTemperature(context, high, isMetric));

        // Read low temperature from cursor
        double low = cursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP);
        viewHolder.lowTemp.setText(Utility.formatTemperature(context, low, isMetric));
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    // position from Cursor!!!
    @Override
    public int getItemViewType(int position) {
        return (position == 0)? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE;
    }

    private String formatHighLows(double high, double low) {
        boolean isMetric = Utility.isMetric(context);
        String highLowStr = Utility.formatTemperature(context, high, isMetric) + "/" + Utility.formatTemperature(context, low, isMetric);
        return highLowStr;
    }

    public static class ForecastViewHolder {

        final ImageView icon;
        final TextView date;
        final TextView forecast;
        final TextView highTemp;
        final TextView lowTemp;

        public ForecastViewHolder(View view) {

            icon = (ImageView) view.findViewById(R.id.list_item_icon);
            date = (TextView) view.findViewById(R.id.list_item_date_textview);
            forecast = (TextView) view.findViewById(R.id.list_item_forecast_textview);
            highTemp = (TextView) view.findViewById(R.id.list_item_hight_textview);
            lowTemp = (TextView) view.findViewById(R.id.list_item_low_textview);

        }
    }

}

