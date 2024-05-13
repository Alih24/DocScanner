package com.ar.docscanner.process.model;

public class User {
    private String Password;

    public User(String password) {
        Password = password;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }
}
