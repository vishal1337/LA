package com.v15h4l.la.model;

import android.media.RingtoneManager;
import android.net.Uri;

/**
 * Created by v15h4l on 4/20/2015.
 */

public class Alarm {

    public static final int SUNDAY = 0;
    public static final int MONDAY = 1;
    public static final int TUESDAY = 2;
    public static final int WEDNESDAY = 3;
    public static final int THURSDAY = 4;
    public static final int FRIDAY = 5;
    public static final int SATURDAY = 6;

    public long id = -1;
    public boolean isEnabled;
    public int timeHour;
    public int timeMinute;
    public String name="";
    public String recurrence="";
    public String recurrenceMessage="" ;
    public String location_name="";
    public String location_lat = "0";
    public String location_lon = "0";
    public String description="";
    public boolean repeatingDays[];
    public boolean repeatWeekly = true;
    public Uri alarmTone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

    public Alarm(){
        repeatingDays = new boolean[7];
    }

    public void setRepeatingDays(int dayOfWeek,boolean isEnabled){
        repeatingDays[dayOfWeek] = isEnabled;
    }

    public boolean getRepeatingDay(int dayOfWeek){
        return repeatingDays[dayOfWeek];
    }
}
