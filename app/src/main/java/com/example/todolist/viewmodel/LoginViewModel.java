package com.example.todolist.viewmodel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.todolist.model.User;
import com.example.todolist.repositories.UserRepository;

public class LoginViewModel extends ViewModel {
    private final MutableLiveData<User> mUser;
    private final UserRepository userRepository;

    public LoginViewModel() {
        userRepository = new UserRepository();
        mUser = userRepository.getUser();
    }

    public void login(String email, String password, Context context) {
        userRepository.login(email, password, context);
    }

    public LiveData<User> getUser() {
        return mUser;
    }
}
