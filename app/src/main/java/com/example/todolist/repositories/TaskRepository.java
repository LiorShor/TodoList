package com.example.todolist.repositories;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.todolist.model.Task;
import com.example.todolist.viewmodel.DataLoadListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TaskRepository {
    private static TaskRepository instance;
    private static DataLoadListener dataLoadListener;
    private final MutableLiveData<Task> task = new MutableLiveData<>();
    private final Map<String,Task> mTaskMap = new HashMap<String, Task>() {
    };
    private static final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    public static TaskRepository getInstance(DataLoadListener context) {
        if (instance == null) {
            instance = new TaskRepository();
        }
        dataLoadListener = (DataLoadListener) context;
        return instance;
    }
    public MutableLiveData<Task> getTask(){
        return task;
    }

    public void writeNewTaskToDatabase(Task task){
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        assert firebaseUser != null;
        String uid = firebaseUser.getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference("tasks").child(uid).child(task.getID());
        databaseReference.setValue(task);
        mTaskMap.put(task.getID(), task);
        dataLoadListener.onTaskLoaded();

//        this.task.postValue(task);
    }

    private void getTaskListFromDB() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        assert firebaseUser != null;
        String uid = firebaseUser.getUid();
        DatabaseReference reference = database.getReference("tasks").child(uid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childDataSnapshot : snapshot.getChildren()) {
                    mTaskMap.put(childDataSnapshot.getKey(), childDataSnapshot.getValue(Task.class));
                }
                dataLoadListener.onTaskLoaded();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
/*        reference.get().addOnSuccessListener(dataSnapshot -> {

        });*/
    }

    public MutableLiveData<Map<String, Task>> getTaskMap() {
        getTaskListFromDB();
        MutableLiveData<Map<String,Task>> data = new MutableLiveData<>();
        data.setValue(mTaskMap);
        return data;
    }
    public MutableLiveData<Map<String, Task>> writeToTaskMap(Task task) {
        writeNewTaskToDatabase(task);
        MutableLiveData<Map<String,Task>> data = new MutableLiveData<>();
        data.setValue(mTaskMap);
        return data;
    }
}
