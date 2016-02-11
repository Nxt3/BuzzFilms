package com.nullpointexecutioners.buzzfilms;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class WelcomeActivity extends AppCompatActivity {

    public static Map<String, User> accounts = new HashMap<String, User>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        final Button loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setEnabled(false); //disabled by default
        final EditText loginUsernameInput = (EditText) findViewById(R.id.login_username);
        final EditText loginPasswordInput = (EditText) findViewById(R.id.login_password);

        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (loginUsernameInput.getText().length() > 0
                        && loginPasswordInput.getText().length() > 0) {
                    loginButton.setEnabled(true);
                } else {
                    loginButton.setEnabled(false);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        };
        loginUsernameInput.addTextChangedListener(watcher);
        loginPasswordInput.addTextChangedListener(watcher);

        /*We want to also allow the user to press the Done button on the keyboard*/
        loginPasswordInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    authenticateLogin();
//                    Hide virtual keyboard after "Done" action
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(loginPasswordInput.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    return true;
                }
                return false;
            }
        });
        ButterKnife.bind(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.login_button)
    public void authenticateLogin() {
        final EditText loginUsernameInput = (EditText) findViewById(R.id.login_username);
        final EditText loginPasswordInput = (EditText) findViewById(R.id.login_password);
        String username = loginUsernameInput.getText().toString();
        String password = loginPasswordInput.getText().toString();

        /*Check and see if the Login fields are blank*/
        boolean emptyFields = true;
        if (username.length() != 0 && password.length() != 0) {
            emptyFields = false;
        }
        if (username.equals("user") && password.equals("pass") && !emptyFields) {
            // Login works - proceed to application
            Intent loginIntent = new Intent(WelcomeActivity.this, MainActivity.class);
            startActivity(loginIntent);
        } else {
            makeSnackbar(findViewById(android.R.id.content), getString(R.string.invalid_login), Snackbar.LENGTH_LONG,
                    getColor(R.color.accent), getColor(R.color.primary_text_light)).show();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(loginPasswordInput.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public void registerUser(String name, String email, String username, String password) {
        accounts.put(username, new User(username, password, name, email));
    }

    @OnClick(R.id.register_button)
    public void showRegisterDialog() {
        final MaterialDialog registerDialog = new MaterialDialog.Builder(WelcomeActivity.this)
                .title(getString(R.string.register_dialog_title))
                .customView(R.layout.register_dialog, true)
                .theme(Theme.DARK)
                .positiveText(getString(R.string.register))
                .negativeText(getString(R.string.cancel))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull final MaterialDialog registerDialog, @NonNull DialogAction which) {
                        final EditText registerNameInput;
                        final EditText registerEmailInput;
                        final EditText registerUsernameInput;
                        final EditText registerPasswordInput;
                        String name = "", email = "", username = "", password = "";
                        if (registerDialog.getCustomView() != null) {
                            registerNameInput = (EditText) registerDialog.getCustomView().findViewById(R.id.register_name);
                            registerEmailInput = (EditText) registerDialog.getCustomView().findViewById(R.id.register_email);
                            registerUsernameInput = (EditText) registerDialog.getCustomView().findViewById(R.id.register_username);
                            registerPasswordInput = (EditText) registerDialog.getCustomView().findViewById(R.id.register_password);
                            name = registerNameInput.getText().toString();
                            email = registerEmailInput.getText().toString();
                            username = registerUsernameInput.getText().toString();
                            password = registerPasswordInput.getText().toString();
                        }
                        /*Check and see if the Login fields are blank*/
                        boolean emptyFields = true;
                        if (name.length() != 0 && email.length() != 0 && username.length() != 0
                                && password.length() != 0) {
                            emptyFields = false;
                        }
                        //TODO: search the database for an existing user; if no match is found, allow this user to be added to the DB

                        if (!emptyFields) {
                            if (!accounts.containsKey(username)) {
                                // Call the method to actually register the user after all checks
                                registerUser(name, email, username, password);
                                printUsers();
                                // Proceed to application
                                Intent registerIntent = new Intent(WelcomeActivity.this, MainActivity.class);
                                startActivity(registerIntent);
                            }
                        } else {
                            //Inform the user that the email address or username is already in use
                            makeSnackbar(findViewById(android.R.id.content), "Username already exists", Snackbar.LENGTH_LONG,
                                    getColor(R.color.accent), getColor(R.color.primary_text_light)).show();
                            //TODO, invalidate the input field that holds the pre-existing info.
                        }

                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog registerDialog, @NonNull DialogAction which) {
                        registerDialog.dismiss(); //close the registerDialog since they pressed "Cancel"
                    }
                }).build();

        final View registerAction = registerDialog.getActionButton(DialogAction.POSITIVE);
        final EditText registerNameInput;
        final EditText registerEmailInput;
        final EditText registerUsernameInput;
        final EditText registerPasswordInput;
        if (registerDialog.getCustomView() != null) {
            registerNameInput = (EditText) registerDialog.getCustomView().findViewById(R.id.register_name);
            registerEmailInput = (EditText) registerDialog.getCustomView().findViewById(R.id.register_email);
            registerUsernameInput = (EditText) registerDialog.getCustomView().findViewById(R.id.register_username);
            registerPasswordInput = (EditText) registerDialog.getCustomView().findViewById(R.id.register_password);

            /*
             * TextWatcher lets us monitor the input fields while registering;
             * This make sure we don't allow the user to register with empty fields
             */
            TextWatcher watcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (registerNameInput.getText().toString().length() > 0
                            && registerEmailInput.getText().toString().length() > 0
                            && registerUsernameInput.getText().toString().length() > 0
                            && registerPasswordInput.getText().toString().length() > 0) {
                        registerAction.setEnabled(true);
                    } else {
                        //If we delete text after we've already filled everything in--we need to disable the button again
                        registerAction.setEnabled(false);
                    }
                }
                @Override
                public void afterTextChanged(Editable s) {
                }
            };
            /*We want to watch all EditText fields for input*/
            registerNameInput.addTextChangedListener(watcher);
            registerEmailInput.addTextChangedListener(watcher);
            registerUsernameInput.addTextChangedListener(watcher);
            registerPasswordInput.addTextChangedListener(watcher);
        }
        registerDialog.show();
        registerAction.setEnabled(false); //disabled by default
    }

    private void printUsers() {
        for (Map.Entry<String, User> entry : accounts.entrySet()) {
            System.out.println(entry.getKey()+" : " + entry.getValue());
        }
        System.out.println("Size: " + accounts.size());
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
    public static Snackbar makeSnackbar(@NonNull View layout, @NonNull CharSequence  text, int duration, int backgroundColor, int textColor/*, int actionTextColor*/) {
        Snackbar snackBarView = Snackbar.make(layout, text, duration);
        snackBarView.getView().setBackgroundColor(backgroundColor);
        //snackBarView.setActionTextColor(actionTextColor);
        TextView tv = (TextView) snackBarView.getView().findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(textColor);
        return snackBarView;
    }
}
