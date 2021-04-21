package com.example.todolist.view.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.WindowManager;

import com.example.todolist.R;
import com.example.todolist.view.fragments.SubTaskFragment;
import com.example.todolist.view.fragments.TaskFragment;

public class TodoMainScreen extends AppCompatActivity implements TaskFragment.DataPassListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        setContentView(R.layout.activity_todo_main_screen);
        loadTasksFragment();
    }
    public void loadTasksFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.flContent, new TaskFragment()).commit();
    }

    @Override
    public void passData(int data) {
        SubTaskFragment fragmentB = new SubTaskFragment();
        Bundle args = new Bundle();
        args.putInt(SubTaskFragment.DATA_RECEIVE, data);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentB.setArguments(args);
        fragmentTransaction.replace(R.id.flContent, fragmentB).addToBackStack(null).commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //call super
        super.onActivityResult(requestCode, resultCode, data);
    }

}