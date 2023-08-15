package com.example.todolist_java.recyclerview;// Import necessary classes and packages


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.example.todolist_java.AddNewTaskActivity;
import com.example.todolist_java.R;
import com.example.todolist_java.Utils.DatabaseHandler;
import com.example.todolist_java.databinding.TaskLayoutBinding;
import java.util.List;

/**
 * Adapter class for the RecyclerView to display ToDoList items.
 */
public class ToDoListAdapter extends RecyclerView.Adapter<ToDoListAdapter.ViewHolder> {

    private List<ToDoListModel> toDoList; // List to hold ToDoListModel objects
    private DatabaseHandler db; // DatabaseHandler instance to interact with the database
    private Context context; // Context of the activity
    private AppCompatActivity activity; // Reference to the activity

    /**
     * Constructor to initialize the adapter with a DatabaseHandler and an AppCompatActivity.
     *
     * @param db       The DatabaseHandler instance.
     * @param activity The AppCompatActivity.
     */
    public ToDoListAdapter(DatabaseHandler db, AppCompatActivity activity) {
        this.db = db;
        this.activity = activity;
    }


    /**
     * Create a new ViewHolder when needed.
     *
     * @param parent   The parent ViewGroup.
     * @param viewType The view type of the new view.
     * @return A new ViewHolder instance.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the task_layout.xml layout as the item view for the RecyclerView
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_layout, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Bind data to the ViewHolder.
     *
     * @param holder   The ViewHolder to bind data to.
     * @param position The position of the item in the data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        db.openDatabase(); // Open the database
        ToDoListModel item = toDoList.get(position); // Get the ToDoListModel at the specified position
        holder.binding.todoCb.setText(item.getTask()); // Set the task text to the CheckBox
        holder.binding.todoCb.setChecked(toBoolean(item.getStatus())); // Set the CheckBox state based on the task status
        holder.binding.todoTitle.setText(item.getTitle());

        // Set an event listener for CheckBox state changes
        holder.binding.todoCb.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Update the task status in the database based on CheckBox state
            if (isChecked) {
                db.updateStatus(item.getId(), 1);
            } else {
                db.updateStatus(item.getId(), 0);
            }
        });

        // Set an event listener for long click (Edit task)
        holder.binding.getRoot().setOnLongClickListener(v -> {
            editItem(position); // Call the editItem method to edit the task
            return true;
        });
    }

    /**
     * Convert integer to boolean.
     *
     * @param n The integer to convert.
     * @return The boolean value.
     */
    private boolean toBoolean(int n) {
        return n != 0;
    }

    /**
     * Delete a task item at the specified position.
     *
     * @param position The position of the item to be deleted.
     */
    public void deleteItem(int position) {
        ToDoListModel item = toDoList.get(position); // Get the ToDoListModel to be deleted
        db.deleteTask(item.getId()); // Delete the task from the database
        toDoList.remove(position); // Remove the task from the list
        notifyItemRemoved(position); // Notify the adapter about the removal
    }

    /**
     * Set the list of tasks and notify the adapter about the data change.
     *
     * @param toDoList The list of ToDoListModel objects.
     */
    public void setTasks(List<ToDoListModel> toDoList) {
        this.toDoList = toDoList;
        notifyDataSetChanged();
    }

    /**
     * Edit a task item at the specified position.
     *
     * @param position The position of the item to be edited.
     */
    public void editItem(int position) {
        ToDoListModel item = toDoList.get(position); // Get the ToDoListModel to be edited
        Bundle bundle = new Bundle();
        bundle.putInt("id", item.getId());
        bundle.putString("title", item.getTitle());
        bundle.putString("task", item.getTask());
        Intent intent = new Intent(activity, AddNewTaskActivity.class);
        intent.putExtras(bundle); // Attach the task data to the intent
        activity.startActivityForResult(intent, 1); // Start the AddNewTaskActivity for editing
    }


    /**
     * Get the number of items in the list.
     *
     * @return The number of items in the data set.
     */
    @Override
    public int getItemCount() {
        return toDoList.size();
    }

    /**
     * Get the number of items in the list.
     *
     * @return The number of items in the data set.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TaskLayoutBinding binding; // Binding for the task item view

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = TaskLayoutBinding.bind(itemView); // Bind the layout to the view holder
        }
    }

    /**
     * Method to exit edit mode.
     */
    public void exitEditMode() {
        // Update any state or variables related to editing mode
        // Call notifyDataSetChanged() to refresh the adapter's view
        notifyDataSetChanged();
    }
}
