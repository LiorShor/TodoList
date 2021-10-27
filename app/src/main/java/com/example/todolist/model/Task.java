package com.example.todolist.model;

import java.io.Serializable;
import java.util.LinkedList;

public class Task implements Serializable {
    private String id;
    private String title;
    private String dateCreated;
    private LinkedList<String> items;

    public Task(String dateCreated, String id, String title) {
        this.title = title;
        this.id = id;
        this.dateCreated = dateCreated;
        items = new LinkedList<>();
    }

    public Task() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String m_Title) {
        this.title = m_Title;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String m_DateCreated) {
        this.dateCreated = m_DateCreated;
    }

    public String getID() {
        return id;
    }

    public LinkedList<String> getItems() {
        return items;
    }

    public void setItems(LinkedList<String> items) {
        this.items = items;
    }
}
