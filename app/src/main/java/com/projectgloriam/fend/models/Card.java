package com.projectgloriam.fend.models;

import com.google.firebase.firestore.DocumentReference;

import java.util.Date;
import java.util.List;

public class Card {
    private String number;
    private String fullName;
    private DocumentReference type;
    private Date issueDate;
    private Date expiryDate;
    private String photo;

    public Card() {}

    public Card(String number, String fullName, DocumentReference type, Date issueDate, Date expiryDate, String photo) {
        this.number = number;
        this.fullName = fullName;
        this.type = type;
        this.issueDate = issueDate;
        this.expiryDate = expiryDate;
        this.photo = photo;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public DocumentReference getType() {
        return type;
    }

    public void setType(DocumentReference type) {
        this.type = type;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
