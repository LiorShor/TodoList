package com.example.todolist.model;

import java.util.LinkedList;
import java.util.List;

public class TaskList{
    private final static TaskList instance = new TaskList();
    private final LinkedList<Task> taskList;
    private TaskList(){
        taskList = new LinkedList<>();
    }

    public static TaskList getInstance(){
        return instance;
    }

    public List<Task> getTaskList() {
        return taskList;
    }
}
