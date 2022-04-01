package com.projectgloriam.fend.models;

import android.net.Uri;

public class User {
    String name;
    String email;
    Uri photoUrl;
    boolean emailVerified;
    String uid;

    public User(String name, String email, Uri photoUrl, boolean emailVerified, String uid) {
        this.name = name;
        this.email = email;
        this.photoUrl = photoUrl;
        this.emailVerified = emailVerified;
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Uri getPhotoUrl() {
        return photoUrl;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public String getUid() {
        return uid;
    }
}
