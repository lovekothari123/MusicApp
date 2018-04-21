package com.djgeraldo.data;

/**
 * Created by sachin on 10-01-2018.
 */

public class notification {
    int notificationId;
    String msg;
    String createdAt;

    public notification(int notificationId, String msg, String createdAt) {
        this.notificationId = notificationId;
        this.msg = msg;
        this.createdAt = createdAt;
    }

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
