package com.example.winningrecipe;

public class SingletonUser {
    private static SingletonUser instance;
    private String user;

    private SingletonUser() {
        // Private constructor to prevent instantiation.
    }

    public static synchronized SingletonUser getInstance() {
        if (instance == null) {
            instance = new SingletonUser();
        }
        return instance;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String userEmail) {
        this.user = userEmail;
    }
}
