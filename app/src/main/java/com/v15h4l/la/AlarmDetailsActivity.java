package com.v15h4l.la;

import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.v15h4l.la.Database.AlarmDBHelper;
import com.v15h4l.la.model.Alarm;


public class AlarmDetailsActivity extends ActionBarActivity {

    final static String TAG = AlarmDetailsActivity.class.getSimpleName();
    Alarm alarm;
    AlarmDBHelper dbHelper;

    private TimePicker timePicker;
    private EditText alarmName;
    private CheckBox chkWeekly;
    private CheckBox chkSunday;
    private CheckBox chkMonday;
    private CheckBox chkTuesday;
    private CheckBox chkWednesday;
    private CheckBox chkThursday;
    private CheckBox chkFriday;
    private CheckBox chkSaturday;
    private TextView txtToneSelection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_details);

        Log.i(TAG, "onCreate was Called");

        dbHelper = new AlarmDBHelper(this);

        timePicker = (TimePicker) findViewById(R.id.alarm_time);
        alarmName = (EditText) findViewById(R.id.alarm_name);
        chkWeekly = (CheckBox) findViewById(R.id.alarm_repeat_weekly);
        chkSunday = (CheckBox) findViewById(R.id.alarm_repeat_sunday);
        chkMonday = (CheckBox) findViewById(R.id.alarm_repeat_monday);
        chkTuesday = (CheckBox) findViewById(R.id.alarm_repeat_tuesday);
        chkWednesday = (CheckBox) findViewById(R.id.alarm_repeat_wednesday);
        chkThursday = (CheckBox) findViewById(R.id.alarm_repeat_thursday);
        chkFriday = (CheckBox) findViewById(R.id.alarm_repeat_friday);
        chkSaturday = (CheckBox) findViewById(R.id.alarm_repeat_saturday);
        txtToneSelection = (TextView) findViewById(R.id.alarm_label_tone_selection);

        long id = getIntent().getExtras().getLong("id");

        if (id == -1){
            alarm = new Alarm();
            txtToneSelection.setText(RingtoneManager.getRingtone(this,alarm.alarmTone).getTitle(this));
        }else {
            alarm = dbHelper.getAlarm(id);

            timePicker.setCurrentHour(alarm.timeHour);
            timePicker.setCurrentMinute(alarm.timeMinute);

            alarmName.setText(alarm.name);

            chkWeekly.setChecked(alarm.repeatWeekly);
            chkSunday.setChecked(alarm.getRepeatingDay(Alarm.SUNDAY));
            chkMonday.setChecked(alarm.getRepeatingDay(Alarm.MONDAY));
            chkTuesday.setChecked(alarm.getRepeatingDay(Alarm.TUESDAY));
            chkWednesday.setChecked(alarm.getRepeatingDay(Alarm.WEDNESDAY));
            chkThursday.setChecked(alarm.getRepeatingDay(Alarm.THURSDAY));
            chkFriday.setChecked(alarm.getRepeatingDay(Alarm.FRIDAY));
            chkSaturday.setChecked(alarm.getRepeatingDay(Alarm.SATURDAY));
            txtToneSelection.setText(RingtoneManager.getRingtone(this,alarm.alarmTone).getTitle(this));
        }


        final LinearLayout ringToneContainer = (LinearLayout) findViewById(R.id.alarm_ringtone_container);
        ringToneContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(RingtoneManager.ACTION_RINGTONE_PICKER),0);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            alarm.alarmTone = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            txtToneSelection.setText(RingtoneManager.getRingtone(this,alarm.alarmTone).getTitle(this));
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

        alarm.isEnabled = true;
        alarm.timeHour = timePicker.getCurrentHour();
        alarm.timeMinute = timePicker.getCurrentMinute();
        if (!String.valueOf(alarmName.getText()).equals("")){ alarm.name = String.valueOf(alarmName.getText()); }
        alarm.repeatWeekly = chkWeekly.isChecked();
        alarm.setRepeatingDays(Alarm.SUNDAY,chkSunday.isChecked());
        alarm.setRepeatingDays(Alarm.MONDAY,chkMonday.isChecked());
        alarm.setRepeatingDays(Alarm.TUESDAY,chkTuesday.isChecked());
        alarm.setRepeatingDays(Alarm.WEDNESDAY,chkWednesday.isChecked());
        alarm.setRepeatingDays(Alarm.THURSDAY,chkThursday.isChecked());
        alarm.setRepeatingDays(Alarm.FRIDAY,chkFriday.isChecked());
        alarm.setRepeatingDays(Alarm.SATURDAY,chkSaturday.isChecked());
    }
}
