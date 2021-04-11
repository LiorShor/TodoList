package com.example.todolist.view.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.example.todolist.R;
import com.example.todolist.model.Task;
import com.google.firebase.auth.FirebaseAuth;
import java.util.Calendar;

public class NewTask extends ConstraintLayout {
    private Dialog m_NewTaskDialog;
    private TextView m_TitleTextView;
    private TextView m_FriendEmail;
    private CheckBox m_AddFriendCheckBox;
    private boolean isUserExist = false;
    private static final FirebaseAuth m_Auth = FirebaseAuth.getInstance();
    private static final String TAG = "NewTask";
    public NewTask(@NonNull Context context) {
        super(context);
        m_NewTaskDialog = new Dialog(context);
        setDialogSettings();
        m_TitleTextView = m_NewTaskDialog.findViewById(R.id.editTextTitle);
        m_FriendEmail = m_NewTaskDialog.findViewById(R.id.editTextTextFriendEmail);
        m_AddFriendCheckBox = m_NewTaskDialog.findViewById(R.id.addFriendCheckBox);
        setOnClickAddFriendCheckBox();
        setOnClickCreateNewTaskButton();
    }
    private void setOnClickAddFriendCheckBox(){
        m_AddFriendCheckBox.setOnCheckedChangeListener((compoundButton, isChecked) -> m_FriendEmail.setEnabled(isChecked));
    }

    private void setOnClickCreateNewTaskButton(){
        Button m_CreateNewTaskButton = m_NewTaskDialog.findViewById(R.id.createNewTask);
        m_CreateNewTaskButton.setOnClickListener(view ->
        {
            if(titleAndEmailValidation()) {
                Task task = new Task(m_TitleTextView.getText().toString(), Calendar.getInstance().toString(), "1");
                Log.d(TAG, "setOnClickCreateNewTaskButton: " + task);
            }
        });
    }
    private boolean titleAndEmailValidation(){
        boolean validationSuccess = true;
        if(m_TitleTextView.getText().toString().isEmpty()) {
            m_TitleTextView.setHintTextColor(Color.RED);
            validationSuccess = false;
        }
        if(m_AddFriendCheckBox.callOnClick() && m_FriendEmail.toString().isEmpty() || !checkEmailExistsOrNot()) {
            m_FriendEmail.setHintTextColor(Color.RED);
            m_FriendEmail.setTextColor(Color.RED);
            validationSuccess = false;
        }
        if (checkEmailExistsOrNot()) {
            m_FriendEmail.setHintTextColor(Color.BLACK);
            m_FriendEmail.setTextColor(Color.BLACK);
        }
        return validationSuccess;
    }

    private boolean checkEmailExistsOrNot(){
        String friendEmail = m_FriendEmail.getText().toString();
        if(!friendEmail.isEmpty()) {
           Thread thread =  new Thread(() -> m_Auth.fetchSignInMethodsForEmail(friendEmail).addOnCompleteListener(task -> {
                if (task.getResult() != null && task.getResult().getSignInMethods() != null)
                    if (task.getResult().getSignInMethods().size() != 0) {
                        isUserExist = true;
                    }
            }));
           thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return isUserExist;
    }

    public NewTask(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public NewTask(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void setDialogSettings() {
        m_NewTaskDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        m_NewTaskDialog.setContentView(R.layout.dialog_new_task);
        m_NewTaskDialog.show();
        m_NewTaskDialog.setCanceledOnTouchOutside(true);
        setDialogWidthAndHeight();

    }
    private void setDialogWidthAndHeight() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int m_Width = metrics.widthPixels;
        int m_Height = metrics.heightPixels;
        m_NewTaskDialog.getWindow().setLayout((6 * m_Width)/7, (4 * m_Height)/5);
    }
}
