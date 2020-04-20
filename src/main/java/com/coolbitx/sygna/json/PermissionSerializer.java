/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.coolbitx.sygna.json;

import com.coolbitx.sygna.bridge.enums.PermissionStatus;
import com.coolbitx.sygna.bridge.model.Field;
import com.coolbitx.sygna.bridge.model.Permission;
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
public class PermissionSerializer implements JsonSerializer<Permission> {

    @Override
    public JsonElement serialize(Permission src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty(Field.TRANSFER_ID, src.getTransfer_id());
        jsonObj.addProperty(Field.PERMISSION_STATUS, src.getPermission_status());
        
        if(src.getExpire_date() != 0l){
            jsonObj.addProperty(Field.EXPIRE_DATE, src.getExpire_date());
        }
        if(src.getPermission_status().equals(PermissionStatus.REJECTED.getStatus())){
            jsonObj.addProperty(Field.REJECT_CODE, src.getRejectCode());
            if(!StringUtil.isNullOrEmpty(src.getRejectMessage())){
                 jsonObj.addProperty(Field.REJECT_MESSAGE, src.getRejectMessage());
            }
        }
        if(!StringUtil.isNullOrEmpty(src.getSignature())){
            jsonObj.addProperty(Field.SIGNATURE, src.getSignature());
        }
        return jsonObj;
    }

}
