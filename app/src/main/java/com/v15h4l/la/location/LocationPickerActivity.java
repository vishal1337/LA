package com.v15h4l.la.location;

import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.v15h4l.la.Prefs.AlarmPreference;
import com.v15h4l.la.R;

public class LocationPickerActivity extends ActionBarActivity {

    public GoogleMap mMap;
    Context mContext;
    LatLng myPosition;
    double locationLat;
    double locationLon;
    private static String locationName;
    Button btnSubmit;
    Location currentLocation;
    AlarmPreference prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_picker);

        mContext = this;

        prefs = new AlarmPreference(mContext);
        btnSubmit = (Button) findViewById(R.id.submit);

        //  buildGoogleApiClient();
        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();


        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, true);
        currentLocation = locationManager.getLastKnownLocation(bestProvider);

//        Toast.makeText(mContext,"Current Location"+currentLocation,Toast.LENGTH_SHORT).show();

        // map is a GoogleMap object
        mMap.setMyLocationEnabled(true);
        mMap.setBuildingsEnabled(true);
        if(currentLocation!=null)
        {
            myPosition = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

            // inside class, for a given lat/lon
                CameraPosition INIT =
                    new CameraPosition.Builder()
                            .target(myPosition)
                            .zoom(14)
                            .build();
            // use GoogleMap mMap to move camera into position
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(INIT));

        }

        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                // Get the center of the Map.
                locationLat = mMap.getCameraPosition().target.latitude;
                locationLon = mMap.getCameraPosition().target.longitude;

                LocationAddress.getAddressFromLocation(locationLat, locationLon,
                        mContext, new GeoCoderHandler());
                Location destinationLocation = new Location("destination");
                destinationLocation.setLatitude(locationLat);
                destinationLocation.setLongitude(locationLon);

 //               Toast.makeText(mContext, currentLocation.distanceTo(destinationLocation)+"",Toast.LENGTH_SHORT).show();

//                Toast.makeText(mContext, mMap.getCameraPosition().target + "", Toast.LENGTH_SHORT).show();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.putExtra("LocationLat",locationLat);
                intent.putExtra("LocationLon",locationLon);
                intent.putExtra("locationName",locationName);
                setResult(-1, intent);
                finish(); //finishing activity
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_location_picker, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private static class GeoCoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }
            locationName = locationAddress;
        }
    }
}
