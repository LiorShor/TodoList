package com.example.todolist.view.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.todolist.R;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends ConstraintLayout {
    private Dialog m_ForgotPasswordDialog;

    public ForgotPassword(@NonNull Context context) {
        super(context);
        m_ForgotPasswordDialog = new Dialog(context);
        setDialogSettings();
        setOnCLickRecover();
    }

    private void setOnCLickRecover() {
        Button recoverLostPassword = m_ForgotPasswordDialog.findViewById(R.id.recoverPasswordBT);
        recoverLostPassword.setOnClickListener(view ->
                recoverLostPassword());
    }

    public ForgotPassword(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ForgotPassword(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void recoverLostPassword() {
        TextView m_EmailTextView = m_ForgotPasswordDialog.findViewById(R.id.editTextForgotEmail);
        String email = m_EmailTextView.getText().toString();
        if (!email.isEmpty()) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), R.string.emailsent, Toast.LENGTH_LONG).show();
                            m_ForgotPasswordDialog.dismiss();
                        } else
                            m_EmailTextView.setHintTextColor(Color.RED);
                    });
        } else
            m_EmailTextView.setHintTextColor(Color.RED);
    }

    private void setDialogSettings(){
        m_ForgotPasswordDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        m_ForgotPasswordDialog.setContentView(R.layout.dialog_forgotpassword);
        m_ForgotPasswordDialog.show();
        m_ForgotPasswordDialog.setCanceledOnTouchOutside(true);
        setDialogWidthAndHeight();
    }

    private void setDialogWidthAndHeight()
    {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int m_Width = metrics.widthPixels;
        int m_Height = metrics.heightPixels;
        m_ForgotPasswordDialog.getWindow().setLayout((6 * m_Width)/7, (4 * m_Height)/5);
    }

}
