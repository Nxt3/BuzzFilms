package com.nullpointexecutioners.buzzfilms;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            // TODO: change the toolbar title color non-programmatically?
            toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));
        }
    }

    public void onLogoutClick(View v) {
        Intent myIntent = new Intent(MainActivity.this, WelcomeActivity.class);
        startActivity(myIntent);
    }
}
