package com.nullpointexecutioners.buzzfilms;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        printUsers();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            // TODO: change the toolbar title color non-programmatically?
            toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.primary_text_dark));
        }
    }

    private void printUsers() {
        for (Map.Entry<String, User> entry : WelcomeActivity.accounts.entrySet()) {
            System.out.println(entry.getKey()+" : " + entry.getValue());
        }
        System.out.println("Size: " + WelcomeActivity.accounts.size());
    }

    public void onLogoutClick(View v) {
        Intent myIntent = new Intent(MainActivity.this, WelcomeActivity.class);
        startActivity(myIntent);
    }
}
