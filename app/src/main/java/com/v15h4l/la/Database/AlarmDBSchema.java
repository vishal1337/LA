package com.v15h4l.la.Database;

import android.provider.BaseColumns;

/**
 * Created by v15h4l on 4/20/2015.
 */
public class AlarmDBSchema{

    public AlarmDBSchema(){}

    public static abstract class AlarmDB implements BaseColumns{

        public static final String TABLE_NAME = "alarms";
        public static final String COLUMN_NAME_ALARM_NAME = "name";
        public static final String COLUMN_NAME_ALARM_IS_ENABLED = "isEnabled";
        public static final String COLUMN_NAME_ALARM_TIME_HOUR = "timeHour";
        public static final String COLUMN_NAME_ALARM_TIME_MINUTE = "timeMinute";
        public static final String COLUMN_NAME_ALARM_REPEAT_DAYS = "repeatDays";
        public static final String COLUMN_NAME_ALARM_REPEAT_WEEKLY = "repeatWeekly";
        public static final String COLUMN_NAME_ALARM_TONE = "alarmTone";

    }
}
