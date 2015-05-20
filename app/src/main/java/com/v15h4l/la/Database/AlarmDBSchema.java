package com.v15h4l.la.Database;

import android.provider.BaseColumns;

/**
 * Created by v15h4l on 4/20/2015.
 */
public class AlarmDBSchema{

    public AlarmDBSchema(){}

    public static abstract class AlarmDB implements BaseColumns{

        public static final String TABLE_NAME = "alarms";
        public static final String COLUMN_NAME_ALARM_IS_ENABLED = "isEnabled";
        public static final String COLUMN_NAME_ALARM_TIME_HOUR = "timeHour";
        public static final String COLUMN_NAME_ALARM_TIME_MINUTE = "timeMinute";
        public static final String COLUMN_NAME_ALARM_NAME = "name";
        public static final String COLUMN_NAME_ALARM_RECURRENCE = "recurrence";
        public static final String COLUMN_NAME_ALARM_RECURRENCE_MESSAGE = "recurrenceMessage";
        public static final String COLUMN_NAME_ALARM_LOCATION_NAME = "locationName";
        public static final String COLUMN_NAME_ALARM_LOCATION_LAT = "locationLat";
        public static final String COLUMN_NAME_ALARM_LOCATION_LON = "locationLon";
        public static final String COLUMN_NAME_ALARM_DESCRIPTION = "description";
        public static final String COLUMN_NAME_ALARM_TONE = "alarmTone";

    }
}