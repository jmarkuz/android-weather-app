package com.kuziv.android.sunshineapp;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.kuziv.android.sunshineapp.R;

public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener {

    private static final String LOG_TAG = "SettingsActivity";

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, " onCreate()");

        addPreferencesFromResource(R.xml.pref_general);

        attachPreferenceChangeListener(findPreference(getString(R.string.pref_location_key)));
        attachPreferenceChangeListener(findPreference(getString(R.string.pref_units_key)));
    }

    private void attachPreferenceChangeListener(Preference preference) {

        preference.setOnPreferenceChangeListener(this);

        // Trigger the listener immediately with the preference's current value.
        onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {

        String strValue = newValue.toString();

        if (preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(strValue);
            if (prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        } else {
            preference.setSummary(strValue);
        }

        return true;
    }

}
