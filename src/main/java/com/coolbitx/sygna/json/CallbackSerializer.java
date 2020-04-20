/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.coolbitx.sygna.json;

import com.coolbitx.sygna.bridge.model.Callback;
import com.coolbitx.sygna.bridge.model.Field;
import com.coolbitx.sygna.bridge.model.Transaction;
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
public class CallbackSerializer implements JsonSerializer<Callback> {

    @Override
    public JsonElement serialize(Callback src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty(Field.CALL_BACK_URL, src.getCallback_url());

        if(!StringUtil.isNullOrEmpty(src.getSignature())){
            jsonObj.addProperty(Field.SIGNATURE, src.getSignature());
        }
        return jsonObj;
    }

}
