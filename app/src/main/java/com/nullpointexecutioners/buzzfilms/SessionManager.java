package com.nullpointexecutioners.buzzfilms;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.firebase.client.Firebase;

import java.util.HashMap;

/**
 * Helper class for managing a session (i.e. persistence across launches of the app
 */
public class SessionManager {

    Context context;
    SharedPreferences pref;
    //Editor for Shared preferences
    SharedPreferences.Editor editor;

    //SharedPref file name
    private static final String PREF_NAME = "current.user";

    //All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    //Username
    public static final String KEY_USERNAME = "username";

    //Name
    public static final String KEY_NAME = "name";

    //Email
    public static final String KEY_EMAIL = "email";

    final Firebase mRef = new Firebase("https://buzz-films.firebaseio.com/users");

    //Constructor for SessionManager
    public SessionManager(Context context) {
        this.context = context;
        pref = this.context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    /**
     * Create login session
     * @param username to store in SharedPrefs
     * @param email to store in SharedPrefs
     */
    public void createLoginSession(String username, String name, String email) {
        /*Store each value into SharedPrefs*/
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_EMAIL, email);

        //Commit changes to SharedPrefs
        editor.commit();
    }

    /**
     * Update the current Session's values
     * @param name to update
     * @param email to update
     */
    public void updateSession(String name, String email) {
        /*Store the updated values into SharedPrefs*/
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_EMAIL, email);

        //Commit changes to SharedPrefs
        editor.commit();
    }

    /**
     * Checks if current user is logged in
     * If false, the user is redirected to WelcomeActivity to login or register
     * @return activity the SplashScreen should take the user to
     */
    public Intent checkLogin() {
        Intent intent;

        if(!this.isLoggedIn()) {
            //User is not logged in; redirect them to Login Activity
            intent = new Intent(context, WelcomeActivity.class);
        } else {
            //User is logged in; redirect them to MainActivity
            intent = new Intent(context, MainActivity.class);
        }
        return intent;
    }


    /**
     * Store properties of user to a HashMap in SharedPrefs
     * @return HashMap of values
     */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<>();

        //Username
        user.put(KEY_USERNAME, pref.getString(KEY_USERNAME, null));

        //Name
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));

        //Email
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));

        return user;
    }

    /**
     * Clears session credentials
     */
    public void logoutUser() {
        //UnAuth from Firebase
        mRef.unauth();

        //Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        //After logout redirect user to WelcomeActivity to login or register
        Intent intent = new Intent(context, WelcomeActivity.class);
        //Closing all the Activities
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        //Add new Flag to start new Activity
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(intent);
    }

    /**
     * Helper method for determining if someone is logged in
     * @return
     */
    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }
}
