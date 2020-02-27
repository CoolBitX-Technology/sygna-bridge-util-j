package com.coolbitx.sygna.bridge.enums;

import com.google.gson.annotations.SerializedName;

public enum RejectCode {
    @SerializedName("BVRC001")
    BVRC001("BVRC001"),
    @SerializedName("BVRC002")
    BVRC002("BVRC002"),
    @SerializedName("BVRC003")
    BVRC003("BVRC003"),
    @SerializedName("BVRC004")
    BVRC004("BVRC004"),
    @SerializedName("BVRC999")
    BVRC999("BVRC999"), NULL(null);

    private String rejectCode;

    RejectCode(String rejectCode) {
        this.rejectCode = rejectCode;
    }

    public String getRejectCode() {
        return this.rejectCode;
    }
}
