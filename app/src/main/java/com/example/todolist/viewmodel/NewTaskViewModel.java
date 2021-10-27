package com.example.todolist.viewmodel;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.todolist.model.Task;
import com.example.todolist.repositories.TaskRepository;

import java.util.Map;

public class NewTaskViewModel extends ViewModel {
    private MutableLiveData<Map<String, Task>> mTaskMap;
    private TaskRepository taskRepository;

    public NewTaskViewModel(DataLoadListener context) {
        taskRepository = TaskRepository.getInstance(context);
    }

    public NewTaskViewModel() {
    }

    public void init(DataLoadListener dataLoadListener) {
        if (mTaskMap != null) {
            return;
        }
        mTaskMap = TaskRepository.getInstance(dataLoadListener).getTaskMap();
    }

    public void writeNewTaskToDatabase(Task task) {
        taskRepository.writeToTaskMap(task);
    }

    public MutableLiveData<Map<String, Task>> getTaskMap() {
        return mTaskMap;
    }
}
