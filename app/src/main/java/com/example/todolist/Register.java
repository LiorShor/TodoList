package com.example.todolist;

import androidx.constraintlayout.widget.ConstraintLayout;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends ConstraintLayout {

    private EditText m_EditTextPersonName;
    private EditText m_EditTextEmail;
    private EditText m_EditTextRegisterPassword;
    private EditText m_EditTextRePassword;
    private Dialog m_RegisterDialog;
    private FirebaseAuth m_Auth;
    public Register(Context context) {
        this(context, null, 0);
        m_RegisterDialog = new Dialog(context);
        setDialogSettings();
        m_EditTextPersonName = m_RegisterDialog.findViewById(R.id.editTextPersonName);
        m_EditTextEmail = m_RegisterDialog.findViewById(R.id.editTextEmail);
        m_EditTextRegisterPassword = m_RegisterDialog.findViewById(R.id.editTextRegisterPassword);
        m_EditTextRePassword = m_RegisterDialog.findViewById(R.id.editTextRePassword);
        m_Auth = FirebaseAuth.getInstance();
        setOnClickRegisterButton();
    }

    public Register(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Register(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    private void setDialogSettings(){
        m_RegisterDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        m_RegisterDialog.setContentView(R.layout.dialog_register);
        m_RegisterDialog.show();
        m_RegisterDialog.setCanceledOnTouchOutside(true);
        setDialogWidthAndHeight();

    }
    private void setDialogWidthAndHeight()
    {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int m_Width = metrics.widthPixels;
        int m_Height = metrics.heightPixels;
        m_RegisterDialog.getWindow().setLayout((6 * m_Width)/7, (4 * m_Height)/5);
    }

    private void setOnClickRegisterButton(){
        Button RegisterButton = m_RegisterDialog.findViewById(R.id.registerBT);

        RegisterButton.setOnClickListener(view ->
        {
            String name = m_EditTextPersonName.getText().toString();
            String email = m_EditTextEmail.getText().toString();
            String password = m_EditTextRegisterPassword.getText().toString();
            String rePassword = m_EditTextRePassword.getText().toString();
            if(validation(name, email, password, rePassword))
                writeNewUser(password, email, name);
        });
    }
    public void writeNewUser(String password, String email,String name) {
        User user = new User(name,email);
        Intent intent = new Intent(getContext(),TodoMainScreen.class);
        m_Auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener((Activity) getContext(), task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        FirebaseUser firebaseUser = m_Auth.getCurrentUser();
                        assert firebaseUser != null;
                        String uid = firebaseUser.getUid();
                        // Write a message to the database
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference databaseReference = database.getReference("users").child(uid);
                        databaseReference.setValue(user);
                        m_RegisterDialog.dismiss();
                        getContext().startActivity(intent);

                    } else {
                        Toast.makeText(getContext(), "Error signing in", Toast.LENGTH_SHORT).show();
                        // If sign in fails, display a message to the user.
                    }
//        m_Database.child("users").child(userId).setValue(user);
                });
    }
    private boolean validation(String name, String email, String password, String rePassword) {
        boolean validationSuccess = true;
        if (name.isEmpty()) {
            m_EditTextPersonName.setHintTextColor(Color.RED);
            validationSuccess = false;
        }
        if (email.isEmpty()) {
            m_EditTextEmail.setHintTextColor(Color.RED);
            validationSuccess = false;
        }
        if(rePassword.equals(""))
        {
            m_EditTextRePassword.setHintTextColor(Color.RED);
            validationSuccess = false;
        }
        if(password.equals(""))
        {
            m_EditTextRegisterPassword.setHintTextColor(Color.RED);
            validationSuccess = false;
        }
        if (!password.equals(rePassword)) {
            m_EditTextRegisterPassword.setTextColor(Color.RED);
            m_EditTextRePassword.setTextColor(Color.RED);
            validationSuccess = false;
        }
        return validationSuccess;
    }
}