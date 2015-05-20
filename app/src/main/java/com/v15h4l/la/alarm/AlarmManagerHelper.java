package com.v15h4l.la.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.v15h4l.la.Database.AlarmDBHelper;
import com.v15h4l.la.model.Alarm;
import com.v15h4l.la.services.AlarmService;

import java.util.Calendar;
import java.util.List;

/**
 * Created by v15h4l on 4/21/2015.
 */

public class AlarmManagerHelper extends BroadcastReceiver {

    private static final String TAG = AlarmManagerHelper.class.getSimpleName();

    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String TIME_HOUR = "timeHour";
    public static final String TIME_MINUTE = "timeMinute";
    public static final String DEST_LAT= "destLat";
    public static final String DEST_LON = "destLon";
    public static final String TONE = "alarmTone";

    @Override
    public void onReceive(Context context, Intent intent) {
     //   setAlarms(context);
    }

    public static void setAlarms(Context context){

        Log.i(TAG, "setAlarms was Called");

        cancelAlarms(context);

        AlarmDBHelper dbHelper = new AlarmDBHelper(context);

        List<Alarm> alarms = dbHelper.getAlarms();

        //Logic still needs some Rework but Works
        if (alarms != null){
            for (Alarm alarm : alarms){
                if (alarm.isEnabled){
                    PendingIntent pendingIntent = createPendingIntent(context,alarm);

                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY, alarm.timeHour);
                    calendar.set(Calendar.MINUTE, alarm.timeMinute);
                    calendar.set(Calendar.SECOND, 0);

                    //Find next time to set
                    final int nowDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
                    final int nowHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                    final int nowMinute = Calendar.getInstance().get(Calendar.MINUTE);

                    boolean alarmSet = false;

                    //First check if it's later in the week
                    for (int dayOfWeek = Calendar.SUNDAY; dayOfWeek <= Calendar.SATURDAY; dayOfWeek++) {
                        if (alarm.getRepeatingDay(dayOfWeek - 1) && dayOfWeek >= nowDay &&
                                !(dayOfWeek == nowDay && alarm.timeHour < nowHour) &&
                                !(dayOfWeek == nowDay && alarm.timeHour == nowHour && alarm.timeMinute <= nowMinute)) {
                            calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);

                            Log.i(TAG,"Alarm will Occur later This Week, Repeat Alarm: "+alarm.repeatWeekly);

                            setAlarm(context, calendar, pendingIntent);
                            alarmSet = true;
                            break;
                        }
                    }

                    //Else check if it's earlier in the week [Buggy]
                    if (!alarmSet) {
                        for (int dayOfWeek = Calendar.SUNDAY; dayOfWeek <= Calendar.SATURDAY; dayOfWeek++) {
                            if (alarm.getRepeatingDay(dayOfWeek - 1) && dayOfWeek <= nowDay && alarm.repeatWeekly) {
                                calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
                                calendar.add(Calendar.WEEK_OF_YEAR,1);
                                Log.i(TAG, "Alarm will not Occur later This Week, Repeat Alarm: "+alarm.repeatWeekly);
                                setAlarm(context, calendar, pendingIntent);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    public static void cancelAlarms(Context context){

        Log.i(TAG,"cancelAlarms was Called");

        AlarmDBHelper dbHelper = new AlarmDBHelper(context);

        List<Alarm> alarms = dbHelper.getAlarms();

        if (alarms != null){
            for (Alarm alarm : alarms){
                if (alarm.isEnabled){
                    PendingIntent pendingIntent = createPendingIntent(context, alarm);
                    cancelAlarm(context,pendingIntent);
                }
            }
        }
    }

    private static void setAlarm(Context context, Calendar calendar, PendingIntent pendingIntent){

        Log.i(TAG,"setAlarm was Called");

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
        Log.i(TAG, "Alarm Will Go Off on " + calendar.getTime());
    }

    private static void cancelAlarm(Context context, PendingIntent pendingIntent){

        Log.i(TAG, "cancelAlarm was Called");

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    private static PendingIntent createPendingIntent(Context context, Alarm alarm){

        Log.i(TAG, "createPendingIntent was Called");

        Intent intent = new Intent(context,AlarmService.class);
        intent.putExtra(ID,alarm.id);
        intent.putExtra(NAME,alarm.name);
        intent.putExtra(TIME_HOUR,alarm.timeHour);
        intent.putExtra(TIME_MINUTE,alarm.timeMinute);
        intent.putExtra(DEST_LAT,Double.valueOf(alarm.location_lat));
        intent.putExtra(DEST_LON,Double.valueOf(alarm.location_lon));
        intent.putExtra(TONE, String.valueOf(alarm.alarmTone));

        return PendingIntent.getService(context,(int) alarm.id,intent,PendingIntent.FLAG_UPDATE_CURRENT);
    }

}
