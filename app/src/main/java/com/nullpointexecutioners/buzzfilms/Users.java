package com.nullpointexecutioners.buzzfilms;

public class Users {

    private String username;
    private String status;

    public Users(String username, String status) {
        this.username = username;
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public String getStatus() {
        return status;
    }
}
