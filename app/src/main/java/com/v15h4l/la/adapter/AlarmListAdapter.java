package com.v15h4l.la.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.v15h4l.la.Database.AlarmDBHelper;
import com.v15h4l.la.Fragments.HomeFragment;
import com.v15h4l.la.R;
import com.v15h4l.la.model.Alarm;

import java.util.List;

public class AlarmListAdapter extends BaseAdapter {

    private final String TAG = AlarmListAdapter.class.getSimpleName();

    AlarmDBHelper dbHelper;
    List<Alarm> mAlarms;
    Context mContext;
    HomeFragment fragment;

    public AlarmListAdapter(Context context, List<Alarm> alarms,HomeFragment fragment){

        Log.i(TAG, "AlarmListAdapter constructor was called");

        this.fragment = fragment;
        dbHelper = new AlarmDBHelper(context);
        mAlarms = alarms;
        mContext = context;
    }

    public void setAlarms(List<Alarm> alarms){

        Log.i(TAG,"setAlarms was Called");

        mAlarms = alarms;
    }

    @Override
    public int getCount() {
        if (mAlarms != null){
            return mAlarms.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (mAlarms != null){
            return mAlarms.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        if (mAlarms != null){
            return mAlarms.get(position).id;
        }
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.alarm_list_item,parent,false);
        }

        final Alarm alarm = (Alarm) getItem(position);

        if (!alarm.location_lon.equals("0") && !alarm.location_lat.equals("0")) {
            ((ImageView) convertView.findViewById(R.id.img_alarm_type)).setImageResource(R.drawable.img_alarm_type_location);
        }else{
            ((ImageView) convertView.findViewById(R.id.img_alarm_type)).setImageResource(R.drawable.img_alarm_type_normal);
        }
        ((TextView) convertView.findViewById(R.id.alarm_item_time)).setText(String.format("%02d:%02d",alarm.timeHour,alarm.timeMinute));
        if(!alarm.name.isEmpty()){ ((TextView) convertView.findViewById(R.id.alarm_item_name)).setText(alarm.name); }
        updateTextColor((TextView) convertView.findViewById(R.id.alarm_item_sunday), alarm.getRepeatingDay(Alarm.SUNDAY));
        updateTextColor((TextView) convertView.findViewById(R.id.alarm_item_monday), alarm.getRepeatingDay(Alarm.MONDAY));
        updateTextColor((TextView) convertView.findViewById(R.id.alarm_item_tuesday), alarm.getRepeatingDay(Alarm.TUESDAY));
        updateTextColor((TextView) convertView.findViewById(R.id.alarm_item_wednesday), alarm.getRepeatingDay(Alarm.WEDNESDAY));
        updateTextColor((TextView) convertView.findViewById(R.id.alarm_item_thursday), alarm.getRepeatingDay(Alarm.THURSDAY));
        updateTextColor((TextView) convertView.findViewById(R.id.alarm_item_friday), alarm.getRepeatingDay(Alarm.FRIDAY));
        updateTextColor((TextView) convertView.findViewById(R.id.alarm_item_saturday), alarm.getRepeatingDay(Alarm.SATURDAY));

        final ToggleButton toggleButton = ((ToggleButton) convertView.findViewById(R.id.alarm_item_toggle));

        toggleButton.setChecked(alarm.isEnabled);

        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment.setAlarmEnabled(alarm.id,!alarm.isEnabled);
            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment.startAlarmViewActivity(alarm.id);
            }
        });

        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                fragment.deleteAlarm(alarm.id);
                return true;
            }
        });

        return convertView;

    }

    public void updateTextColor(TextView textView, Boolean isOn){
        if (isOn){
            textView.setTextColor(mContext.getResources().getColor(R.color.time_light));
        }else {
            textView.setTextColor(mContext.getResources().getColor(R.color.purple_100));
        }
    }
}
