package com.example.todolist.model;

import java.util.LinkedList;

public class Task {
    private final String m_ID;
    private String m_Title;
    private String m_DateCreated;
    private LinkedList<String> items;

    public Task(String m_Title, String m_DateCreated, String m_ID) {
        this.m_Title = m_Title;
        this.m_DateCreated = m_DateCreated;
        this.m_ID = m_ID;

    }

    public String getM_Title() {
        return m_Title;
    }

    public void setM_Title(String m_Title) {
        this.m_Title = m_Title;
    }

    public String getM_DateCreated() {
        return m_DateCreated;
    }

    public void setM_DateCreated(String m_DateCreated) {
        this.m_DateCreated = m_DateCreated;
    }

    public String getM_ID() {
        return m_ID;
    }

    public LinkedList<String> getItems() {
        return items;
    }

    public void setItems(LinkedList<String> items) {
        this.items = items;
    }
}
