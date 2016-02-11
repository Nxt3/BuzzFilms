package com.nullpointexecutioners.buzzfilms;

/**
 * Created by Matthew on 2/11/2016.
 * This is the user class.
 */
public class User {
    private String username;
    private String password; // Not hashed yet

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return this.username;
    }

    // Such security. Wow.
    public String getPassword() {
        return this.password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
