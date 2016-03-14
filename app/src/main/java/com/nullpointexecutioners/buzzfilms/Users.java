package com.nullpointexecutioners.buzzfilms;

public class Users {

    private String username;
    private String name;
    private String email;
    private String major;
    private String status;

    public Users(String username, String name, String email, String major, String status) {
        this.username = username;
        this.name = name;
        this.email = email;
        this.major = major;
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getMajor() {
        return major;
    }

    public String getStatus() {
        return status;
    }
}
