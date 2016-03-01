package com.nullpointexecutioners.buzzfilms;

import android.content.Context;

import com.android.volley.RequestQueue;

public class Volley {
    private static Volley instance;
    private static RequestQueue requestQueue;
    public static final String API_KEY = "vbhetn4chdpudf7mqhckacca";

    private Volley(Context context) {
        requestQueue = com.android.volley.toolbox.Volley.newRequestQueue(context);
    }

    public static Volley getInstance(Context context) {
        if (instance == null) {
            instance = new Volley(context);
        }
        return instance;
    }

    public static RequestQueue getRequestQueue() {
        return requestQueue;
    }
}
