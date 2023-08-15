package com.example.todolist_java;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import com.example.todolist_java.Utils.DatabaseHandler;
import com.example.todolist_java.databinding.ActivityAddNewTaskBinding;
import com.example.todolist_java.recyclerview.ToDoListModel;


/**
 * Activity to add or edit tasks.
 */
public class AddNewTaskActivity extends AppCompatActivity {

    // Binding for the activity layout
    ActivityAddNewTaskBinding binding;

    // DatabaseHandler instance to manage the database
    private DatabaseHandler db;

    // Flags to track whether the activity is in update mode and task ID to update
    private boolean isUpdate = false;
    private int taskIdToUpdate = -1;


    // Lifecycle method: Activity creation
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate the activity layout using binding
        binding = ActivityAddNewTaskBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize the database handler
        db = new DatabaseHandler(this);
        db.openDatabase();

        // Get task data from intent extras
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            // Check if the activity is in update mode
            taskIdToUpdate = bundle.getInt("id", -1);
            isUpdate = (taskIdToUpdate != -1);

            if (isUpdate) {
                // If in update mode, set the task text and update button state
                String task = bundle.getString("task");
                String title = bundle.getString("title");
                binding.newTaskEt.setText(task);
                binding.titleEt.setText(title);
                updateButtonState(task);
            }
        }
        binding.cancelBtn.setOnClickListener(v -> {
            // Handle cancel action
            setResult(RESULT_CANCELED);
            finish();
        });

        // Button click listener for adding/editing tasks
        binding.newTaskBtn.setOnClickListener(v -> {
            // Get task text from EditText view
            String task = binding.newTaskEt.getText().toString().trim();
            String title = binding.titleEt.getText().toString().trim();

            if (TextUtils.isEmpty(task) || TextUtils.isEmpty(title)) {
                // Display a toast if the task is empty
                Toast.makeText(AddNewTaskActivity.this, getResources().getString(R.string.enterTask), Toast.LENGTH_SHORT).show();
            } else {
                // Start the animation
                binding.newTaskBtn.startAnimation();

                if (isUpdate) {
                    // Handle task update
                    if (taskIdToUpdate != -1) {
                        // Update the task in the database
                        db.updateTask(taskIdToUpdate, task, title);
                        Toast.makeText(AddNewTaskActivity.this,  getResources().getString(R.string.updateTask), Toast.LENGTH_SHORT).show();

                        // Pass the result back to MainActivity
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("edited_position", taskIdToUpdate);
                        setResult(RESULT_OK, resultIntent);

                        // Introduce a 1-second delay before navigating and closing the activity
                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                            // Stop the animation
                            binding.newTaskBtn.revertAnimation();

                            // Close the activity
                            finish();
                        }, 1000); // Delay of 1 second
                    }
                } else {
                    // Handle task creation
                    db.insertTask(new ToDoListModel(task,title, 0)); // Assume status is initially set to 0
                    Toast.makeText(AddNewTaskActivity.this, getResources().getString(R.string.taskCreated), Toast.LENGTH_SHORT).show();

                    // Pass the result back to MainActivity
                    Intent resultIntent = new Intent();
                    setResult(RESULT_OK, resultIntent);

                    // Introduce a 1-second delay before navigating and closing the activity
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        // Stop the animation
                        binding.newTaskBtn.revertAnimation();

                        // Close the activity
                        finish();
                    }, 1000); // Delay of 1 second
                }
            }
        });



        /**
         * Change the status bar color.
         */
        changeStatusColor();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#77D7EF")));
        }
    }
    void changeStatusColor() {
        // Change status bar color
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.status_bar_color,null)); // Change to your desired color resource
    }
    /**
     * Update the state of the button based on the task text.
     *
     * @param task The task text to update the button state.
     */
    private void updateButtonState(String task) {
        boolean isValidTask = !TextUtils.isEmpty(task);
        binding.newTaskBtn.setEnabled(isValidTask);
        binding.newTaskBtn.setTextColor(getResources().getColor(isValidTask ? R.color.status_bar_color : R.color.g_background_facebook,null));
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.closeDatabase();
    }
}
