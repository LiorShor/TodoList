package com.example.todolist.view.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.R;
import com.example.todolist.model.Task;
import com.example.todolist.model.TaskMap;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> implements Filterable {
    private final Map<String,Task> m_TaskMap;
    protected List<Task> m_TaskList;
    protected List<Task> m_FilteredTaskList;
    private static final String TAG = "TaskAdapter";
    public TaskAdapter() {
        m_TaskMap = TaskMap.getInstance().getTaskMap();
        List<Task> taskList = new LinkedList<>(m_TaskMap.values());
        this.m_TaskList = taskList ;
        this.m_FilteredTaskList = taskList;
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

    @Override
    public Filter getFilter() {
        return new Filter() {
            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                m_TaskList = (List<Task>) results.values;

                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<Task> filteredResults;
                if (constraint.length() == 0) {
                    filteredResults = m_FilteredTaskList;
                } else {
                    filteredResults = getFilteredResults(constraint.toString().toLowerCase());
                }

                FilterResults results = new FilterResults();
                results.values = filteredResults;

                return results;
            }
        };
    }
    protected List<Task> getFilteredResults(String constraint) {
        List<Task> results = new LinkedList<>();
        for (Task item : m_FilteredTaskList) {
            if (item.getTitle().toLowerCase().contains(constraint)) {
                results.add(item);
            }
        }
        return results;
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
        Task deletedTask = m_TaskList.get(position);
        m_TaskList.remove(deletedTask);
        m_TaskMap.remove(deletedTask.m_ID);
        notifyItemRemoved(position);
        updateDBWithDeletedItem(deletedTask);
    }

    public void updateDBWithDeletedItem(Task deletedTask){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        FirebaseAuth m_Auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = m_Auth.getCurrentUser();
        assert firebaseUser != null;
        Query idQuery = ref.child("tasks").child(firebaseUser.getUid());
        idQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshotToDelete: dataSnapshot.getChildren()) {
                    if(deletedTask.getID().equals(snapshotToDelete.child("m_ID").getValue()))
                    {
                        snapshotToDelete.getRef().removeValue();
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });
    }
    public interface ItemCallBack{
        void updateList();
    }

}
