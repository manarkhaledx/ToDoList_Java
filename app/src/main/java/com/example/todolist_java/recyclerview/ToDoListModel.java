package com.example.todolist_java.recyclerview;

public class ToDoListModel {
    private int id,status; //id of task , for the check button
    private String task; //written tasks
    public ToDoListModel() {
        // Empty constructor
    }
    public ToDoListModel(String task, int status) {
        this.task = task;
        this.status = status;
    }

    public ToDoListModel(String task) {
        this.task = task;
        // Initialize other fields with default values if needed
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }
}
