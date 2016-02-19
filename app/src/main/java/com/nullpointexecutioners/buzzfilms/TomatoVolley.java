package com.nullpointexecutioners.buzzfilms;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

/**
 * Created by Matthew on 2/18/2016.
 */
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
