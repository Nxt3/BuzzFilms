package com.nullpointexecutioners.buzzfilms;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

/**
 * Splash screen for our app gives the appearance of speeeeeeeed
 */
public class SplashActivity extends Activity {

    @Override
    /**
     * Creates this activity
     * @param savedInstanceState
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        Thread timerThread = new Thread() {
            public void run() {
                try {
                    sleep(2000); //wait for x number of milliseconds
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    Intent intent = new Intent(SplashActivity.this, WelcomeActivity.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            }
        };
        timerThread.start();
    }

    /**
     * Handles the activity once it is paused (i.e. in the background)
     */
    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
