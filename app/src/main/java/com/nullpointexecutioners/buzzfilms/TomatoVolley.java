package com.nullpointexecutioners.buzzfilms;

/**
 * Created by Matthew on 2/18/2016.
 */
public class TomatoVolley {
    private static TomatoVolley ourInstance = new TomatoVolley();

    public static TomatoVolley getInstance() {
        return ourInstance;
    }

    private TomatoVolley() {

    }
}
