package com.example.todolist.model;

import java.util.LinkedList;

public class User {
    private final String mName;
    private final String mEmail;


    public User(String name, String email) {
        this.mName = name;
        this.mEmail = email;
        LinkedList<Task> tasks = new LinkedList<>();
    }
    public String getName() {
        return mName;
    }
    public String getEmail() {
        return mEmail;
    }
}
