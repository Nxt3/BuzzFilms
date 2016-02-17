package com.nullpointexecutioners.buzzfilms;

import com.firebase.client.Firebase;

public class BuzzFilms extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
    }
}
