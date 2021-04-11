package com.example.todolist.view.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.example.todolist.view.dialogs.Login;
import com.example.todolist.R;

import static com.example.todolist.view.dialogs.Login.SignIn;

public class
MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        SharedPreferences sharedPreferences = getSharedPreferences("USER_DETAILS", MODE_PRIVATE);
        // Check if user already signed in to the app
        if(sharedPreferences.getString("userPassword", null) != null) {
            String userEmail = sharedPreferences.getString("userEmail", null);
            String userPassword = sharedPreferences.getString("userPassword", null);
            SignIn(userEmail,userPassword,this);
        }
        setContentView(R.layout.activity_main);
    }

    public void ExecuteLogin(View view) {
        new Login(this);
    }
}