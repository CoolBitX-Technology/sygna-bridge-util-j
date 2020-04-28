package com.coolbitx.sygna.bridge.model;

import com.coolbitx.sygna.util.Validator;
import com.google.gson.annotations.SerializedName;

public class BeneficiaryEndpointUrl extends Packet {

    public BeneficiaryEndpointUrl(String signature, String vaspCode, String beneficiaryEndpointUrl) {
        super(signature);
        this.vaspCode = vaspCode;
        this.beneficiaryEndpointUrl = beneficiaryEndpointUrl;
    }

    @SerializedName("vasp_code")
    private String vaspCode;
    @SerializedName("beneficiary_endpoint_url")
    private String beneficiaryEndpointUrl = null;

    public String getVaspCode() {
        return vaspCode;
    }

    public String getBeneficiaryEndpointUrl() {
        return beneficiaryEndpointUrl;
    }

    @Override
    public void check() throws Exception {
        super.check();
        checkSignData();
    }

    public void checkSignData() throws Exception {
        Validator.validateVaspCode(vaspCode);
        Validator.validateUrl(beneficiaryEndpointUrl);
    }
}
