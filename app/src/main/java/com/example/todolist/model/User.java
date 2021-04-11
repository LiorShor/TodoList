package com.example.todolist.model;

public class User {
    private final String Name;
    private final String Email;

    public User(String name, String email) {
        Name = name;
        Email = email;
    }
    public String getName() {
        return Name;
    }
    public String getEmail() {
        return Email;
    }
}
