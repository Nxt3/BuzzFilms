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
import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WelcomeActivity extends AppCompatActivity {

    @Bind(R.id.login_button) Button mLoginButton;
    @Bind(R.id.login_password) EditText mLoginPasswordInput;
    @Bind(R.id.login_username) EditText mLoginUsernameInput;
    @BindString(R.string.cancel) String cancel;
    @BindString(R.string.register) String register;
    @BindString(R.string.register_username_taken) String usernameTaken;
    @BindString(R.string.register_dialog_title) String registerDialogTitle;

    /* Listener for Firebase session changes */
    private Firebase mRef = new Firebase("https://buzz-films.firebaseio.com/users");

    public static Map<String, User> accounts = new HashMap<String, User>();
    public static User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);

        mLoginButton.setEnabled(false); //disabled by default

        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mLoginUsernameInput.getText().length() > 0
                        && mLoginPasswordInput.getText().length() > 0) {
                    mLoginButton.setEnabled(true);
                } else {
                    mLoginButton.setEnabled(false);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        };
        mLoginUsernameInput.addTextChangedListener(watcher);
        mLoginPasswordInput.addTextChangedListener(watcher);

        /*We want to also allow the user to press the Done button on the keyboard*/
        mLoginPasswordInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    authenticateLogin();
//                    Hide virtual keyboard after "Done" action
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mLoginPasswordInput.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.login_button)
    public void authenticateLogin() {
        final String username = mLoginUsernameInput.getText().toString();
        final String password = mLoginPasswordInput.getText().toString();

        /*Check and see if the Login fields are blank*/
        boolean emptyFields = true;
        if (username.length() != 0 && password.length() != 0) {
            emptyFields = false;
        }

        mRef.authWithPassword(username, password, new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                Intent loginIntent = new Intent(WelcomeActivity.this, MainActivity.class);
                startActivity(loginIntent);
                finish(); //We're done with logging in
            }
            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
            }
        });

        // We didn't proceed to Welcome, so we must have an invalid login
        makeSnackbar(findViewById(android.R.id.content), getString(R.string.invalid_login), Snackbar.LENGTH_LONG,
                getColor(R.color.accent), getColor(R.color.primary_text_light)).show();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mLoginPasswordInput.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @OnClick(R.id.register_button)
    public void showRegisterDialog() {
        final MaterialDialog registerDialog = new MaterialDialog.Builder(WelcomeActivity.this)
                .title(registerDialogTitle)
                .customView(R.layout.register_dialog, true)
                .theme(Theme.DARK)
                .autoDismiss(false)
                .positiveText(register)
                .negativeText(cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull final MaterialDialog registerDialog, @NonNull DialogAction which) {
                        final EditText registerNameInput;
                        final EditText registerEmailInput;
                        final EditText registerUsernameInput;
                        final EditText registerPasswordInput;
                        final String name, email, username, password;

                        if (registerDialog.getCustomView() != null) {
                            registerNameInput = ButterKnife.findById(registerDialog, R.id.register_name);
                            registerEmailInput = ButterKnife.findById(registerDialog, R.id.register_email);
                            registerUsernameInput = ButterKnife.findById(registerDialog, R.id.register_username);
                            registerPasswordInput = ButterKnife.findById(registerDialog, R.id.register_password);
                            name = registerNameInput.getText().toString();
                            email = registerEmailInput.getText().toString();
                            username = registerUsernameInput.getText().toString();
                            password = registerPasswordInput.getText().toString();

                            mRef.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.getValue() != null) {
                                        //User exists already
                                        registerUsernameInput.setError(usernameTaken);
                                    } else {
                                        mRef.createUser(username, password, new Firebase.ResultHandler() {
                                            @Override
                                            public void onSuccess() {
                                            }
                                            @Override
                                            public void onError(FirebaseError firebaseError) {
                                            }
                                        });
                                        registerDialog.dismiss();
                                        registerUser(name, email, username, password);

                                        Intent loginIntent = new Intent(WelcomeActivity.this, MainActivity.class);
                                        startActivity(loginIntent);
                                        finish(); //We're done with logging in
                                    }
                                }

                                @Override
                                public void onCancelled(FirebaseError firebaseError) {
                                }
                            });
                        }
                    }
                }).build();

        final View registerAction = registerDialog.getActionButton(DialogAction.POSITIVE);

        if (registerDialog.getCustomView() != null) {
            final EditText registerNameInput = ButterKnife.findById(registerDialog, R.id.register_name);
            final EditText registerEmailInput = ButterKnife.findById(registerDialog, R.id.register_email);
            final EditText registerUsernameInput = ButterKnife.findById(registerDialog, R.id.register_username);
            final EditText registerPasswordInput = ButterKnife.findById(registerDialog, R.id.register_password);

            /*
             * TextWatcher lets us monitor the input fields while registering;
             * This make sure we don't allow the user to register with empty fields
             */
            final TextWatcher watcher = new TextWatcher() {
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

    /*Add the registered user to our backend*/
    public void registerUser(String name, String email, String username, String password) {
        Firebase userRef = mRef.child(username);
        userRef.child("username").setValue(username);
        userRef.child("name").setValue(name);
        userRef.child("email").setValue(email);
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
