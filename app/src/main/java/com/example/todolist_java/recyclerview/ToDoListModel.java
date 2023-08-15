package com.example.todolist_java.recyclerview;

/**
 * Model class representing a ToDoList item.
 */
public class ToDoListModel {
    private int id,status; //id of task , for the check button
    private String task,title; //written tasks
    public ToDoListModel() {
        // Empty constructor
    }
    public ToDoListModel(String task,String title, int status) {
        this.task = task;
        this.title = title;
        this.status = status;

    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
