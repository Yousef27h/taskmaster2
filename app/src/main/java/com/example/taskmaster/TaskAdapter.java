package com.example.taskmaster;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amplifyframework.datastore.generated.model.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    private OnTaskListener listener;
    private List<Task> tasks;

    public TaskAdapter( List<Task> tasks, OnTaskListener listener) {
        this.listener = listener;
        this.tasks = tasks;
    }

    public interface OnTaskListener{
        void onItemClick(int position);
        void onDeleteClick(int position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_layout,parent,false);
        return new ViewHolder(itemView, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String title = tasks.get(position).getTitle();
//        String description = tasks.get(position).getBody();
//        String state = tasks.get(position).getState();

        holder.taskTitle.setText(title);
//        holder.taskState.setText(state);
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView taskTitle;
        private TextView taskState;
        private Button deleteBtn;

        public ViewHolder(@NonNull View itemView, OnTaskListener listener) {
            super(itemView);
            taskTitle = itemView.findViewById(R.id.titleTask);
//            taskState = itemView.findViewById(R.id.stateTask);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                        listener.onItemClick(getAdapterPosition());
                }
            });

            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onDeleteClick(getAdapterPosition());
                }
            });

        }
    }
}
