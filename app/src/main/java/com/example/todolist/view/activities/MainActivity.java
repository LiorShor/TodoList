package com.example.todolist.view.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.todolist.view.dialogs.Login;
import com.example.todolist.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        setContentView(R.layout.activity_main);
    }

    public void ExecuteLogin(View view) {
        FragmentManager fm = getSupportFragmentManager();
        Login login = Login.newInstance();
        login.show(fm, null);
    }
}