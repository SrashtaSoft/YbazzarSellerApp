package com.app.ybazzarseller.model;

public class OrderStatus {
    String displayName, statusName;

    public OrderStatus(String statusName, String displayName) {
        this.statusName = statusName;
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        statusName = statusName;
    }
}
