package com.example.todolist.view.dialogs;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.todolist.R;
import com.example.todolist.databinding.DialogNewTaskBinding;
import com.example.todolist.model.Task;
import com.example.todolist.model.TaskMap;
import com.example.todolist.view.adapters.TaskAdapter;
import com.example.todolist.viewmodel.DataLoadListener;
import com.example.todolist.viewmodel.NewTaskViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;


public class NewTask extends DialogFragment implements TaskAdapter.ItemCallBack {
    private NewTaskViewModel mNewTaskViewModel;
    private DialogNewTaskBinding mNewTaskDialogBinding;
    private boolean isUserExist = false;
    private static final FirebaseAuth m_Auth = FirebaseAuth.getInstance();
    private static final String TAG = "NewTask";
    private final SimpleDateFormat dateFormatForDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    try {
                        if (result.getData() != null) {
                            Uri selectedImageUri = result.getData().getData();
                            mNewTaskDialogBinding.taskImageView.setImageURI(selectedImageUri);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

    public static NewTask newInstance(){
        return new NewTask();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mNewTaskDialogBinding = DialogNewTaskBinding.inflate(LayoutInflater.from(getContext()));
        getDialog().getWindow().setBackgroundDrawableResource(R.drawable.rounded_dialog);
        return mNewTaskDialogBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mNewTaskViewModel = ViewModelProviders.of(getActivity()).get(NewTaskViewModel.class);
        setOnClickAddFriendCheckBox();
        setOnClickCreateNewTaskButton();
        setOnClickImage();
    }

    private void setOnClickImage() {
        mNewTaskDialogBinding.taskImageView.setOnClickListener(view -> openGallery());
    }

    public void openGallery() {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        someActivityResultLauncher.launch(i);
    }

    private void setOnClickAddFriendCheckBox() {
        mNewTaskDialogBinding.addFriendCheckBox.setOnCheckedChangeListener((compoundButton, isChecked) ->
                mNewTaskDialogBinding.editTextTextFriendEmail.setEnabled(isChecked));
    }

    private void setOnClickCreateNewTaskButton() {
        mNewTaskDialogBinding.createNewTask.setOnClickListener(view ->
        {
            if (titleAndEmailValidation()) {
                String dateCreated = dateFormatForDate.format(Calendar.getInstance().getTimeInMillis());
                Task task = new Task(dateCreated, UUID.randomUUID().toString(),mNewTaskDialogBinding.editTextTitle.getText().toString());
                Map<String, Task> newTask = TaskMap.getInstance().getTaskMap();
                newTask.put(task.getID(), task);
                mNewTaskViewModel.init(this);
                mNewTaskViewModel.writeNewTaskToDatabase(task);
                Log.d(TAG, "setOnClickCreateNewTaskButton: " + task);
                this.dismiss();
            }
        });
    }

    private boolean titleAndEmailValidation() {
        boolean validationSuccess = true;
        if (mNewTaskDialogBinding.editTextTitle.getText().toString().isEmpty()) {
            mNewTaskDialogBinding.editTextTitle.setHintTextColor(Color.RED);
            validationSuccess = false;
        }
        if (mNewTaskDialogBinding.addFriendCheckBox.isChecked() &&
                mNewTaskDialogBinding.editTextTextFriendEmail.toString().isEmpty() ||
                mNewTaskDialogBinding.addFriendCheckBox.isChecked() && !checkEmailExistsOrNot()) {
            mNewTaskDialogBinding.editTextTextFriendEmail.setHintTextColor(Color.RED);
            mNewTaskDialogBinding.editTextTextFriendEmail.setTextColor(Color.RED);
            validationSuccess = false;
        }
        if (checkEmailExistsOrNot()) {
            mNewTaskDialogBinding.editTextTextFriendEmail.setHintTextColor(Color.BLACK);
            mNewTaskDialogBinding.editTextTextFriendEmail.setTextColor(Color.BLACK);
        }
        return validationSuccess;
    }

    private boolean checkEmailExistsOrNot() {
        String friendEmail = mNewTaskDialogBinding.editTextTextFriendEmail.getText().toString();
        if (!friendEmail.isEmpty()) {
            Thread thread = new Thread(() -> m_Auth.fetchSignInMethodsForEmail(friendEmail).addOnCompleteListener(task -> {
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

    @Override
    public void updateList() {

    }
}
