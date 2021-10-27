package com.example.todolist.view.dialogs;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.todolist.R;
import com.example.todolist.databinding.DialogLoginBinding;
import com.example.todolist.view.activities.TodoMainScreen;
import com.example.todolist.viewmodel.LoginViewModel;


public class Login extends DialogFragment {
    private LoginViewModel mLoginViewModel;
    private DialogLoginBinding mDialogBinding;
    private static final String USER_EMAIL = "userEmail";
    private static final String USER_DETAILS = "USER_DETAILS";
    private static final String USER_PASSWORD = "userPassword";

    public static Login newInstance(){
        return new Login();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mLoginViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);
        onClickLoginButton();
        onClicksSignUpEditText();
        onClickForgotPassword();
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(USER_DETAILS, MODE_PRIVATE);
        if (sharedPreferences.getString(USER_EMAIL, null) != null) {
            String userEmail = sharedPreferences.getString(USER_EMAIL, null);
            String userPassword = sharedPreferences.getString(USER_PASSWORD, null);
            SignIn(userEmail,userPassword);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mDialogBinding = DialogLoginBinding.inflate(LayoutInflater.from(getContext()));
        getDialog().getWindow().setBackgroundDrawableResource(R.drawable.rounded_dialog);
        return mDialogBinding.getRoot();
    }

    private boolean stringChecker(String emailAddress, String password) {
        boolean isNotEmpty = true;
        if (emailAddress.isEmpty()) {
            mDialogBinding.editTextEmailAddress.setError("Required*");
            isNotEmpty = false;
        }
        if (password.isEmpty()) {
            mDialogBinding.editTextPassword.setError("Required*");
            isNotEmpty = false;
        }
        return isNotEmpty;
    }

    private void onClicksSignUpEditText() {
        mDialogBinding.editTextSignUp.setOnClickListener(view -> {
            new Register(getContext());
        });
    }

    private void onClickForgotPassword() {
        mDialogBinding.forgotPasswordTextView.setOnClickListener(view ->
                new ForgotPassword(getContext()));
    }

    private void onClickLoginButton() {
        mDialogBinding.signInButton.setOnClickListener(view -> {
            String emailAddress = mDialogBinding.editTextEmailAddress.getText().toString();
            String password = mDialogBinding.editTextPassword.getText().toString();
            if (stringChecker(emailAddress, password)) {
                SignIn(emailAddress, password);
            }
        });
    }

    private void SignIn(String emailAddress, String password) {
        mLoginViewModel.login(emailAddress, password, getContext());
        mLoginViewModel.getUser().observe(this, user -> {
            if (user != null) {
                Intent moveToMainScreen = new Intent(getActivity(), TodoMainScreen.class);
                startActivity(moveToMainScreen);
            }
        });
    }

/*    private void getTaskListFromDB() {
        Intent moveToMainScreen = new Intent(mContext, TodoMainScreen.class);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        assert firebaseUser != null;
        String uid = firebaseUser.getUid();
        DatabaseReference myRef = database.getReference("tasks").child(uid);
        myRef.get().addOnSuccessListener(dataSnapshot -> {
            for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                TaskMap.getInstance().getTaskMap().put(Objects.requireNonNull(childDataSnapshot.child("m_ID").getValue()).toString(), childDataSnapshot.getValue(Task.class));
            }
            mContext.startActivity(moveToMainScreen);
        });
    }*/
}