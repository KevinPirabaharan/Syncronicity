package com.example.avjindersinghsekhon.minimaltodo;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceFragment;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener{
    AnalyticsApplication app;


    //Function onCreate
    //IN: Bundle savedInstanceState
    //Bundle is: A mapping from String keys to values of various types. In most cases, you should work directly with either the Bundle or PersistableBundle subclass.
    @Override
    //When the app is opened this function pulls the users preferences from the cml.preferences.layout
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences_layout);
        app = (AnalyticsApplication) getActivity().getApplication();
   }

    @Override
    //When the user changes their preferences this is the function that handles this
    //This function changes/saves the users preferences when they choose to change them and also applies the new theme
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        PreferenceKeys preferenceKeys = new PreferenceKeys(getResources());
        if(key.equals(preferenceKeys.night_mode_pref_key)){
            SharedPreferences themePreferences = getActivity().getSharedPreferences(MainActivity.THEME_PREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor themeEditor = themePreferences.edit();
            //We tell our MainLayout to recreate itself because mode has changed
            themeEditor.putBoolean(MainActivity.RECREATE_ACTIVITY, true);

            CheckBoxPreference checkBoxPreference = (CheckBoxPreference)findPreference(preferenceKeys.night_mode_pref_key);
            if(checkBoxPreference.isChecked()){
                //Comment out this line if not using Google Analytics
                app.send(this, "Settings", "Night Mode used");
                themeEditor.putString(MainActivity.THEME_SAVED, MainActivity.DARKTHEME);
            }
            else{
                themeEditor.putString(MainActivity.THEME_SAVED, MainActivity.LIGHTTHEME);
            }
            themeEditor.apply();

            getActivity().recreate();
        }
    }

    @Override
    //When the app is reopened gets the users preferences ie nightMode/ not nightMode
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    //When the app is paused stores the users preferences
    public void onPause() {
        super.onPause();
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}
