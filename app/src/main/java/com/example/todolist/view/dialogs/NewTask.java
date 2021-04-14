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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.todolist.R;
import com.example.todolist.model.Task;
import com.example.todolist.model.TaskList;
import com.example.todolist.view.adapters.TaskAdapter;
import com.example.todolist.view.fragments.SubTaskFragment;
import com.example.todolist.view.fragments.TaskFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class NewTask extends ConstraintLayout implements TaskAdapter.ItemCallBack {
    private Dialog m_NewTaskDialog;
    private TextView m_TitleTextView;
    private TextView m_FriendEmail;
    private CheckBox m_AddFriendCheckBox;
    private boolean isUserExist = false;
    private FragmentManager fragmentManager;
    private static final FirebaseAuth m_Auth = FirebaseAuth.getInstance();
    private static final String TAG = "NewTask";
    private final SimpleDateFormat dateFormatForDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    public NewTask(@NonNull Context context, FragmentManager fragmentManager) {
        super(context);
        m_NewTaskDialog = new Dialog(context);
        setDialogSettings();
        m_TitleTextView = m_NewTaskDialog.findViewById(R.id.editTextTitle);
        m_FriendEmail = m_NewTaskDialog.findViewById(R.id.editTextTextFriendEmail);
        m_AddFriendCheckBox = m_NewTaskDialog.findViewById(R.id.addFriendCheckBox);
        setOnClickAddFriendCheckBox();
        setOnClickCreateNewTaskButton();
        this.fragmentManager = fragmentManager;
    }
    private void setOnClickAddFriendCheckBox(){
        m_AddFriendCheckBox.setOnCheckedChangeListener((compoundButton, isChecked) -> m_FriendEmail.setEnabled(isChecked));
    }

    private void setOnClickCreateNewTaskButton(){
        Button m_CreateNewTaskButton = m_NewTaskDialog.findViewById(R.id.createNewTask);
        m_CreateNewTaskButton.setOnClickListener(view ->
        {
            if(titleAndEmailValidation()) {
                String dateCreated = dateFormatForDate.format(Calendar.getInstance().getTimeInMillis());
                Task task = new Task(m_TitleTextView.getText().toString(),dateCreated, "1");
                List<Task> newTask = TaskList.getInstance().getTaskList();
                newTask.add(task);
                writeNewTaskToDB();
                Log.d(TAG, "setOnClickCreateNewTaskButton: " + task);
                m_NewTaskDialog.dismiss();
                moveToSubTaskFragment();
            }
        });
    }
    private void moveToSubTaskFragment(){
        FragmentManager fragmentManager = this.fragmentManager;
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.flContent, new SubTaskFragment()).addToBackStack(null).commit();
    }
    private void writeNewTaskToDB(){
        FirebaseUser firebaseUser = m_Auth.getCurrentUser();
        assert firebaseUser != null;
        String uid = firebaseUser.getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference("tasks").child(uid);
        databaseReference.setValue(TaskList.getInstance().getTaskList());

/*        databaseReference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "onDataChange: "+snapshot);
                User user = snapshot.getValue(User.class);
                user.getTasks().add(task);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/
    }
    private boolean titleAndEmailValidation(){
        boolean validationSuccess = true;
        if(m_TitleTextView.getText().toString().isEmpty()) {
            m_TitleTextView.setHintTextColor(Color.RED);
            validationSuccess = false;
        }
        if(m_AddFriendCheckBox.isChecked() && m_FriendEmail.toString().isEmpty() || m_AddFriendCheckBox.isChecked() && !checkEmailExistsOrNot()) {
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

    @Override
    public void updateList() {

    }
}
