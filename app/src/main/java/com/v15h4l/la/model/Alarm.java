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
    public String name;
    public int timeHour;
    public int timeMinute;
    public boolean repeatingDays[];
    public boolean repeatWeekly;
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
