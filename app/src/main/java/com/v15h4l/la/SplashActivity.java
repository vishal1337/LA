package com.v15h4l.la;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;


public class SplashActivity extends Activity {

    ImageView splashImage;

    long DELAY=1800;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Remove ActionBar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Remove NotificationBar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Set Splash Layout
        setContentView(R.layout.activity_splash);

        //Timer
        Timer RunSplash = new Timer();

        //Task to do when the Timer Ends
        TimerTask ShowSplash = new TimerTask() {
            @Override
            public void run() {
                //Close SplashActivity.class
                finish();
                Intent myIntent = new Intent(SplashActivity.this,MainActivity.class);
                startActivity(myIntent);
            }
        };

        //Start the Timer
        RunSplash.schedule(ShowSplash,DELAY);
    }

}
