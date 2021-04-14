package com.example.todolist.model;

import java.util.HashMap;
import java.util.Map;

public class TaskMap {
    private final static TaskMap instance = new TaskMap();
    private final Map<String, Task> taskMap;
    private TaskMap(){
        taskMap = new HashMap<>();
    }

    public static TaskMap getInstance(){
        return instance;
    }

    public Map<String, Task> getTaskMap() {
        return taskMap;
    }
}
