package com.example.jkaur3589.plantid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;


public class SplashScreen extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGTH = 1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                Intent mainIntent = new Intent(SplashScreen.this, MainActivity.class);
                SplashScreen.this.startActivity(mainIntent);
                SplashScreen.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }




    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        finish();
    }


}
