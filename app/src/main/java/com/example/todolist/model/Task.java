package com.example.todolist.model;

import java.io.Serializable;
import java.util.LinkedList;

public class Task implements Serializable {
    public String m_ID;
    public String m_Title;
    public String m_DateCreated;
    public LinkedList<String> items;

    public Task(String m_Title, String m_DateCreated, String m_ID) {
        this.m_Title = m_Title;
        this.m_DateCreated = m_DateCreated;
        this.m_ID = m_ID;
        items = new LinkedList<>();
    }

    public Task() {
    }

    public String getTitle() {
        return m_Title;
    }

    public void setTitle(String m_Title) {
        this.m_Title = m_Title;
    }

    public String getDateCreated() {
        return m_DateCreated;
    }

    public void setDateCreated(String m_DateCreated) {
        this.m_DateCreated = m_DateCreated;
    }

    public String getID() {
        return m_ID;
    }

    public LinkedList<String> getItems() {
        return items;
    }

    public void setItems(LinkedList<String> items) {
        this.items = items;
    }
}
