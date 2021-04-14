package com.example.todolist.view.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.todolist.R;
import com.example.todolist.model.Task;
import com.example.todolist.model.TaskList;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {
    private final List<Task> m_TaskList;
    public TaskAdapter() {
        this.m_TaskList = TaskList.getInstance().getTaskList();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.m_TaskTitleTextView.setText(m_TaskList.get(position).getTitle());
        holder.m_TaskDateOfCreationTextView.setText(m_TaskList.get(position).getDateCreated());
    }

    @Override
    public int getItemCount() {
        return m_TaskList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView m_TaskTitleTextView;
        private final TextView m_TaskDateOfCreationTextView;
        public ViewHolder(View itemView) {
            super(itemView);
            m_TaskTitleTextView = itemView.findViewById(R.id.titleTextView);
            m_TaskDateOfCreationTextView = itemView.findViewById(R.id.dateTextView);

        }
    }
    public void deleteItem(int position) {
        m_TaskList.remove(position);
        notifyItemRemoved(position);
//        showUndoSnackbar();
    }
/*
    private void showUndoSnackbar() {
        View view = mActivity.findViewById(R.id.coordinator_layout);
        Snackbar snackbar = Snackbar.make(view, R.string.snack_bar_text,
                Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.snack_bar_undo, v -> undoDelete());
        snackbar.show();
    }

    private void undoDelete() {
        mListItems.add(mRecentlyDeletedItemPosition,
                mRecentlyDeletedItem);
        notifyItemInserted(mRecentlyDeletedItemPosition);
    }
*/

    public interface ItemCallBack{
        void updateList();
    }

}
