package com.example.todolist_java;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
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

/**
 * The main activity for the to-do list app.
 */
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

        // Change the action bar color
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.status_bar_color,null))); // Use getResources() to get the color
            actionBar.setDisplayHomeAsUpEnabled(true); // Enable the system back arrow
        }


        changeStatusColor(); // Change the status bar color
    }

    /**
     * Change the status bar color.
     */
    void changeStatusColor() {
        // Change status bar color
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.status_bar_color,null)); // Change to your desired color resource
    }

    // Lifecycle method: Handling activity result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            // Fetch updated tasks from the database and update the adapter
            tasklist = db.getAllTasks();
            Collections.reverse(tasklist); // Rearrange the list
            adapter.setTasks(tasklist);

            // Exit editing mode for the whole adapter
            adapter.exitEditMode();
        }else if (requestCode == 1 && resultCode == RESULT_CANCELED) {
            // Exit editing mode for the whole adapter
            adapter.exitEditMode();
        }
    }

    // Handle action bar item clicks (Handles the menu)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.deletemenu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.delete) {
            // Handle the "Delete All" menu item click
            showDeleteConfirmationDialog();
            return true;
        }
        if (item.getItemId() == android.R.id.home) {
            onBackPressed(); // Handle the back arrow click
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Show a confirmation dialog before deleting all tasks
    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.deleteall));
        builder.setMessage(getResources().getString(R.string.deleteMessage));
        builder.setPositiveButton(getResources().getString(R.string.delete), (dialog, which) -> {
            // User confirmed deletion, perform the delete operation
            db.deleteAllTasks(); // Delete all tasks from the database
            tasklist.clear(); // Clear the task list
            adapter.notifyDataSetChanged(); // Notify the adapter about the data change
        });
        builder.setNegativeButton(getResources().getString(R.string.cancel), null);
        builder.show();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.closeDatabase();
    }

}

