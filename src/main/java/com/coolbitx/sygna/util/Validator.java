package com.coolbitx.sygna.util;

public class Validator {

    public static void validatePrivateKey(String privateKey) throws Exception {
        if (StringUtil.isNullOrEmpty(privateKey)) {
            throw new Exception("privateKey length should NOT be shorter than 1");
        }
    }
}
