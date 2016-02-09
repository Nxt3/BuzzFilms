package com.nullpointexecutioners.buzzfilms;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
            toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back));
            toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.primary_text_dark));
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
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
           makeSnackbar(v, "Incorrect login", Snackbar.LENGTH_LONG, getColor(R.color.accent), getColor(R.color.primary_text_light)).show();
        }
    }

    /*User doesn't want to login anymore*/
    public void onReturnClick(View v) {
        /*go back one screen; same thing as pressing the back button in Android*/
        onBackPressed();
    }

    /**
     * Helper method for creating custom Snackbars
     * @param layout view to place the Snackbar in
     * @param text what you want the Snackbar to say
     * @param duration how long you want the Snackbar to appear for
     * @param backgroundColor color you want the Snackbar's background to be
     * @param textColor color you want the Snackbar's text to be
     * @return the custom made Snackbar
     */
    @NonNull
    public static Snackbar makeSnackbar(@NonNull View layout, @NonNull CharSequence  text, int duration, int backgroundColor, int textColor/*, int actionTextColor*/){
        Snackbar snackBarView = Snackbar.make(layout, text, duration);
        snackBarView.getView().setBackgroundColor(backgroundColor);
        //snackBarView.setActionTextColor(actionTextColor);
        TextView tv = (TextView) snackBarView.getView().findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(textColor);
        return snackBarView;
    }
}