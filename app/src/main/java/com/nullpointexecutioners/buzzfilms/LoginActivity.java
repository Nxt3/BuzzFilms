package com.nullpointexecutioners.buzzfilms;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getWindow().setStatusBarColor(getColor(R.color.primary_dark));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(R.string.title_activity_login);
            // TODO: change the toolbar title color non-programmatically?
            toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));
        }
    }

    public void onLoginClick(View v) {
        EditText usernameBox =  (EditText)findViewById(R.id.login_username);
        EditText passwordBox = (EditText)findViewById(R.id.login_password);
        String username = usernameBox.getText().toString();
        String password = passwordBox.getText().toString();

        if (username.equals("user") && password.equals("pass")) {
            // Login works - proceed to application
            Snackbar.make(findViewById(android.R.id.content), "Yay", Snackbar.LENGTH_LONG).show();
        } else {
            Snackbar.make(findViewById(android.R.id.content), "Incorrect login information", Snackbar.LENGTH_LONG).show();
        }
    }
}
