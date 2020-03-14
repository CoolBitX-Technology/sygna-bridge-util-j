package com.coolbitx.sygna.bridge.model;

import com.coolbitx.sygna.util.Validator;

public class Callback extends Packet {

    public Callback(String signature, String callback_url) {
        super(signature);
        this.callback_url = callback_url;
    }

    private String callback_url;

    public String getCallback_url() {
        return callback_url;
    }

    @Override
    public void check() throws Exception {
        super.check();
        checkSignData();
    }

    public void checkSignData() throws Exception {
        Validator.validateUrl(callback_url);
    }

}
