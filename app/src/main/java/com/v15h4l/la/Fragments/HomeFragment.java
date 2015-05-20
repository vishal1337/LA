package com.v15h4l.la.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.shamanland.fab.FloatingActionButton;
import com.v15h4l.la.Database.AlarmDBHelper;
import com.v15h4l.la.Prefs.PreferenceActivity;
import com.v15h4l.la.R;
import com.v15h4l.la.adapter.AlarmListAdapter;
import com.v15h4l.la.alarm.AlarmDetailsActivity;
import com.v15h4l.la.alarm.AlarmManagerHelper;
import com.v15h4l.la.alarm.AlarmViewActivity;
import com.v15h4l.la.model.Alarm;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;

public class HomeFragment extends ListFragment {

    private final String TAG = HomeFragment.class.getSimpleName();

    AlarmDBHelper dbHelper;
    AlarmListAdapter mAdapter;
    Context mContext;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;

        Log.i(TAG, "onAttach was Called");

        dbHelper = new AlarmDBHelper(mContext);

        mAdapter = new AlarmListAdapter(mContext,dbHelper.getAlarms(),HomeFragment.this);
        setListAdapter(mAdapter);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        JodaTimeAndroid.init(mContext);
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        switch (dayTime())
        {
            case "morning":
                rootView.findViewById(R.id.header).setBackgroundResource(R.drawable.wall_morning);
            break;
            case "day":
                rootView.findViewById(R.id.header).setBackgroundResource(R.drawable.wall_day);
            break;
            case "evening":
                rootView.findViewById(R.id.header).setBackgroundResource(R.drawable.wall_evening);
            break;
            case "night":
                rootView.findViewById(R.id.header).setBackgroundResource(R.drawable.wall_night);
            break;
            default:
                rootView.findViewById(R.id.header).setBackgroundResource(R.drawable.wall_day);
            break;
        }
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        Log.v("Home Fragment: ColorList: ",fab.getColorStateList()+"");
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAlarmDetailsActivity(-1);
            }
        });
        return rootView;
    }


    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_home, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.add_alarm:
                startAlarmDetailsActivity(-1);
            break;
            case R.id.action_settings:
                startActivity(new Intent(mContext, PreferenceActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    public void setAlarmEnabled(Long id, Boolean isChecked){

        Log.i(TAG,"setAlarmEnabled was Called");

        //Cancel All Alarms
        AlarmManagerHelper.cancelAlarms(mContext);

        Alarm alarm = dbHelper.getAlarm(id);
        alarm.isEnabled = isChecked;
        dbHelper.updateAlarm(alarm);
        mAdapter.setAlarms(dbHelper.getAlarms());
        mAdapter.notifyDataSetChanged();

        //ReSet Alarms
        AlarmManagerHelper.setAlarms(mContext);
    }

    public void deleteAlarm(Long id){

        Log.i(TAG,"deleteAlarm was Called");

        final Long alarmId = id;
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Delete Alarm?")
                .setCancelable(true)
                .setNegativeButton("Abort",null)
                .setPositiveButton("Proceed",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    //Cancel All Alarms
                    AlarmManagerHelper.cancelAlarms(mContext);

                    dbHelper.deleteAlarm(alarmId);
                    mAdapter.setAlarms(dbHelper.getAlarms());
                    mAdapter.notifyDataSetChanged();

                    //ReSet Alarms
                    AlarmManagerHelper.setAlarms(mContext);
                    }
                })
                .show();
    }

    public void startAlarmViewActivity(long id){

        Log.i(TAG,"startAlarmDetailsActivity was Called");

        Intent intent = new Intent(mContext,AlarmViewActivity.class);
        intent.putExtra("id",id);
        startActivityForResult(intent, 0);

    }

    public void startAlarmDetailsActivity(long id){

        Log.i(TAG, "startAlarmDetailsActivity was Called");

        Intent intent = new Intent(mContext,AlarmDetailsActivity.class);
        intent.putExtra("id",id);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){

            case 0:
                if(resultCode == Activity.RESULT_OK){
                    startAlarmDetailsActivity(data.getLongExtra("id",0));
                }
            break;

            case 1:
                if (resultCode == Activity.RESULT_OK){
                    switch (requestCode){
                        case 1:
                            mAdapter.setAlarms(dbHelper.getAlarms());
                            mAdapter.notifyDataSetChanged();
                            break;
                    }
                }
            break;

        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public String dayTime(){

        DateTime now = DateTime.now();
        int hourOfDay = now.getHourOfDay();

        if (hourOfDay >= 6 && hourOfDay <=10)
        {
            return "morning";
        }
        else if (hourOfDay >= 11 && hourOfDay <17)
        {
            return "day";
        }
        else if (hourOfDay >=17 && hourOfDay < 19)
        {
            return "evening";
        }
        return "night";
    }
}
