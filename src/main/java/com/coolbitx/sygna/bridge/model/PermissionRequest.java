package com.coolbitx.sygna.bridge.model;

import com.coolbitx.sygna.util.Validator;
import com.google.gson.JsonObject;
import java.util.Calendar;

public class PermissionRequest extends Packet {
    
    public PermissionRequest(String signature, String private_info, JsonObject transaction, String data_dt) {
        super(signature);
        this.private_info = private_info;
        this.transaction = transaction;
        this.data_dt = data_dt;
    }
    
    public PermissionRequest(String signature, String private_info, JsonObject transaction, String data_dt, long expire_date) {
        super(signature);
        this.private_info = private_info;
        this.transaction = transaction;
        this.data_dt = data_dt;
        this.expire_date = expire_date;
    }
    
    private String private_info;
    private JsonObject transaction;
    private String data_dt;
    private long expire_date = 0l;
    
    public String getPrivate_info() {
        return private_info;
    }
    
    public JsonObject getTransaction() {
        return transaction;
    }
    
    public String getData_dt() {
        return data_dt;
    }

    public long getExpire_date() {
        return expire_date;
    }

    @Override
    public void check() throws Exception {
        super.check();
        Validator.validatePrivateInfo(private_info);
        Validator.validateTransactionSchema(transaction);
        Validator.validateDataDate(data_dt);
        Validator.validateExpireDate(Calendar.getInstance(), expire_date);
    }    
    
}
