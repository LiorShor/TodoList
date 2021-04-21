package com.example.todolist.view.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.example.todolist.R;
import com.example.todolist.model.Task;
import com.example.todolist.model.TaskMap;
import com.example.todolist.view.adapters.TaskAdapter;
import com.example.todolist.view.fragments.SubTaskFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;


public class NewTask extends TaskFragment implements TaskAdapter.ItemCallBack {
    private final Dialog m_NewTaskDialog;
    private final TextView m_TitleTextView;
    private final TextView m_FriendEmail;
    private final CheckBox m_AddFriendCheckBox;
    private final ImageView m_TaskImage;
    private boolean isUserExist = false;
    private final FragmentManager fragmentManager;
    private static final FirebaseAuth m_Auth = FirebaseAuth.getInstance();
    private static final String TAG = "NewTask";
    private final SimpleDateFormat dateFormatForDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    public NewTask(@NonNull Context context, FragmentManager fragmentManager) {
        m_NewTaskDialog = new Dialog(context);
        setDialogSettings();
        m_TitleTextView = m_NewTaskDialog.findViewById(R.id.editTextTitle);
        m_FriendEmail = m_NewTaskDialog.findViewById(R.id.editTextTextFriendEmail);
        m_AddFriendCheckBox = m_NewTaskDialog.findViewById(R.id.addFriendCheckBox);
        m_TaskImage = m_NewTaskDialog.findViewById(R.id.uploadImage);
        setOnClickAddFriendCheckBox();
        setOnClickCreateNewTaskButton();
        setOnClickImage();
        this.fragmentManager = fragmentManager;
    }

    private void setOnClickImage() {
        m_TaskImage.setOnClickListener(view -> openGallery());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==0 && resultCode == Activity.RESULT_OK){
            try {
                Bundle bundle = data.getExtras();
                Bitmap bitmap = bundle.getParcelable("data");
                m_TaskImage.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public void openGallery(){
        Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // ******** code for crop image
        i.putExtra("crop", "true");
        i.putExtra("aspectX", 100);
        i.putExtra("aspectY", 100);
        i.putExtra("outputX", 256);
        i.putExtra("outputY", 356);

        try {

            i.putExtra("return-data", true);
            Objects.requireNonNull(getActivity()).startActivityForResult(Intent.createChooser(i, "Select Picture"), 0);

        }catch (ActivityNotFoundException ex){
            ex.printStackTrace();
        }
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
                Task task = new Task(m_TitleTextView.getText().toString(),dateCreated, UUID.randomUUID().toString());
                Map<String,Task> newTask = TaskMap.getInstance().getTaskMap();
                newTask.put(task.getID(),task);
                writeNewTaskToDB(task);
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
  
    private void writeNewTaskToDB(Task task){
        FirebaseUser firebaseUser = m_Auth.getCurrentUser();
        assert firebaseUser != null;
        String uid = firebaseUser.getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference("tasks").child(uid).child(task.getID());
        databaseReference.setValue(task);
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

    private void setDialogSettings() {
        m_NewTaskDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        m_NewTaskDialog.setContentView(R.layout.dialog_new_task);
        m_NewTaskDialog.show();
        m_NewTaskDialog.setCanceledOnTouchOutside(true);
    }

    @Override
    public void updateList() {

    }
}
