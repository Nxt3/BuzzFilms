package com.nullpointexecutioners.buzzfilms.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import com.nullpointexecutioners.buzzfilms.Major;
import com.nullpointexecutioners.buzzfilms.R;
import com.nullpointexecutioners.buzzfilms.helpers.NetworkHelper;
import com.nullpointexecutioners.buzzfilms.helpers.SessionManager;
import com.nullpointexecutioners.buzzfilms.helpers.StringHelper;
import com.nullpointexecutioners.buzzfilms.helpers.ViewHelper;

import butterknife.Bind;
import butterknife.BindInt;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Login/Register activity
 */
public class WelcomeActivity extends AppCompatActivity {

    @Bind(R.id.login_button) Button mLoginButton;
    @Bind(R.id.login_password) EditText mLoginPasswordInput;
    @Bind(R.id.login_username) EditText mLoginUsernameInput;
    @Bind(android.R.id.content) View thisActivity;
    @BindInt(R.color.accent) int accentColor;
    @BindInt(R.color.primary_text_light) int primaryTextLightColor;
    @BindString(R.string.auth_progress_dialog_content) String authProgressDialogContent;
    @BindString(R.string.auth_progress_dialog_title) String authProgressDialogTitle;
    @BindString(R.string.cancel) String cancel;
    @BindString(R.string.network_not_available) String invalidEmail;
    @BindString(R.string.network_not_available) String networkNotAvailable;
    @BindString(R.string.register) String register;
    @BindString(R.string.register_dialog_title) String registerDialogTitle;
    @BindString(R.string.register_username_taken) String usernameTaken;

    private MaterialDialog mAuthProgressDialog;

    private SessionManager mSession;

    final Firebase mRef = new Firebase("https://buzz-films.firebaseio.com/users");

    /**
     * Creates activity
     * @param savedInstanceState no idea what this is
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);

        this.mSession = SessionManager.getInstance(getApplicationContext());

        mLoginButton.setEnabled(false); //disable the login button until the user fills out the login fields

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
                    /*Hide virtual keyboard after "Done" action is pressed on keyboard*/
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mLoginPasswordInput.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    return true;
                }
                return false;
            }
        });

        /*Shows progress while authenticating user*/
        mAuthProgressDialog = new MaterialDialog.Builder(WelcomeActivity.this)
                .title(authProgressDialogTitle)
                .content(authProgressDialogContent)
                .progress(true, 0)
                .build();
    }

    /**
     * Handles this activity once it is destroyed
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    /**
     * When the user clicks the login button, the credentials the user has entered need to be authenticated against what is stored in Firebase.
     */
    @OnClick(R.id.login_button)
    public void authenticateLogin() {
        final String USERNAME = mLoginUsernameInput.getText().toString();
        final String PASSWORD = mLoginPasswordInput.getText().toString();

        /*Show progress dialog when we try to login*/
        mAuthProgressDialog.show();
        if (NetworkHelper.isInternetAvailable()) {
            mRef.authWithPassword(StringHelper.setUserWithDummyDomain(USERNAME), PASSWORD, new Firebase.AuthResultHandler() {
                @Override
                public void onAuthenticated(AuthData authData) {
                    getUserInfoForLogin(USERNAME);
                    /*This delay of 500ms must be present or else we run into issues with setting the
                    * navdrawer info in the MainActivity. Makes sense. ¯\_(ツ)_/¯
                    */
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            mAuthProgressDialog.dismiss();
                            //We successfully logged in, go to MainActivity
                            Intent loginIntent = new Intent(WelcomeActivity.this, MainActivity.class);
                            startActivity(loginIntent);
                            finish(); //We're done with logging in
                        }
                    }, 500);
                }
                @Override
                public void onAuthenticationError(FirebaseError firebaseError) {
                    //Invalid login credentials
                    Log.e("authenticateLogin", firebaseError.toString());
                    mAuthProgressDialog.dismiss();
                    ViewHelper.makeSnackbar(thisActivity, getString(R.string.invalid_login), Snackbar.LENGTH_LONG,
                            accentColor, primaryTextLightColor).show();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mLoginPasswordInput.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            });
        } else {
            mAuthProgressDialog.dismiss();
            ViewHelper.makeSnackbar(thisActivity, networkNotAvailable, Snackbar.LENGTH_LONG, accentColor, primaryTextLightColor).show();
        }
    }

    /**
     * When the user clicks the register button, a dialog will be shown where they can enter all of the necessary information to register a new account.
     * Once the user is done editing the information, the user will press the "Register" button in the dialog to commit their registration to Firebase.
     */
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
                        final String NAME, EMAIL, USERNAME, PASSWORD;

                        if (registerDialog.getCustomView() != null) {
                            registerNameInput = ButterKnife.findById(registerDialog, R.id.register_name);
                            registerEmailInput = ButterKnife.findById(registerDialog, R.id.register_email);
                            registerUsernameInput = ButterKnife.findById(registerDialog, R.id.register_username);
                            registerPasswordInput = ButterKnife.findById(registerDialog, R.id.register_password);
                            NAME = registerNameInput.getText().toString();
                            EMAIL = registerEmailInput.getText().toString();
                            USERNAME = registerUsernameInput.getText().toString();
                            PASSWORD = registerPasswordInput.getText().toString();

                            if (NetworkHelper.isInternetAvailable()) {
                                mRef.child(USERNAME).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.getValue() != null) {
                                            //User exists already, so display an error on the username field and focus there
                                            registerUsernameInput.setError(usernameTaken);
                                            registerUsernameInput.requestFocus();
                                        } else {
                                            registerDialog.dismiss();
                                            mAuthProgressDialog.show();
                                            mRef.createUser(StringHelper.setUserWithDummyDomain(USERNAME), PASSWORD, new Firebase.ResultHandler() {
                                                @Override
                                                public void onSuccess() {
                                                    //User was created successfully--so take them to the MainActivity
                                                    mAuthProgressDialog.dismiss();
                                                    registerUser(USERNAME, NAME, EMAIL);
                                                    mSession.createLoginSession(USERNAME, NAME, EMAIL);

                                                    //Go to the MainActivity
                                                    Intent loginIntent = new Intent(WelcomeActivity.this, MainActivity.class);
                                                    startActivity(loginIntent);
                                                    finish(); //We're done with the WelcomeActivity
                                                }
                                                @Override
                                                public void onError(FirebaseError firebaseError) {
                                                    mAuthProgressDialog.dismiss();
                                                    String error = firebaseError.getMessage();
                                                    Log.e("Registering...", error);
                                                    ViewHelper.makeSnackbar(thisActivity, "An unexpected error occurred", Snackbar.LENGTH_LONG, accentColor, primaryTextLightColor).show();
                                                }
                                            });
                                        }
                                    }
                                    @Override
                                    public void onCancelled(FirebaseError firebaseError) {
                                    }
                                });
                            } else {
                                registerDialog.dismiss();
                                ViewHelper.makeSnackbar(thisActivity, networkNotAvailable, Snackbar.LENGTH_LONG, accentColor, primaryTextLightColor).show();
                            }
                        }
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog registerDialog, @NonNull DialogAction which) {
                        registerDialog.dismiss();
                    }
                }).build();

        final View registerAction = registerDialog.getActionButton(DialogAction.POSITIVE);

        if (registerDialog.getCustomView() != null) {
            final EditText registerNameInput = ButterKnife.findById(registerDialog, R.id.register_name);
            final EditText registerEmailInput = ButterKnife.findById(registerDialog, R.id.register_email);
            final EditText registerUsernameInput = ButterKnife.findById(registerDialog, R.id.register_username);
            final EditText registerPasswordInput = ButterKnife.findById(registerDialog, R.id.register_password);

            /*
             * TextWatcher lets us monitor the input fields while registering
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
            /**
             * This lets us monitor the validity of the email entered
             */
            final TextWatcher emailWatcher = StringHelper.emailWatcher(registerEmailInput, registerAction, invalidEmail);

            /*We want to watch all EditText fields for input*/
            registerNameInput.addTextChangedListener(watcher);
            registerEmailInput.addTextChangedListener(emailWatcher);
            registerUsernameInput.addTextChangedListener(watcher);
            registerPasswordInput.addTextChangedListener(watcher);
        }
        registerDialog.show();
        registerAction.setEnabled(false); //disabled by default
    }

    /**
     * Method that adds all of the user's attributes to Firebase
     * @param name of user
     * @param email of user
     * @param username to register
     */
    private void registerUser(String username, String name, String email) {
        Firebase userRef = mRef.child(username);
        userRef.child("username").setValue(username);
        userRef.child("name").setValue(name);
        userRef.child("email").setValue(email);
        userRef.child("major").setValue(Major.NONE);
        userRef.child("interests").setValue("");
    }

    /**
     * Helper method for getting the user's properties (ONLY WHEN LOGGING IN) from Firebase and
     * then creating a Session using SessionManager
     * @param username to get information for
     */
    private void getUserInfoForLogin(String username) {
        final String USERNAME = username;
        mRef.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String NAME = dataSnapshot.child("name").getValue().toString();
                final String EMAIL = dataSnapshot.child("email").getValue().toString();
                mSession.createLoginSession(USERNAME, NAME, EMAIL);
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }
}
