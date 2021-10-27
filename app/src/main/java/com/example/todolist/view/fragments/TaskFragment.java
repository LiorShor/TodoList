package com.example.todolist.view.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.todolist.databinding.FragmentTaskBinding;
import com.example.todolist.view.ICommunicator;
import com.example.todolist.view.adapters.SwipeToDeleteCallback;
import com.example.todolist.view.adapters.TaskAdapter;
import com.example.todolist.viewmodel.DataLoadListener;
import com.example.todolist.viewmodel.NewTaskViewModel;

public class TaskFragment extends Fragment implements DataLoadListener {
    private TaskAdapter taskAdapter;
    private FragmentTaskBinding mTaskFragmentBinding;
    private NewTaskViewModel mNewTaskViewModel;
    private final ICommunicator communicator;
    public TaskFragment(ICommunicator communicator){
        this.communicator = communicator;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setOnClickFloatingButton();
        setUpRecyclerView();
        searchBarConfiguration();

    }

    private void searchBarConfiguration() {
        mTaskFragmentBinding.taskSearchView.setOnClickListener(taskSearchView -> mTaskFragmentBinding.taskSearchView.setIconified(false));
        mTaskFragmentBinding.taskSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mTaskFragmentBinding = FragmentTaskBinding.inflate(inflater, container, false);
        mNewTaskViewModel = ViewModelProviders.of(this).get(NewTaskViewModel.class);
        mNewTaskViewModel.init((DataLoadListener)this);
        return mTaskFragmentBinding.getRoot();

/*        mTaskFragmentBinding.taskListRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), mTaskFragmentBinding.taskListRecyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        mCallback.passData(position);
                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );*/
    }

    private void setOnClickFloatingButton() {
        mTaskFragmentBinding.floatingActionButton.setOnClickListener(view ->
        {
            if(getContext()!=null) {
                communicator.loadNewTask();
            }
        });
    }

    private void setUpRecyclerView() {
        taskAdapter = new TaskAdapter(mNewTaskViewModel.getTaskMap().getValue());
        mTaskFragmentBinding.taskListRecyclerView.setAdapter(taskAdapter);
        mTaskFragmentBinding.taskListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new SwipeToDeleteCallback(taskAdapter,getContext()));
        itemTouchHelper.attachToRecyclerView(mTaskFragmentBinding.taskListRecyclerView);
    }

    @Override
    public void onTaskLoaded() {
        mNewTaskViewModel.getTaskMap().observe(getViewLifecycleOwner(), stringTaskMap ->
        {
            if(stringTaskMap != null)
            {
                taskAdapter.setTasks(stringTaskMap);
            }
//            taskAdapter.notifyDataSetChanged();
        });
    }
}