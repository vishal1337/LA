package com.v15h4l.la.services;

import android.app.Service;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.util.Log;

import com.v15h4l.la.Prefs.AlarmPreference;
import com.v15h4l.la.alarm.AlarmManagerHelper;
import com.v15h4l.la.alarm.AlarmScreen;

/**
 * Created by v15h4l on 4/21/2015.
 */

public class AlarmService extends Service {

    AlarmPreference preference;

    private final String TAG = this.getClass().getSimpleName();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i(TAG, "onStartCommand was Called");

        if (intent != null) {
            preference = new AlarmPreference(getBaseContext());

            Location myLocation;
            Location destinationLocation;

            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            String bestProvider = locationManager.getBestProvider(criteria, true);
            myLocation = locationManager.getLastKnownLocation(bestProvider);

//        Log.v("Service test", "My Location "+myLocation.getLatitude()+","+myLocation.getLongitude());
//        Log.v("Service test", "Destination "+intent.getDoubleExtra(AlarmManagerHelper.DEST_LAT, 0)+","+intent.getDoubleExtra(AlarmManagerHelper.DEST_LON,0));
            destinationLocation = new Location("Destination");
            destinationLocation.setLatitude(intent.getDoubleExtra(AlarmManagerHelper.DEST_LAT, 0));
            destinationLocation.setLongitude(intent.getDoubleExtra(AlarmManagerHelper.DEST_LON, 0));

            Intent alarmIntent = new Intent(getBaseContext(), AlarmScreen.class);
            alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            alarmIntent.putExtras(intent);

            if (destinationLocation.getLongitude() != 0 && destinationLocation.getLatitude() != 0) {
                if (isWithinArea(myLocation, destinationLocation)) {
                    Log.v("Alarm Service", "Inside");
                    getApplication().startActivity(alarmIntent);
                } else {
                    Log.v("Alarm Service", "Outside");
                }
            } else {
                Log.v("Alarm Service", "Offline Alarm");
                getApplication().startActivity(alarmIntent);
            }
        }

        AlarmManagerHelper.setAlarms(this);
        
        return super.onStartCommand(intent, flags, startId);
    }

    private Boolean isWithinArea(Location myLocation, Location destinationLocation){
        Log.v("Service test","Distance: "+myLocation.distanceTo(destinationLocation)+", Radius: "+preference.getRadius());

        if (myLocation.distanceTo(destinationLocation) <= preference.getRadius()){
            return true;
        }
        return false;
    }
}
