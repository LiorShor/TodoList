package com.example.todolist.model;

import java.util.LinkedList;

public class User {
    private final String name;
    private final String email;


    public User(String name, String email) {
        this.name = name;
        this.email = email;
        LinkedList<Task> tasks = new LinkedList<>();
    }
    public String getName() {
        return name;
    }
    public String getEmail() {
        return email;
    }
}
