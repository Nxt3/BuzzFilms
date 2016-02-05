package com.nullpointexecutioners.buzzfilms;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
<<<<<<< Updated upstream
        // TODO: change the toolbar title color non-programmatically?
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));
=======
>>>>>>> Stashed changes
    }




}
