package com.v15h4l.la.alarm;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.doomonafireball.betterpickers.radialtimepicker.RadialTimePickerDialog;
import com.doomonafireball.betterpickers.recurrencepicker.EventRecurrence;
import com.doomonafireball.betterpickers.recurrencepicker.EventRecurrenceFormatter;
import com.doomonafireball.betterpickers.recurrencepicker.RecurrencePickerDialog;
import com.v15h4l.la.Database.AlarmDBHelper;
import com.v15h4l.la.R;
import com.v15h4l.la.location.LocationPickerActivity;
import com.v15h4l.la.model.Alarm;

import org.joda.time.DateTime;


public class AlarmDetailsActivity extends ActionBarActivity
        implements RadialTimePickerDialog.OnTimeSetListener, RecurrencePickerDialog.OnRecurrenceSetListener {

    final static String TAG = AlarmDetailsActivity.class.getSimpleName();

    Alarm alarm;
    AlarmDBHelper dbHelper;

    Context mContext;

    private static final String FRAG_TAG_TIME_PICKER = "timePickerDialogFragment";
    private static final String FRAG_TAG_RECUR_PICKER = "recurrencePickerDialogFragment";

    private EventRecurrence mEventRecurrence = new EventRecurrence();
    private String mRrule;

    private LinearLayout timePicker;
    private LinearLayout recurrencePicker;
    private LinearLayout locationPicker;
    private LinearLayout tonePicker;
    private TextView alarmTime;
    private EditText alarmName;
    private TextView alarmRecurrence;
    private TextView alarmLocationName;
    private EditText alarmDescription;
    private TextView alarmTone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_details);

        mContext = this;

        Log.i(TAG, "onCreate was Called");

        dbHelper = new AlarmDBHelper(this);

        //Alarm Time Picker
        timePicker = (LinearLayout) findViewById(R.id.layout_time);
        alarmTime = (TextView) findViewById(R.id.alarm_time);

        //Alarm Name
        alarmName = (EditText) findViewById(R.id.alarm_name);

        //Alarm RepeatDays
        recurrencePicker = (LinearLayout) findViewById(R.id.Layout_recurrence);
        alarmRecurrence = (TextView) findViewById(R.id.alarm_recurrence);

        //Alarm Location Picker
        locationPicker = (LinearLayout) findViewById(R.id.layout_location);
        alarmLocationName = (TextView) findViewById(R.id.alarm_location);

        //Alarm Description
        alarmDescription = (EditText) findViewById(R.id.alarm_description);

        //Alarm Tone
        tonePicker = (LinearLayout) findViewById(R.id.layout_tone);
        alarmTone = (TextView) findViewById(R.id.alarm_tone);

        //Check if Alarm is being edited
        long id = getIntent().getExtras().getLong("id");


        alarm = new Alarm();

        //Setting Alarm Details is found in Database
        if (id == -1){
            DateTime now = DateTime.now();
            alarmTime.setText(String.format("%02d", now.getHourOfDay()) + ":" + String.format("%02d", now.getMinuteOfHour()));
            alarm.timeHour = now.getHourOfDay();
            alarm.timeMinute = now.getMinuteOfHour();
            mRrule = "FREQ=WEEKLY;WKST=MO;BYDAY="+getDay();
            onRecurrenceSet(mRrule);
            alarmTone.setText(RingtoneManager.getRingtone(this,alarm.alarmTone).getTitle(this));

        }else {
            alarm = dbHelper.getAlarm(id);
            alarmTime.setText(String.format("%02d", alarm.timeHour) + ":" + String.format("%02d", alarm.timeMinute));
            if (!alarm.name.isEmpty()){ alarmName.append(alarm.name); }
            mRrule = alarm.recurrence;
            onRecurrenceSet(mRrule);
            if (!alarm.location_name.isEmpty()) { alarmLocationName.setText(alarm.location_name); }
            if (!alarm.description.isEmpty()) { alarmDescription.append(alarm.description); }
            alarmTone.setText(RingtoneManager.getRingtone(this,alarm.alarmTone).getTitle(this));
        }

        //Alarm Time Picker Dialog
        timePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadialTimePickerDialog timePickerDialog = RadialTimePickerDialog
                        .newInstance(AlarmDetailsActivity.this, alarm.timeHour, alarm.timeMinute,
                                DateFormat.is24HourFormat(AlarmDetailsActivity.this));
                timePickerDialog.show(getSupportFragmentManager(), FRAG_TAG_TIME_PICKER);
            }
        });

        //Alarm Repeat Days picker Dialog
        recurrencePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();
                Bundle b = new Bundle();
                Time t = new Time();
                t.setToNow();
                b.putLong(RecurrencePickerDialog.BUNDLE_START_TIME_MILLIS, t.toMillis(false));
                b.putString(RecurrencePickerDialog.BUNDLE_TIME_ZONE, t.timezone);

                // may be more efficient to serialize and pass in EventRecurrence
                b.putString(RecurrencePickerDialog.BUNDLE_RRULE, mRrule);

                RecurrencePickerDialog rpd = (RecurrencePickerDialog) fm.findFragmentByTag(
                        FRAG_TAG_RECUR_PICKER);
                if (rpd != null) {
                    rpd.dismiss();
                }
                rpd = new RecurrencePickerDialog();
                rpd.setArguments(b);
                rpd.setOnRecurrenceSetListener(AlarmDetailsActivity.this);
                rpd.show(fm, FRAG_TAG_RECUR_PICKER);
            }
        });

        //Alarm Location picker
        locationPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isConnectedToInternet())
                {
                    Intent intent = new Intent(AlarmDetailsActivity.this, LocationPickerActivity.class);
                    startActivityForResult(intent, 0);

                }else{

                    Toast.makeText(mContext,"No Connectivity",Toast.LENGTH_LONG).show();

                }

            }
        });

        //Alarm Tone picker Dialog
        tonePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Alarm Tone");
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, alarm.alarmTone);
                startActivityForResult(intent,1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case 0:
                if (resultCode == RESULT_OK) {

                    String locationName = data.getStringExtra("locationName");

                    alarm.location_lat = String.valueOf(data.getDoubleExtra("LocationLat", 0));
                    alarm.location_lon = String.valueOf(data.getDoubleExtra("LocationLon",0));

                    if (!locationName.isEmpty()) {
                        alarm.location_name = locationName;
                        alarmLocationName.setText(locationName);
                    }

                }
            break;

            case 1:
                if (resultCode == RESULT_OK) {
                    alarm.alarmTone = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                    alarmTone.setText(RingtoneManager.getRingtone(this, alarm.alarmTone).getTitle(this));
                }
            break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_alarm_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()){
            case R.id.save_alarm:
                updateFromLayout();

                //Cancel All Alarms
                AlarmManagerHelper.cancelAlarms(this);

                if (alarm.id < 0){
                    dbHelper.createAlarm(alarm);
                }else{
                    dbHelper.updateAlarm(alarm);
                }

                //Cancel All Alarms
                AlarmManagerHelper.setAlarms(this);

                setResult(RESULT_OK);
                finish();

            break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateFromLayout(){

        Log.i(TAG, "updateFromLayout was Called");

        alarm.isEnabled = true;
        if (!String.valueOf(alarmName.getText()).equals("")){ alarm.name = String.valueOf(alarmName.getText()); }
        if (!String.valueOf(alarmDescription.getText()).equals("")){ alarm.description = String.valueOf(alarmDescription.getText()); }

    }

    @Override
    protected void onResume() {
        // Example of reattaching to the fragment
        super.onResume();
        RecurrencePickerDialog rpd = (RecurrencePickerDialog) getSupportFragmentManager().findFragmentByTag(
                FRAG_TAG_RECUR_PICKER);
        if (rpd != null) {
            rpd.setOnRecurrenceSetListener(this);
        }
    }

    @Override
    public void onTimeSet(RadialTimePickerDialog dialog, int hourOfDay, int minute) {

        alarm.timeHour = hourOfDay;
        alarm.timeMinute = minute;
        alarmTime.setText(String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute));
    }

    @Override
    public void onRecurrenceSet(String rRule) {
        mRrule = rRule;
        if (mRrule != null) {
            alarm.recurrence = rRule;
            Log.v("AlarmDetailsActivity", "rRule: "+rRule);
            mEventRecurrence.parse(mRrule);
        }else{
            alarm.recurrence = null;
        }
        populateRepeats();
    }
    private void populateRepeats() {
        Resources r = getResources();
        if (!TextUtils.isEmpty(mRrule)) {
            alarm.recurrenceMessage=EventRecurrenceFormatter.getRepeatString(this, r, mEventRecurrence, true);
            alarmRecurrence.setText(alarm.recurrenceMessage);
        }else {
            alarm.recurrenceMessage = "Never";
            alarmRecurrence.setText(alarm.recurrenceMessage);
        }
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

    public String getDay(){

        switch (DateTime.now().getDayOfWeek()){
            case 1:
                return "MO";
            case 2:
                return "TU";
            case 3:
                return "WE";
            case 4:
                return "TH";
            case 5:
                return "FR";
            case 6:
                return "SA";
            case 7:
                return "SU";
        }
        return "";
    }
}
