package com.v15h4l.la;

import android.content.Intent;
import android.content.res.Resources;
import android.media.RingtoneManager;
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

import com.doomonafireball.betterpickers.radialtimepicker.RadialTimePickerDialog;
import com.doomonafireball.betterpickers.recurrencepicker.EventRecurrence;
import com.doomonafireball.betterpickers.recurrencepicker.EventRecurrenceFormatter;
import com.doomonafireball.betterpickers.recurrencepicker.RecurrencePickerDialog;
import com.v15h4l.la.Database.AlarmDBHelper;
import com.v15h4l.la.model.Alarm;

import org.joda.time.DateTime;


public class AlarmDetailsActivity extends ActionBarActivity
        implements RadialTimePickerDialog.OnTimeSetListener, RecurrencePickerDialog.OnRecurrenceSetListener {

    final static String TAG = AlarmDetailsActivity.class.getSimpleName();
    Alarm alarm;
    AlarmDBHelper dbHelper;

    private static final String FRAG_TAG_TIME_PICKER = "timePickerDialogFragment";
    private static final String FRAG_TAG_RECUR_PICKER = "recurrencePickerDialogFragment";

    private EventRecurrence mEventRecurrence = new EventRecurrence();
    private String mRrule;

    private LinearLayout timePicker;
    private LinearLayout tonePicker;
    private LinearLayout recurrencePicker;
    private TextView time;
    private EditText alarmName;
    private TextView recurrence;
    private TextView tone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_details);

        Log.i(TAG, "onCreate was Called");
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.color.purple_500)); //Change ActionBar Color
        dbHelper = new AlarmDBHelper(this);

        timePicker = (LinearLayout) findViewById(R.id.layout_time);
        time = (TextView) findViewById(R.id.alarm_time);

        alarmName = (EditText) findViewById(R.id.alarm_name);

        recurrencePicker = (LinearLayout) findViewById(R.id.Layout_recurrence);
        recurrence = (TextView) findViewById(R.id.alarm_recurrence);

        tonePicker = (LinearLayout) findViewById(R.id.layout_tone);
        tone = (TextView) findViewById(R.id.alarm_tone);

        long id = getIntent().getExtras().getLong("id");

        if (id == -1){
            alarm = new Alarm();
            tone.setText(RingtoneManager.getRingtone(this,alarm.alarmTone).getTitle(this));
        }else {
            alarm = dbHelper.getAlarm(id);
            time.setText(alarm.timeHour+":"+String.format("%02d", alarm.timeMinute));
            alarmName.setText(alarm.name);
            tone.setText(RingtoneManager.getRingtone(this,alarm.alarmTone).getTitle(this));
        }


        timePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTime now = DateTime.now();
                RadialTimePickerDialog timePickerDialog = RadialTimePickerDialog
                        .newInstance(AlarmDetailsActivity.this, now.getHourOfDay(), now.getMinuteOfHour(),
                                DateFormat.is24HourFormat(AlarmDetailsActivity.this));
                timePickerDialog.show(getSupportFragmentManager(), FRAG_TAG_TIME_PICKER);
            }
        });

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

        tonePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Alarm Tone");
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, alarm.alarmTone);
                startActivityForResult(intent,0);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            alarm.alarmTone = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            tone.setText(RingtoneManager.getRingtone(this,alarm.alarmTone).getTitle(this));
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

        Log.i(TAG,"updateFromLayout was Called");
/*
        alarm.isEnabled = true;
        alarm.timeHour = timePicker.getCurrentHour();
        alarm.timeMinute = timePicker.getCurrentMinute();
        if (!String.valueOf(alarmName.getText()).equals("")){ alarm.name = String.valueOf(alarmName.getText()); }

*/    }

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
        time.setText(String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute));
    }

    @Override
    public void onRecurrenceSet(String rRule) {
        mRrule = rRule;
        if (mRrule != null) {
            mEventRecurrence.parse(mRrule);
        }
        populateRepeats();
    }
    private void populateRepeats() {
        Resources r = getResources();
        String repeatString = "";
        if (!TextUtils.isEmpty(mRrule)) {
            repeatString = EventRecurrenceFormatter.getRepeatString(this, r, mEventRecurrence, true);
        }

        recurrence.setText(repeatString);
    }
}
