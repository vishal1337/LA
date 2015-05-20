package com.v15h4l.la.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

import com.v15h4l.la.Database.AlarmDBSchema.AlarmDB;
import com.v15h4l.la.model.Alarm;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by v15h4l on 4/20/2015.
 */
public class AlarmDBHelper extends SQLiteOpenHelper {

    public static final String TAG = AlarmDBHelper.class.getSimpleName();

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Alarm.db";

    private static final String SQL_CREATE_ALARM_TABLE = "CREATE TABLE " + AlarmDB.TABLE_NAME + " (" +
            AlarmDB._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            AlarmDB.COLUMN_NAME_ALARM_NAME + " TEXT," +
            AlarmDB.COLUMN_NAME_ALARM_IS_ENABLED + " BOOLEAN," +
            AlarmDB.COLUMN_NAME_ALARM_TIME_HOUR + " INTEGER," +
            AlarmDB.COLUMN_NAME_ALARM_TIME_MINUTE + " INTEGER," +
            AlarmDB.COLUMN_NAME_ALARM_RECURRENCE + " TEXT," +
            AlarmDB.COLUMN_NAME_ALARM_RECURRENCE_MESSAGE + " TEXT," +
            AlarmDB.COLUMN_NAME_ALARM_LOCATION_NAME + " TEXT," +
            AlarmDB.COLUMN_NAME_ALARM_LOCATION_LAT + " TEXT," +
            AlarmDB.COLUMN_NAME_ALARM_LOCATION_LON + " TEXT," +
            AlarmDB.COLUMN_NAME_ALARM_DESCRIPTION + " TEXT," +
            AlarmDB.COLUMN_NAME_ALARM_TONE + " TEXT"+
    ")";

    private static final String SQL_DELETE_ALARM_TABLE = "DROP TABLE IF EXISTS "+AlarmDB.TABLE_NAME;

    public AlarmDBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ALARM_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ALARM_TABLE);
        onCreate(db);
    }

    /**
     * method used to convert received cursor values into Alarm Object
     */
    private Alarm populateModel(Cursor cursor){

        Log.i(TAG,"populateModel was Called");

        Alarm alarm = new Alarm();

        alarm.id = cursor.getLong(cursor.getColumnIndex(AlarmDB._ID));
        alarm.isEnabled = cursor.getInt(cursor.getColumnIndex(AlarmDB.COLUMN_NAME_ALARM_IS_ENABLED)) != 0;
        alarm.timeHour = cursor.getInt(cursor.getColumnIndex(AlarmDB.COLUMN_NAME_ALARM_TIME_HOUR));
        alarm.timeMinute = cursor.getInt(cursor.getColumnIndex(AlarmDB.COLUMN_NAME_ALARM_TIME_MINUTE));
        alarm.name = cursor.getString(cursor.getColumnIndex(AlarmDB.COLUMN_NAME_ALARM_NAME));
        alarm.recurrence = cursor.getString(cursor.getColumnIndex(AlarmDB.COLUMN_NAME_ALARM_RECURRENCE));
        alarm.recurrenceMessage = cursor.getString(cursor.getColumnIndex(AlarmDB.COLUMN_NAME_ALARM_RECURRENCE_MESSAGE));
        alarm.location_name = cursor.getString(cursor.getColumnIndex(AlarmDB.COLUMN_NAME_ALARM_LOCATION_NAME));
        alarm.location_lat = cursor.getString(cursor.getColumnIndex(AlarmDB.COLUMN_NAME_ALARM_LOCATION_LAT));
        alarm.location_lon = cursor.getString(cursor.getColumnIndex(AlarmDB.COLUMN_NAME_ALARM_LOCATION_LON));
        alarm.description = cursor.getString(cursor.getColumnIndex(AlarmDB.COLUMN_NAME_ALARM_DESCRIPTION));
        alarm.alarmTone = Uri.parse(cursor.getString(cursor.getColumnIndex(AlarmDB.COLUMN_NAME_ALARM_TONE)));

        String recurrence = cursor.getString(cursor.getColumnIndex(AlarmDB.COLUMN_NAME_ALARM_RECURRENCE));

        setRecurrence(recurrence,alarm);
        return alarm;
    }

    /**
     * method used to convert received alarm values into ContentValues
     * @param alarm
     * @return ContentValues
     */
    private ContentValues populateContent(Alarm alarm){

        Log.i(TAG, "populateContent was Called");

        ContentValues contentValues = new ContentValues();

        contentValues.put(AlarmDB.COLUMN_NAME_ALARM_IS_ENABLED,alarm.isEnabled);
        contentValues.put(AlarmDB.COLUMN_NAME_ALARM_NAME,alarm.name);
        contentValues.put(AlarmDB.COLUMN_NAME_ALARM_TIME_HOUR, alarm.timeHour);
        contentValues.put(AlarmDB.COLUMN_NAME_ALARM_TIME_MINUTE,alarm.timeMinute);
        contentValues.put(AlarmDB.COLUMN_NAME_ALARM_RECURRENCE,alarm.recurrence);
        contentValues.put(AlarmDB.COLUMN_NAME_ALARM_RECURRENCE_MESSAGE,alarm.recurrenceMessage);
        contentValues.put(AlarmDB.COLUMN_NAME_ALARM_LOCATION_NAME,alarm.location_name);
        contentValues.put(AlarmDB.COLUMN_NAME_ALARM_LOCATION_LAT,alarm.location_lat);
        contentValues.put(AlarmDB.COLUMN_NAME_ALARM_LOCATION_LON,alarm.location_lon);
        contentValues.put(AlarmDB.COLUMN_NAME_ALARM_DESCRIPTION,alarm.description);
        contentValues.put(AlarmDB.COLUMN_NAME_ALARM_TONE, String.valueOf(alarm.alarmTone));

        return contentValues;
    }

    public Long createAlarm(Alarm alarm){

        Log.i(TAG, "createAlarm was Called");

        //Insert alarm data into Database
        return getWritableDatabase().insert(AlarmDB.TABLE_NAME,null,populateContent(alarm));
    }

    public Alarm getAlarm(Long id){

        Log.i(TAG,"getAlarm was Called");

        String SQL_SELECT_QUERY = "SELECT * FROM " + AlarmDB.TABLE_NAME + " WHERE " + AlarmDB._ID +" = " + id;

        Cursor cursor = getReadableDatabase().rawQuery(SQL_SELECT_QUERY,null);

        //Check if Alarm is present in Database
        if (cursor.moveToNext()) {
            return populateModel(cursor);
        }
        return null;
    }

    public int updateAlarm(Alarm alarm){

        Log.i(TAG,"updateAlarm was Called");

        //Update alarm data
        return getWritableDatabase().update(AlarmDB.TABLE_NAME, populateContent(alarm), AlarmDB._ID + " =?", new String[] { String.valueOf(alarm.id) });
    }

    public int deleteAlarm(Long id){

        Log.i(TAG,"deleteAlarm was Called");

        //Delete Alarm
        return getWritableDatabase().delete(AlarmDB.TABLE_NAME, AlarmDB._ID + " =?", new String[]{String.valueOf(id)});
    }

    public List<Alarm> getAlarms(){

        Log.i(TAG,"getAlarms was Called");

        String SQL_SELECT_QUERY = "SELECT * FROM " + AlarmDB.TABLE_NAME;

        Cursor cursor = getReadableDatabase().rawQuery(SQL_SELECT_QUERY, null);

        List<Alarm> alarmList = new ArrayList<>();

        while (cursor.moveToNext()){
            alarmList.add(populateModel(cursor));
        }

        if (!alarmList.isEmpty()){
            return alarmList;
        }

        return null;
    }

    public void setRecurrence(String recurrence,Alarm alarm) {
        String FREQ = "";
        String WKST;
        String BYDAY = "";

        if(recurrence != null) {
            String[] category = recurrence.toUpperCase().split(";");
            int categoryLength = category.length;

            for (int i = 0; i < categoryLength; i++) {
                String[] tmp = null;
                tmp = category[i].split("=");
                switch (tmp[0]) {

                    case "FREQ":
                        FREQ = tmp[1];
                        break;

                    case "WKST":
                        WKST = tmp[1];
                        break;

                    case "BYDAY":
                        BYDAY = tmp[1];
                        break;
                }
            }
        }
        if (FREQ.equals("WEEKLY")) {
            alarm.repeatWeekly = true;
        }else {
            alarm.repeatWeekly = false;
        }

        String[] weekDays = BYDAY.split(",");

        int cntr=0;

        for (int i = 0; i < weekDays.length; i++) {
            switch (weekDays[i]) {
                case "SU":
                    alarm.setRepeatingDays(0, true);
                    cntr++;
                    break;

                case "MO":
                    alarm.setRepeatingDays(1, true);
                    cntr++;
                    break;

                case "TU":
                    alarm.setRepeatingDays(2, true);
                    cntr++;
                    break;

                case "WE":
                    alarm.setRepeatingDays(3, true);
                    cntr++;
                    break;

                case "TH":
                    alarm.setRepeatingDays(4, true);
                    cntr++;
                    break;

                case "FR":
                    alarm.setRepeatingDays(5, true);
                    cntr++;
                    break;

                case "SA":
                    alarm.setRepeatingDays(6, true);
                    cntr++;
                    break;
            }
        }
        if (cntr == 0){
            alarm.isEnabled = false;
        }
    }
}
