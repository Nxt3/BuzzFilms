package com.nullpointexecutioners.buzzfilms;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class TomatoVolley {
    private static TomatoVolley instance;
    private RequestQueue requestQueue;
    public static final String API_KEY = "vbhetn4chdpudf7mqhckacca";

    private TomatoVolley(Context context) {
        requestQueue = Volley.newRequestQueue(context);
    }

    public static TomatoVolley getInstance(Context context) {
        if (instance == null) {
            instance = new TomatoVolley(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }
}
