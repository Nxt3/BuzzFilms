package com.nullpointexecutioners.buzzfilms;

/**
 * Created by Matthew on 2/11/2016.
 * This is the user class.
 */
public class User {
    private String username;
    private String password; // Not hashed yet
    private String name;
    private String email;
    Major studentMajor;
    private String interests;

    public User(String username, String password, String name, String email) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
    }

    public enum Major {
        CS, EE, ME, ISYE, Math, Phys, Chem, ChemE
    }

    public String getUsername() {
        return this.username;
    }

    // Such security. Wow.
    public String getPassword() {
        return this.password;
    }

    public String getName() {
        return this.name;
    }

    public String getEmail() {
        return this.email;
    }

    public Major getMajor() { return this.studentMajor; }

    public String getInterests() {
        return interests;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setName(String name) {
        this.name = name;
    }


    public void setEmail(String email) {
        this.email = email;
    }

    public void setMajor(Major major) { this.studentMajor = major; }

    public void setInterests(String interests) {
        this.interests = interests;
    }

}
