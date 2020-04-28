/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.coolbitx.sygna.json;

import com.coolbitx.sygna.bridge.model.BeneficiaryEndpointUrl;
import com.coolbitx.sygna.bridge.model.Field;
import com.coolbitx.sygna.util.StringUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;

/**
 *
 * @author kunming.liu
 */
public class BeneficiaryEndpointUrlSerializer implements JsonSerializer<BeneficiaryEndpointUrl> {

    @Override
    public JsonElement serialize(BeneficiaryEndpointUrl src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty(Field.VASP_CODE, src.getVaspCode());
        jsonObj.addProperty(Field.BENEFICIARY_ENDPOINT_URL, src.getBeneficiaryEndpointUrl());
        if (!StringUtil.isNullOrEmpty(src.getSignature())) {
            jsonObj.addProperty(Field.SIGNATURE, src.getSignature());
        }
        return jsonObj;
    }

}
