package com.example.todolist.view.fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.todolist.R;

import java.util.List;

public class SubTaskAdapter  extends RecyclerView.Adapter<SubTaskAdapter.ViewHolder> {
    private final List<String> m_SubTaskList;

    public SubTaskAdapter(List<String> m_SubTaskList) {
        this.m_SubTaskList = m_SubTaskList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.subtask_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.m_TaskDoneCheckBox.setText(m_SubTaskList.get(position));
    }

    @Override
    public int getItemCount() {
        return m_SubTaskList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final CheckBox m_TaskDoneCheckBox;

        public ViewHolder(View itemView) {
            super(itemView);
            this.m_TaskDoneCheckBox = itemView.findViewById(R.id.taskCheckBox);
        }
    }
}
