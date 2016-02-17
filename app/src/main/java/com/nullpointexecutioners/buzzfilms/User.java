package com.nullpointexecutioners.buzzfilms;

public class User {

    Major studentMajor;
    private String email;
    private String interests;
    private String name;
    private String username;

    public User(String username, String name, String email) {
        this.username = username;
        this.name = name;
        this.email = email;
    }

    public enum Major {
        NONE("Select your major"),
        CS("Computer Science"),
        EE("Electrical Engineering"),
        ME("Mechanical Engineering"),
        ISYE("Industrial & Systems Engineering"),
        MATH("Mathematics"),
        PHYS("Physics"),
        CHEM("Chemistry"),
        CHEME("Chemical Engineering");

        private String theMajor;

        Major(String aMajor) {
            theMajor = aMajor;
        }

        @Override
        public String toString() {
            return theMajor;
        }
    }

    /*Getter methods*/
    public String getUsername() {
        return this.username;
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

    /*Setter methods*/
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
