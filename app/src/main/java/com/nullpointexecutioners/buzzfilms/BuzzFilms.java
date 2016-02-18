package com.nullpointexecutioners.buzzfilms;

import com.firebase.client.Firebase;

/**
 * Extending the Application class allows us to set Firebase's context (this app)
 */
public class BuzzFilms extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
    }
}
