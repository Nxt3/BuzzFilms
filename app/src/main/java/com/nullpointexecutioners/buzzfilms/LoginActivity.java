package com.nullpointexecutioners.buzzfilms;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            // TODO: change the toolbar title color non-programmatically?
            toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));
        }

//        Lets the user press the "Enter" button on the keyboard instead of having to press the "Login" button
        final EditText loginPassword = (EditText) findViewById(R.id.login_password);
        loginPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    onLoginClick(v);
//                    Hide virtual keyboard after "Done" action
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(loginPassword.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    return true;
                }
                return false;
            }
        });
    }

    public void onLoginClick(View v) {
        EditText usernameBox =  (EditText) findViewById(R.id.login_username);
        EditText passwordBox = (EditText) findViewById(R.id.login_password);
        String username = usernameBox.getText().toString();
        String password = passwordBox.getText().toString();

        if (username.equals("user") && password.equals("pass")) {
            // Login works - proceed to application
            Intent loginIntent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(loginIntent);
        } else {
            Snackbar.make(findViewById(android.R.id.content), "Incorrect login", Snackbar.LENGTH_LONG).show();
        }
    }

    public void onReturnClick(View v) {
        Intent loginIntent = new Intent(LoginActivity.this, WelcomeActivity.class);
        startActivity(loginIntent);
    }
}
