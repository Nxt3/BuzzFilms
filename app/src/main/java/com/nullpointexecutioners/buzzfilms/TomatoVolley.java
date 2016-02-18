package com.nullpointexecutioners.buzzfilms;

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
public class TomatoVolley extends AppCompatActivity {
    private static TomatoVolley ourInstance = new TomatoVolley();

    public static TomatoVolley getInstance() {
        return ourInstance;
    }

    private final String API_KEY = "vbhetn4chdpudf7mqhckaccasdf";

    private TomatoVolley() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://api.rottentomatoes.com/api/public/v1.0.json?apikey=";

        url = url + API_KEY;
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        System.out.println("Response is: " + response.substring(0, 500));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("That didn't work!");
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}
