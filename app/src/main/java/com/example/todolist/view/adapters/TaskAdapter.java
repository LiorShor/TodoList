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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> implements Filterable {
    private  Map<String,Task> mTaskMap;
    protected List<Task> mTaskList;
    protected List<Task> mFilteredTaskList;


    private static final String TAG = "TaskAdapter";
    public TaskAdapter(Map<String,Task> taskMap) {
        mTaskMap = taskMap;
        mTaskList = new LinkedList<>();
        this.mFilteredTaskList = mTaskList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.m_TaskTitleTextView.setText(mTaskList.get(position).getTitle());
        holder.m_TaskDateOfCreationTextView.setText(mTaskList.get(position).getDateCreated());
    }

    @Override
    public int getItemCount() {
        return mTaskList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mTaskList = (List<Task>) results.values;
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<Task> filteredResults;
                if (constraint.length() == 0) {
                    filteredResults = mFilteredTaskList;
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
        for (Task item : mFilteredTaskList) {
            if (item.getTitle().toLowerCase().contains(constraint)) {
                results.add(item);
            }
        }
        return results;
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView m_TaskTitleTextView;
        private final TextView m_TaskDateOfCreationTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            m_TaskTitleTextView = itemView.findViewById(R.id.titleTextView);
            m_TaskDateOfCreationTextView = itemView.findViewById(R.id.dateTextView);

        }
    }
    public void deleteItem(int position) {
        Task deletedTask = mTaskList.get(position);
        mTaskList.remove(deletedTask);
        mTaskMap.remove(deletedTask.getID());
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

    public void setTasks(Map<String,Task> taskMap){
        this.mTaskMap = taskMap;
        mTaskList.addAll(mTaskMap.values());
        notifyDataSetChanged();
    }
    public interface ItemCallBack{
        void updateList();
    }
}
