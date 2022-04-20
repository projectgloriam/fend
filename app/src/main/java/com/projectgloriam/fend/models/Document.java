package com.projectgloriam.fend.models;

public class Document {
    private String name;
    private String photo;
    private String uid;

    public Document() {
    }

    public Document(String name, String photo, String uid) {
        this.name = name;
        this.photo = photo;
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getUid() {
        return uid;
    }
}
