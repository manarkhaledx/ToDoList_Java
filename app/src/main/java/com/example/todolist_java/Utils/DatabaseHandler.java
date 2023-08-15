package com.example.todolist_java.Utils;// Import necessary classes and packages


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

import com.example.todolist_java.recyclerview.ToDoListModel;

import java.util.ArrayList;
import java.util.List;

// DatabaseHandler class to manage SQLite database
public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String NAME = "toDoListDatabase";
    private static final String TODO_TABLE = "todo";
    private static final String ID = "id";
    private static final String TASK = "task";

    private static final String TITLE = "title";
    private static final String STATUS = "status";
    private static final String CREATE_TODO_TABLE = "CREATE TABLE " + TODO_TABLE + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TASK + " TEXT, "
            + TITLE + " TEXT, "
            + STATUS + " INTEGER)";

    private SQLiteDatabase db;

    // Constructor to create the database with the given context
    public DatabaseHandler(@Nullable Context context) {
        super(context, NAME, null, VERSION);
    }

    // Create the database table
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TODO_TABLE);
    }

    // Handle database upgrades (dropping and recreating the table)
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop the older table
        db.execSQL("DROP TABLE IF EXISTS " + TODO_TABLE);
        // Create the table again
        onCreate(db);
    }

    // Open the database for writing
    public void openDatabase() {
        db = this.getWritableDatabase();
    }

    // Close the database
    public void closeDatabase() {
        if (db != null && db.isOpen()) {
            db.close();
        }
    }


    // Insert a new task into the database
    public void insertTask(ToDoListModel task) {
        ContentValues cv = new ContentValues();
        cv.put(TASK, task.getTask());
        cv.put(TITLE, task.getTitle());
        cv.put(STATUS, task.getStatus());
        db.insert(TODO_TABLE, null, cv);
    }

    // Retrieve all tasks from the database
    public List<ToDoListModel> getAllTasks() {
        List<ToDoListModel> taskList = new ArrayList<>();
        db.beginTransaction();
        try (Cursor cur = db.query(TODO_TABLE, null, null, null, null, null, null, null)) {
            // Query all rows from the todo table
            if (cur != null) {
                if (cur.moveToFirst()) {
                    int idIndex = cur.getColumnIndex(ID);
                    int taskIndex = cur.getColumnIndex(TASK);
                    int titleIndex = cur.getColumnIndex(TITLE);
                    int statusIndex = cur.getColumnIndex(STATUS);
                    do {
                        // Create a ToDoListModel object from the retrieved data
                        ToDoListModel task = new ToDoListModel();
                        task.setId(cur.getInt(idIndex));
                        task.setTask(cur.getString(taskIndex));
                        task.setTitle(cur.getString(titleIndex));
                        task.setStatus(cur.getInt(statusIndex));
                        taskList.add(task); // Add the task to the list
                    } while (cur.moveToNext());
                }
            }
        } finally {
            db.endTransaction(); // End the transaction
            // Close the cursor
        }
        return taskList;
    }

    // Update a task in the database
    public void updateTask(int id, String task,String title) {
        ContentValues cv = new ContentValues();
        cv.put(TASK, task);
        cv.put(TITLE, title);
        // Update the task based on its ID
        db.update(TODO_TABLE, cv, ID + "=?", new String[]{String.valueOf(id)});
    }

    // Update the status of a task in the database
    public void updateStatus(int id, int status) {
        ContentValues cv = new ContentValues();
        cv.put(STATUS, status);
        // Update the status based on the task's ID
        db.update(TODO_TABLE, cv, ID + "=?", new String[]{String.valueOf(id)});
    }

    // Delete a task from the database
    public void deleteTask(int id) {
        // Delete the task based on its ID
        db.delete(TODO_TABLE, ID + "=?", new String[]{String.valueOf(id)});
    }
    public void deleteAllTasks() {
        db.delete(TODO_TABLE, null, null);
    }
}
