package com.nullpointexecutioners.buzzfilms;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.view.View;
import android.widget.Toast;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.content.Intent;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        getWindow().setStatusBarColor(getColor(R.color.primary_dark));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // TODO: change the toolbar title color non-programmatically?
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));

        Button loginButton = (Button) findViewById(R.id.btnLogin);

        // Capture button clicks
        loginButton.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {

                // Start LoginActivity.class
                Intent myIntent = new Intent(WelcomeActivity.this,
                        LoginActivity.class);
                startActivity(myIntent);
            }
        });
    }
}
