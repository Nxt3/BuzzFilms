package com.nullpointexecutioners.buzzfilms;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            // TODO: change the toolbar title color non-programmatically?
            toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));
        }

        Button loginButton = (Button) findViewById(R.id.btnLogin);
        Button registerButton = (Button) findViewById(R.id.btnRegister);

        // Capture button clicks
        loginButton.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                // Start LoginActivity.class
                Intent myIntent = new Intent(WelcomeActivity.this,
                        LoginActivity.class);
                startActivity(myIntent);
            }
        });
        registerButton.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
//                placeholder for register button--not yet...
                Snackbar.make(findViewById(android.R.id.content), "Not yet ;)", Snackbar.LENGTH_LONG).show();
            }
        });
    }
}
