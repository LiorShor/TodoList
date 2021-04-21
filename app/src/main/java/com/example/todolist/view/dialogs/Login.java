package com.example.todolist.view.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.example.todolist.R;
import com.example.todolist.model.Task;
import com.example.todolist.model.TaskMap;
import com.example.todolist.view.activities.TodoMainScreen;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class Login extends ConstraintLayout {
    private static final FirebaseAuth m_Auth = FirebaseAuth.getInstance();
    private Dialog m_LoginDialog;
    private Button m_LoginButton;
    private TextView m_SignUpEditText;
    private TextView m_ForgotPasswordTextView;

    public Login(@NonNull Context context) {
        super(context);
        m_LoginDialog = new Dialog(context);
        setDialogSettings();
        m_LoginButton = m_LoginDialog.findViewById(R.id.signIn);
        m_SignUpEditText = m_LoginDialog.findViewById(R.id.editTextSignUp);
        m_ForgotPasswordTextView =  m_LoginDialog.findViewById(R.id.forgotPasswordTextView);
        onClickLoginButton(context);
        onClicksSignUpEditText(context);
        onClickForgotPassword(context);
    }

    public Login(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public Login(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void setDialogSettings(){
        m_LoginDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        m_LoginDialog.setContentView(R.layout.dialog_login);
        m_LoginDialog.show();
        m_LoginDialog.setCanceledOnTouchOutside(true);
        setDialogWidthAndHeight();

    }
    private void setDialogWidthAndHeight()
    {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int m_Width = metrics.widthPixels;
        int m_Height = metrics.heightPixels;
        m_LoginDialog.getWindow().setLayout((6 * m_Width)/7, (4 * m_Height)/5);
    }
    private static boolean stringChecker(String emailAddress, String password, EditText emailAddressEditText, EditText passwordEditText){
        if(emailAddress.isEmpty())
            emailAddressEditText.setHintTextColor(Color.RED);
        if(password.isEmpty())
            passwordEditText.setHintTextColor(Color.RED);
        return true;
    }

    private void onClicksSignUpEditText(Context context) {
        m_SignUpEditText.setOnClickListener(view -> {
            m_LoginDialog.dismiss();
            new Register(context);
        });
    }
    private void onClickForgotPassword(Context context) {
        m_ForgotPasswordTextView.setOnClickListener(view ->
                new ForgotPassword(context));
    }


    private void onClickLoginButton(Context context) {
        m_LoginButton.setOnClickListener(view -> {
            EditText emailAddressEditText = m_LoginDialog.findViewById(R.id.editTextTextEmailAddress);
            EditText passwordEditText = m_LoginDialog.findViewById(R.id.editTextPassword);
            String emailAddress = emailAddressEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            if(stringChecker(emailAddress, password, emailAddressEditText, passwordEditText)) {
                SignIn(emailAddress, password, context);
            }
        });
    }

    public static void SignIn(String emailAddress, String password, Context context){

        m_Auth.signInWithEmailAndPassword(emailAddress, password).addOnSuccessListener(authResult -> {
            SharedPreferences sharedPreferences = context.getSharedPreferences("USER_DETAILS", MODE_PRIVATE);
            sharedPreferences.edit().putString("userEmail",emailAddress).apply();
            sharedPreferences.edit().putString("userPassword",password).apply();
            getTaskListFromDB(context);

        });
    }
    private static void getTaskListFromDB(Context context){
        Intent moveToMainScreen = new Intent(context, TodoMainScreen.class);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseUser firebaseUser = m_Auth.getCurrentUser();
        assert firebaseUser != null;
        String uid = firebaseUser.getUid();
        DatabaseReference myRef = database.getReference("tasks").child(uid);
        myRef.get().addOnSuccessListener(dataSnapshot -> {
            for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                TaskMap.getInstance().getTaskMap().put(Objects.requireNonNull(childDataSnapshot.child("m_ID").getValue()).toString(),childDataSnapshot.getValue(Task.class));
            }
            context.startActivity(moveToMainScreen);
        });
    }
}