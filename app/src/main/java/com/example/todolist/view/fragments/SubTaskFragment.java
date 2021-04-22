package com.example.todolist.view.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import com.example.todolist.R;
import com.example.todolist.model.Task;
import com.example.todolist.model.TaskMap;
import com.example.todolist.view.dialogs.NewTask;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * A fragment representing a list of Items.
 */
public class SubTaskFragment extends Fragment implements SubTaskAdapter.ItemCallBack {
    public final static String DATA_RECEIVE = "data_receive";
    TextView m_SubTaskTitle;
    ImageButton m_DeleteTasks;
    CardView m_CardView;
    List<String> subTaskList;
    private static final String ARG_COLUMN_COUNT = "column-count";

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SubTaskFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            getArguments().getInt(ARG_COLUMN_COUNT);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_subtask, container, false);
        m_SubTaskTitle = view.findViewById(R.id.subTaskTitle);
        m_DeleteTasks = view.findViewById(R.id.deleteSubTaskBT);
        m_CardView = view.findViewById(R.id.newTaskCard);
        RecyclerView recyclerView = view.findViewById(R.id.subTaskList);
        Bundle args = getArguments();
        if (args != null) {
            List<Task> taskList = new LinkedList<>(TaskMap.getInstance().getTaskMap().values());
            Task chosenTask = taskList.get(args.getInt(DATA_RECEIVE));
            m_SubTaskTitle.setText(chosenTask.getTitle());
            subTaskList = chosenTask.getItems();
            if(subTaskList== null)
                subTaskList = new LinkedList<>();
            subTaskList.add("Item 1");
            subTaskList.add("Item 2");
            SubTaskAdapter taskAdapter = new SubTaskAdapter(subTaskList,this);
            setUpRecyclerView(recyclerView, taskAdapter);
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
            itemTouchHelper.attachToRecyclerView(recyclerView);
            setOnClickDeleteTaskButton(taskAdapter);
            setOnClickFloatingButton(view);
            setOnClickNewSubTaskButton(view,recyclerView);
        }
        return view;
    }

    private void setOnClickDeleteTaskButton(SubTaskAdapter subTaskAdapter) {
        m_DeleteTasks.setOnClickListener(view1 -> {
            subTaskAdapter.deleteItems();
            updateVisibility(subTaskAdapter);
        });
    }

    private void setUpRecyclerView(RecyclerView recyclerView, SubTaskAdapter taskAdapter) {
        recyclerView.setAdapter(taskAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP |
            ItemTouchHelper.DOWN | ItemTouchHelper.START| ItemTouchHelper.END,0) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();
            Collections.swap(subTaskList,fromPosition,toPosition);
            if(recyclerView.getAdapter()!= null) {
                SubTaskAdapter adapter = (SubTaskAdapter) recyclerView.getAdapter();
                adapter.swapCheckedSubTasksList(fromPosition,toPosition);
                recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);
            }

            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        }
    };

    private void setOnClickFloatingButton(View view) {
        FloatingActionButton floatingButton = view.findViewById(R.id.addTaskFloatingButton);
        floatingButton.setOnClickListener(view1 ->
                m_CardView.setVisibility(View.VISIBLE));
    }

    private void setOnClickNewSubTaskButton(View view,RecyclerView recyclerView){
        ImageButton newSubTaskButton = view.findViewById(R.id.newSubTaskButton);
        TextView newTaskEditText = view.findViewById(R.id.newTaskEditText);
        CardView cardView = view.findViewById(R.id.newTaskCard);
        newSubTaskButton.setOnClickListener(view1 ->
        {
            String newTask = newTaskEditText.getText().toString();
            if(!newTask.isEmpty()) {
                subTaskList.add(newTask);
                recyclerView.getAdapter().notifyItemInserted(subTaskList.size());
                cardView.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void writeNewTaskToDB(Task task){
        FirebaseAuth m_Auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = m_Auth.getCurrentUser();
        assert firebaseUser != null;
        String uid = firebaseUser.getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        Map<String, Object> taskMap = new HashMap<>();
        for (int i = 0; i < task.getItems().size(); i++) {
            taskMap.put(String.valueOf(i),task.getItems().get(i));
        }
        database.getReference("tasks").child(uid).child(task.getID()).updateChildren(taskMap);
    }

    @Override
    public void updateVisibility(SubTaskAdapter subTaskAdapter) {
        List<Integer> checkedSubTasksList= subTaskAdapter.getCheckedSubTasksList();
        if(checkedSubTasksList.size() == 0)
            m_DeleteTasks.setVisibility(View.INVISIBLE);
        else {
            m_DeleteTasks.setVisibility(View.VISIBLE);
            m_CardView.setVisibility(View.INVISIBLE);
        }
    }
}