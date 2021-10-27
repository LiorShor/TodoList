package com.example.todolist.view.fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.todolist.R;
import java.util.LinkedList;
import java.util.List;

public class SubTaskAdapter  extends RecyclerView.Adapter<SubTaskAdapter.ViewHolder> {
    private final List<String> m_SubTaskList;
    private final List<Integer> m_CheckedSubTasksList;
    private final ItemCallBack itemCallback;
    public SubTaskAdapter(List<String> m_SubTaskList,ItemCallBack itemCallback) {
        this.m_SubTaskList = m_SubTaskList;
        this.m_CheckedSubTasksList = new LinkedList<>();
        this.itemCallback = itemCallback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.subtask_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.m_TaskDoneCheckBox.setText(m_SubTaskList.get(holder.getAdapterPosition()));
        holder.m_TaskDoneCheckBox.setOnCheckedChangeListener((compoundButton, isChecked) ->
        {
            if(isChecked)
                m_CheckedSubTasksList.add(holder.getAdapterPosition());
            else
                m_CheckedSubTasksList.remove(m_CheckedSubTasksList.indexOf(holder.getAdapterPosition()));
            itemCallback.updateVisibility(this);
        });
    }

    public List<Integer> getCheckedSubTasksList() {
        return m_CheckedSubTasksList;
    }

    public void swapCheckedSubTasksList(int from,int to) {

        for (int i = 0; i < m_CheckedSubTasksList.size(); i++) {
            if(m_CheckedSubTasksList.get(i) == from)
                m_CheckedSubTasksList.set(i,to);
            else if(m_CheckedSubTasksList.get(i) == to)
                m_CheckedSubTasksList.set(i,from);
        }
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

    public void deleteItems() {
        for (Integer position: m_CheckedSubTasksList) {
            m_SubTaskList.remove(m_SubTaskList.get(position));
            notifyItemRemoved(position);
        }
        m_CheckedSubTasksList.clear();
    }
    public interface ItemCallBack{
        void updateVisibility(SubTaskAdapter subTaskAdapter);
    }
}
