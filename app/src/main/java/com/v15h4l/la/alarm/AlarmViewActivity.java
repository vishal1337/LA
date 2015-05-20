package com.v15h4l.la.alarm;

import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.v15h4l.la.Database.AlarmDBHelper;
import com.v15h4l.la.R;
import com.v15h4l.la.model.Alarm;


public class AlarmViewActivity extends ActionBarActivity {

    Alarm alarm;
    AlarmDBHelper dbHelper;

    Context mContext;

    private LinearLayout locationPicker;
    private TextView alarmTime;
    private TextView alarmName;
    private TextView alarmRecurrence;
    private TextView alarmLocationName;
    private TextView alarmDescription;
    private TextView alarmTone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_view);

        dbHelper = new AlarmDBHelper(this);

        mContext = this;

        //Alarm Time View
        alarmTime = (TextView) findViewById(R.id.alarm_time);

        //Alarm Name View
        alarmName = (TextView) findViewById(R.id.alarm_name);

        //Alarm RepeatDays View
        alarmRecurrence = (TextView) findViewById(R.id.alarm_recurrence);

        //Alarm Location Picker
        locationPicker = (LinearLayout) findViewById(R.id.layout_location);
        alarmLocationName = (TextView) findViewById(R.id.alarm_location);

        //Alarm Description
        alarmDescription = (TextView) findViewById(R.id.alarm_description);

        //Alarm Tone
        alarmTone = (TextView) findViewById(R.id.alarm_tone);

        //Get alarm id
        long id = getIntent().getExtras().getLong("id");

        alarm = dbHelper.getAlarm(id);

        String weekdays="";
        for(int i=0; i<alarm.repeatingDays.length; i++)
        {
            weekdays+=alarm.getRepeatingDay(i)+" ";
        }

        Log.v("AlarmViewActivity","Week Days: "+weekdays);
        Log.v("AlarmViewActivity","Weekly: "+alarm.repeatWeekly);

        alarmTime.setText(String.format("%02d", alarm.timeHour) + ":" + String.format("%02d", alarm.timeMinute));
        if (!alarm.name.isEmpty()){ alarmName.setText(alarm.name); }
        alarmRecurrence.setText(alarm.recurrenceMessage);
        if (!alarm.location_name.isEmpty()){ alarmLocationName.setText(alarm.location_name); }
        if (!alarm.description.isEmpty()) { alarmDescription.setText(alarm.description); }
        alarmTone.setText(RingtoneManager.getRingtone(this, alarm.alarmTone).getTitle(this));

        //Alarm Location Navigation
        locationPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!alarm.location_lat.equals("0") && !alarm.location_lat.equals("0")){
                    if (isConnectedToInternet()) {
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                Uri.parse("http://maps.google.com/maps?daddr=" + alarm.location_lat + "," + alarm.location_lon));
                        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                        startActivity(intent);
                    } else {
                        Toast.makeText(mContext, "Connect to Internet", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(mContext,"No location found",Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_alarm_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_edit) {
            Intent intent=new Intent();
            intent.putExtra("id",alarm.id);
            setResult(-1, intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean isConnectedToInternet(){
        ConnectivityManager connectivity = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
