package com.example.todolist.repositories;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.MutableLiveData;

import com.example.todolist.model.User;
import com.google.firebase.auth.FirebaseAuth;

public class UserRepository {
    private static UserRepository instance;
    private final MutableLiveData<User> user = new MutableLiveData<>();
    private static final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    public static UserRepository getInstance() {
        if (instance == null) {
            instance = new UserRepository();
        }
        return instance;
    }

    //Method to get to the Database
    public MutableLiveData<User> getUser(){
        return user;
    }

    public void addNewUser(User user){

    }

    public void login(String email, String password, Context context) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(authResult -> {
                        if (authResult.isSuccessful()) {
                            user.postValue(new User(email,password));
                            SharedPreferences sharedPreferences = context.getSharedPreferences("USER_DETAILS", MODE_PRIVATE);
                            sharedPreferences.edit().putString("userEmail", email).apply();
                            sharedPreferences.edit().putString("userPassword", password).apply();
                        }
                });
    }
}
