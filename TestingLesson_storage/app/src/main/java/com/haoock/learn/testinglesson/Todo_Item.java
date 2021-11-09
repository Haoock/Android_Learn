package com.haoock.learn.testinglesson;

public class Todo_Item {
    private int id;
    private String content;
    private String date;
    private String state;

    public Todo_Item(int id, String content, String date, String state) {
        this.id = id;
        this.content = content;
        this.date = date;
        this.state = state;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
