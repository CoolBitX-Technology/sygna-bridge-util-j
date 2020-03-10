package com.coolbitx.sygna.bridge.model;

import com.coolbitx.sygna.bridge.enums.RejectCode;
import com.coolbitx.sygna.util.Validator;
import com.google.gson.annotations.SerializedName;
import java.util.Calendar;

public class Permission extends Packet {

    public Permission(String signature, String transfer_id, String permission_status) {
        super(signature);
        this.transfer_id = transfer_id;
        this.permission_status = permission_status;
    }

    public Permission(String signature, String transfer_id, String permission_status, long expire_date) {
        super(signature);
        this.transfer_id = transfer_id;
        this.permission_status = permission_status;
        this.expire_date = expire_date;
    }

    public Permission(String signature, String transfer_id, String permission_status, RejectCode rejectCode) {
        super(signature);
        this.transfer_id = transfer_id;
        this.permission_status = permission_status;
        this.rejectCode = rejectCode;
    }

    public Permission(String signature, String transfer_id, String permission_status, long expire_date, RejectCode rejectCode) {
        super(signature);
        this.transfer_id = transfer_id;
        this.permission_status = permission_status;
        this.expire_date = expire_date;
        this.rejectCode = rejectCode;
    }

    public Permission(String signature, String transfer_id, String permission_status, RejectCode rejectCode,
            String rejectMessage) {
        super(signature);
        this.transfer_id = transfer_id;
        this.permission_status = permission_status;
        this.rejectCode = rejectCode;
        this.rejectMessage = rejectMessage;
    }

    public Permission(String signature, String transfer_id, String permission_status, long expire_date, RejectCode rejectCode,
            String rejectMessage) {
        super(signature);
        this.transfer_id = transfer_id;
        this.permission_status = permission_status;
        this.expire_date = expire_date;
        this.rejectCode = rejectCode;
        this.rejectMessage = rejectMessage;
    }

    private String transfer_id;
    private String permission_status;
    private long expire_date = 0l;
    @SerializedName("reject_code")
    private RejectCode rejectCode = RejectCode.NULL;
    @SerializedName("reject_message")
    private String rejectMessage = null;

    public String getTransfer_id() {
        return transfer_id;
    }

    public String getPermission_status() {
        return permission_status;
    }

    public long getExpire_date() {
        return expire_date;
    }

    public String getRejectCode() {
        if (rejectCode == RejectCode.NULL) {
            return null;
        }
        return rejectCode.getRejectCode();
    }

    public String getRejectMessage() {
        return rejectMessage;
    }

    @Override
    public void check() throws Exception {
        super.check();
        Validator.validateTransferId(transfer_id);
        Validator.validatePermissionStatus(permission_status);
        Validator.validateExpireDate(Calendar.getInstance(), expire_date);
        Validator.validateRejectData(permission_status, rejectCode, rejectMessage);

    }

}
