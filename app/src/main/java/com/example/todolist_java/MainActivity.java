package com.example.todolist_java;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.example.todolist_java.Utils.DatabaseHandler;
import com.example.todolist_java.databinding.ActivityMainBinding;
import com.example.todolist_java.recyclerview.ToDoListAdapter;
import com.example.todolist_java.recyclerview.ToDoListModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// The main activity for the to-do list app
public class MainActivity extends AppCompatActivity {

    // List to hold tasks
    private List<ToDoListModel> tasklist = new ArrayList<>();

    // ViewBinding for the activity layout
    ActivityMainBinding binding;

    // RecyclerView to display tasks
    RecyclerView recyclerView;

    // Adapter for the RecyclerView
    ToDoListAdapter adapter;

    // Database handler
    private DatabaseHandler db;

    // Lifecycle method: Activity creation
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Use ViewBinding to inflate the activity layout
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        recyclerView = binding.recyclerView;
        setContentView(binding.getRoot());

        // Initialize the database handler
        db = new DatabaseHandler(this);
        db.openDatabase();

        // Initialize the adapter
        adapter = new ToDoListAdapter(db, this);

        // Set the adapter on the RecyclerView
        recyclerView.setAdapter(adapter);

        // Fetch tasks from the database and update the adapter
        tasklist = db.getAllTasks();
        Collections.reverse(tasklist); // Reverse the list to display newest tasks first
        adapter.setTasks(tasklist);

        // Set up FloatingActionButton click listener
        binding.fab.setOnClickListener(v -> {
            // Open the AddNewTaskActivity to add a new task
            Intent intent = new Intent(MainActivity.this, AddNewTaskActivity.class);
            startActivityForResult(intent, 1); // Use startActivityForResult to handle result
        });

        // Set up item touch helper for swipe actions
        RecyclerItemTouchHelper itemTouchHelper = new RecyclerItemTouchHelper(adapter, this);
        new ItemTouchHelper(itemTouchHelper).attachToRecyclerView(recyclerView);
    }

    // Lifecycle method: Handling activity result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            // Fetch updated tasks from the database and update the adapter
            tasklist = db.getAllTasks();
            Collections.reverse(tasklist); // Reverse the list
            adapter.setTasks(tasklist);

            // Optional: Scroll to the edited item to make sure it's visible
            int editedPosition = data.getIntExtra("edited_position", -1);
            if (editedPosition != -1) {
                // Notify the adapter that the item at the edited position has changed
                adapter.notifyItemChanged(editedPosition);
            }

            // Exit editing mode for the whole adapter
            adapter.exitEditMode();
        }
    }
}
