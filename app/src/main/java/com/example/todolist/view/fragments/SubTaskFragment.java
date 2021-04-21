package com.example.todolist.view.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.todolist.R;
import com.example.todolist.model.Task;
import com.example.todolist.model.TaskMap;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 */
public class SubTaskFragment extends Fragment {
    public final static String DATA_RECEIVE = "data_receive";
    TextView m_SubTaskTitle;
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
        RecyclerView recyclerView = view.findViewById(R.id.subTaskList);
        Bundle args = getArguments();
        if (args != null) {
            List<Task> taskList = new LinkedList<>(TaskMap.getInstance().getTaskMap().values());
            Task chosenTask = taskList.get(args.getInt(DATA_RECEIVE));
            m_SubTaskTitle.setText(chosenTask.getTitle());
            subTaskList = chosenTask.getItems();
            if(subTaskList== null)
                subTaskList = new LinkedList<>();
            subTaskList.add("Hello :)");
            subTaskList.add("Hello2");
            SubTaskAdapter taskAdapter = new SubTaskAdapter(subTaskList);
            setUpRecyclerView(recyclerView, taskAdapter);
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
            itemTouchHelper.attachToRecyclerView(recyclerView);
        }
        return view;
       /* // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(new StringAdapter(DummyContent.ITEMS));
        }*/
    }

    private void setUpRecyclerView(RecyclerView recyclerView, SubTaskAdapter taskAdapter) {
        recyclerView.setAdapter(taskAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
/*        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new SwipeToDeleteCallback(taskAdapter, getContext()));
        itemTouchHelper.attachToRecyclerView(recyclerView);*/
    }
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP |
            ItemTouchHelper.DOWN | ItemTouchHelper.START| ItemTouchHelper.END,0) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();
            Collections.swap(subTaskList,fromPosition,toPosition);
            if(recyclerView.getAdapter()!= null)
                recyclerView.getAdapter().notifyItemMoved(fromPosition,toPosition);
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        }
    };
}