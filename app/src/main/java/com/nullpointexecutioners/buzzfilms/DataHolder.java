package com.nullpointexecutioners.buzzfilms;

/**
 * Created by Matthew on 2/14/2016.
 */
public class DataHolder {
    public static User currentUser;

    public static void setCurrentUser(User u) {
        currentUser = u;
    }

    public static User getCurrentUser() {
        return currentUser;
    }
}
