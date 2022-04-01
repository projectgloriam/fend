package com.projectgloriam.fend.models;

public class Menu {
    private Integer title;
    private Integer description;
    private Integer action;

    public Menu(Integer title, Integer description, Integer action){
        this.title = title;
        this.description = description;
        this.action = action;
    }

    public Integer getTitle() {
        return title;
    }

    public Integer getDescription() {
        return description;
    }

    public Integer getAction() {
        return action;
    }
}
