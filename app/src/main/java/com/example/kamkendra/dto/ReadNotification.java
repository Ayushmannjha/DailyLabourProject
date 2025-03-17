package com.example.kamkendra.dto;

public class ReadNotification {
    private int id;
    private int jobNotificationId;
    private int workerId;
    private int isRead;
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getJobNotificationId() {
        return jobNotificationId;
    }
    public void setJobNotificationId(int jobNotificationId) {
        this.jobNotificationId = jobNotificationId;
    }
    public int getWorkerId() {
        return workerId;
    }
    public void setWorkerId(int workerId) {
        this.workerId = workerId;
    }
    public int getIsRead() {
        return isRead;
    }
    public void setIsRead(int isRead) {
        this.isRead = isRead;
    }
}
