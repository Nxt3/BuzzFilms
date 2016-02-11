package com.nullpointexecutioners.buzzfilms;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);

        /*Handle the Register button*/
        findViewById(R.id.register_button).setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
//                placeholder for register button--not yet...
                Snackbar.make(findViewById(android.R.id.content), "Not yet ;)", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.btnLogin)
    public void showLoginDialog() {
        MaterialDialog loginDialog = new MaterialDialog.Builder(WelcomeActivity.this)
                .title(getString(R.string.login_dialog_title))
                .customView(R.layout.login_dialog, true)
                .theme(Theme.DARK)
                .positiveText(getString(R.string.title_activity_login))
                .negativeText(getString(R.string.cancel))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull final MaterialDialog loginDialog, @NonNull DialogAction which) {
                        final EditText loginUsernameInput;
                        final EditText loginPasswordInput;
                        String username = "", password = "";
                        if (loginDialog.getCustomView() != null) {
                            loginUsernameInput =  (EditText) loginDialog.getCustomView().findViewById(R.id.login_username);
                            loginPasswordInput = (EditText) loginDialog.getCustomView().findViewById(R.id.login_password);
                            username = loginUsernameInput.getText().toString();
                            password = loginPasswordInput.getText().toString();
                        }
                        /*Check and see if the Login fields are blank*/
                        boolean emptyFields = true;
                        if (username.length() != 0 && password.length() != 0) {
                            emptyFields = false;
                        }
                        if (username.equals("user") && password.equals("pass") && !emptyFields) {
                            // Login works - proceed to MainActivity
                            Intent loginIntent = new Intent(WelcomeActivity.this, MainActivity.class);
                            startActivity(loginIntent);
                        } else {
                            loginDialog.dismiss(); //Dismiss the login dialog to show the failure dialog
                            new MaterialDialog.Builder(WelcomeActivity.this)
                                    .title(R.string.login_failed_title)
                                    .content(R.string.login_failed_content)
                                    .positiveText(R.string.ok)
                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            loginDialog.show();
                                        }
                                    }).show();
                        }
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss(); //close the loginDialog since they pressed "Cancel"
                    }
                }).build();

        final View loginAction = loginDialog.getActionButton(DialogAction.POSITIVE);
        final EditText loginUsernameInput;
        final EditText loginPasswordInput;
        if (loginDialog.getCustomView() != null) {
            loginUsernameInput = (EditText) loginDialog.getCustomView().findViewById(R.id.login_username);
            loginPasswordInput = (EditText) loginDialog.getCustomView().findViewById(R.id.login_password);

            /*TextWatcher lets us monitor the Username and Password fields for input*/
            TextWatcher watcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    loginAction.setEnabled(s.toString().trim().length() > 0);
                }
                @Override
                public void afterTextChanged(Editable s) {
                }
            };
            /*We want to watch both EditText fields for input*/
            loginUsernameInput.addTextChangedListener(watcher);
            loginPasswordInput.addTextChangedListener(watcher);
        }
        loginDialog.show();
        loginAction.setEnabled(false); //disabled by default
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
