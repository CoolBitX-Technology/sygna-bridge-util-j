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
    @SerializedName("BVRC005")
    BVRC005("BVRC005"),
    @SerializedName("BVRC006")
    BVRC006("BVRC006"),
    @SerializedName("BVRC007")
    BVRC007("BVRC007"),
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
