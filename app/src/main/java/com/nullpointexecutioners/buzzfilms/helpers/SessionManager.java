package com.nullpointexecutioners.buzzfilms.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import com.firebase.client.Firebase;
import com.nullpointexecutioners.buzzfilms.Major;

/**
 * Helper class for managing a session (i.e. persistence across launches of the app
 */
public class SessionManager {

    private static SessionManager sInstance;

    private final SharedPreferences pref;
    private final SharedPreferences.Editor editor;

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

    //Major
    public static final String KEY_MAJOR = "major";

    final Firebase mRef = new Firebase("https://buzz-films.firebaseio.com/users");

    /**
     * Constructor for instance of SessionManager
     * @param context of session
     * @return instance of SessionManager
     */
    public static SessionManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new SessionManager(context);
        }
        return sInstance;
    }

    /**
     * Constructor for SessionManager class
     * @param context of session
     */
    private SessionManager(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    /**
     * Create login session
     * @param username to store in SharedPrefs
     * @param email to store in SharedPrefs
     */
    public void createLoginSession(String username, String name, String email, String major) {
        /*Store each value into SharedPrefs*/
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_EMAIL, email);
        if (major.equals(Major.NONE.toString())) {
            editor.putString(KEY_MAJOR, "NONE");
        } else {
            editor.putString(KEY_MAJOR, major);
        }

        //Commit changes to SharedPrefs
        editor.apply();
    }

    /**
     * Update the current Session's values
     * @param name to update
     * @param email to update
     * @param major to update
     */
    public void updateSession(String name, String email, String major) {
        /*Store the updated values into SharedPrefs*/
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_EMAIL, email);
        if (major.equals(Major.NONE.toString())) {
            editor.putString(KEY_MAJOR, "NONE");
        } else {
            editor.putString(KEY_MAJOR, major);
        }

        //Commit changes to SharedPrefs
        editor.apply();
    }

    /**
     * Checks if current user is logged in
     * If false, the user is redirected to WelcomeActivity to login or register
     * @return true or false depending if we're logged in
     */
    public boolean checkLogin() {
        return pref.getBoolean(IS_LOGIN, false);
    }

    /**
     * Getter for currently logged in user's username
     * @return current user's username
     */
    public String getLoggedInUsername() {
        String username = null;
        if (pref.contains(KEY_USERNAME) && pref.getBoolean(IS_LOGIN, false)) {
            username = pref.getString(KEY_USERNAME, null);
        }
        return username;
    }

    /**
     * Getter for currently logged in user's name
     * @return current user's name
     */
    public String getLoggedInName() {
        String name = null;
        if (pref.contains(KEY_NAME) && pref.getBoolean(IS_LOGIN, false)) {
            name = pref.getString(KEY_NAME, "name");
        }
        return name;
    }

    /**
     * Getter for currently logged in user's email
     * @return current user's email
     */
    public String getLoggedInEmail() {
        String email = null;
        if (pref.contains(KEY_EMAIL) && pref.getBoolean(IS_LOGIN, false)) {
            email = pref.getString(KEY_EMAIL, null);
        }
        return email;
    }

    /**
     * Getter for currently logged in user's major
     * @return current user's major
     */
    public String getLoggedInMajor() {
        String major = null;
        if (pref.contains(KEY_MAJOR) && pref.getBoolean(IS_LOGIN, false)) {
            major = pref.getString(KEY_MAJOR, null);
        }
        return major;
    }

    /**
     * Clears session credentials
     */
    public void logoutUser() {
        //UnAuth from Firebase
        mRef.unauth();

        //Clear SharedPrefs and set IS_LOGIN to false
        editor.clear();
        editor.putBoolean(IS_LOGIN, false);
        editor.commit();
    }

    /**
     * Getter for accessing the current SharedPrefs
     * @return this session's SharedPrefs
     */
    public SharedPreferences getPref() {
        return pref;
    }
}
