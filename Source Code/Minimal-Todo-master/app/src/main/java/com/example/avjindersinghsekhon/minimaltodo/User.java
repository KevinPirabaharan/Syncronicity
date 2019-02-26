package com.example.avjindersinghsekhon.minimaltodo;

/**
 * Created by jauth on 2018-03-13.
 */

public class User {
    public String username;
    public String email;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }


}
