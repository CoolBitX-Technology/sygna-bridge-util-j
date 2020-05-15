package com.coolbitx.sygna.bridge.model;

import com.coolbitx.sygna.util.Validator;
import com.google.gson.annotations.SerializedName;

public class BeneficiaryEndpointUrl extends Packet {

    public BeneficiaryEndpointUrl(String signature, String vaspCode,
            String callbackPermissionRequestUrl, String callbackTxIdUrl) {
        super(signature);
        this.vaspCode = vaspCode;
        this.callbackPermissionRequestUrl = callbackPermissionRequestUrl;
        this.callbackTxIdUrl = callbackTxIdUrl;
    }

    @SerializedName("vasp_code")
    private String vaspCode;
    @SerializedName("callback_permission_request_url")
    private String callbackPermissionRequestUrl = null;
    @SerializedName("callback_txid_url")
    private String callbackTxIdUrl = null;

    public String getVaspCode() {
        return vaspCode;
    }

    public String getCallbackPermissionRequestUrl() {
        return callbackPermissionRequestUrl;
    }

    public String getCallbackTxIdUrl() {
        return callbackTxIdUrl;
    }

    @Override
    public void check() throws Exception {
        super.check();
        checkSignData();
    }

    public void checkSignData() throws Exception {
        Validator.validateVaspCode(vaspCode);
        Validator.validateBeneficiaryEndpointUrl(callbackPermissionRequestUrl,callbackTxIdUrl);
    }
}
