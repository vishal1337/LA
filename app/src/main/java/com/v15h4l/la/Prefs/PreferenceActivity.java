package com.v15h4l.la.Prefs;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import com.v15h4l.la.Fragments.SettingsFragment;
import com.v15h4l.la.R;


public class PreferenceActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("PreferenceActivity", "onCreate()");
        setContentView(R.layout.activity_preference);
        getFragmentManager().beginTransaction()
                .add(R.id.preference_container,new SettingsFragment())
                .commit();
    }

}
