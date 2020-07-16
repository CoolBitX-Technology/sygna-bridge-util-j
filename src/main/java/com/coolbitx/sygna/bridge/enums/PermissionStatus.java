package com.coolbitx.sygna.bridge.enums;

import com.google.gson.annotations.SerializedName;

public enum PermissionStatus {
    @SerializedName("ACCEPTED")
    ACCEPTED("ACCEPTED"),
    @SerializedName("REJECTED")
    REJECTED("REJECTED");

    private String status;

    PermissionStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }
}
