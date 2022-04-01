package com.projectgloriam.fend.models;

public class UserPreferences {
    String uid;
    boolean expiryReminder;
    String emergencyEmail;
    String emergencyTelephone;
    boolean notify;

    public UserPreferences(String uid, boolean expiryReminder, String emergencyEmail, String emergencyTelephone, boolean notify) {
        this.uid = uid;
        this.expiryReminder = expiryReminder;
        this.emergencyEmail = emergencyEmail;
        this.emergencyTelephone = emergencyTelephone;
        this.notify = notify;
    }

    public String getUid() {
        return uid;
    }

    public boolean isExpiryReminder() {
        return expiryReminder;
    }

    public String getEmergencyEmail() {
        return emergencyEmail;
    }

    public String getEmergencyTelephone() {
        return emergencyTelephone;
    }

    public boolean isNotify() {
        return notify;
    }

    public void setExpiryReminder(boolean expiryReminder) {
        this.expiryReminder = expiryReminder;
    }

    public void setEmergencyEmail(String emergencyEmail) {
        this.emergencyEmail = emergencyEmail;
    }

    public void setEmergencyTelephone(String emergencyTelephone) {
        this.emergencyTelephone = emergencyTelephone;
    }

    public void setNotify(boolean notify) {
        this.notify = notify;
    }

    public void update(boolean expiryReminder, String emergencyEmail, String emergencyTelephone, boolean notify){
        setExpiryReminder(expiryReminder);
        setEmergencyEmail(emergencyEmail);
        setEmergencyTelephone(emergencyTelephone);
        setNotify(notify);
    }
}
