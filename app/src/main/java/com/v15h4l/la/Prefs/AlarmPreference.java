package com.v15h4l.la.Prefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by v15h4l on 5/14/2015.
 */
public class AlarmPreference {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    String RADIUS = "radius";

    public AlarmPreference(Context context){
        Log.v("CityPreference", "constructor");
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPreferences.edit();
        editor.apply();
    }

    // By Default returns 5 as area radius
    public int getRadius(){
        return sharedPreferences.getInt(RADIUS, 5);
    }

    public void setRadius(int radius){
        editor.putInt(RADIUS, radius);
        editor.apply();
    }
}
