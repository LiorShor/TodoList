package com.example.todolist.view.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.todolist.R;
import com.example.todolist.view.adapters.SwipeToDeleteCallback;
import com.example.todolist.view.adapters.TaskAdapter;
import com.example.todolist.view.dialogs.NewTask;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class TaskFragment extends Fragment implements TaskAdapter.ItemCallBack {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private TaskAdapter taskAdapter;
    DataPassListener mCallback;
    public interface DataPassListener{
        void passData(int data);
    }
    public TaskFragment() {
        // Required empty public constructor
    }
    @Override
    public void onAttach(@NonNull Context context)
    {
        super.onAttach(context);
        // This makes sure that the host activity has implemented the callback interface
        // If not, it throws an exception
        try
        {
            mCallback = (DataPassListener) context;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(context.toString()+ " must implement OnImageClickListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            getArguments().getString(ARG_PARAM1);
            getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task, container, false);
        FloatingActionButton floatingButton = view.findViewById(R.id.floatingActionButton);
        floatingButton.setOnClickListener(view1 ->
        {
            if(getContext()!=null)
                new NewTask(getContext(),getFragmentManager());
        });
        taskAdapter = new TaskAdapter();
        RecyclerView recyclerView = view.findViewById(R.id.taskList);
        setUpRecyclerView(recyclerView);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        mCallback.passData(position);
                        Toast.makeText(getActivity(), "HEhe", Toast.LENGTH_SHORT).show();
                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );
        SearchView searchView = view.findViewById(R.id.taskSearch);
        searchView.setOnClickListener(v -> searchView.setIconified(false));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String text) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String text) {
                taskAdapter.getFilter().filter(text);
                return true;
            }
        });
        return view;
    }
    private void setUpRecyclerView(RecyclerView recyclerView) {
        recyclerView.setAdapter(taskAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new SwipeToDeleteCallback(taskAdapter,getContext()));
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void updateList() {
        taskAdapter.notifyDataSetChanged();
    }
}