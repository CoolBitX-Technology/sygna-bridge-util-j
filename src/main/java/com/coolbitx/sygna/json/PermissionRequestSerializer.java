/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.coolbitx.sygna.json;

import com.coolbitx.sygna.bridge.model.Field;
import com.coolbitx.sygna.bridge.model.PermissionRequest;
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
public class PermissionRequestSerializer implements JsonSerializer<PermissionRequest> {

    @Override
    public JsonElement serialize(PermissionRequest src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty(Field.PRIVATE_INFO, src.getPrivate_info());

        JsonObject transactionObj = src.getTransaction();
        JsonObject sortedTransactionObj = new JsonObject();

        sortedTransactionObj.addProperty(Field.ORIGINATOR_VASP_CODE, transactionObj.get(Field.ORIGINATOR_VASP_CODE).getAsString());
        sortedTransactionObj.add(Field.ORIGINATOR_ADDRS, transactionObj.get(Field.ORIGINATOR_ADDRS).getAsJsonArray());
        if (transactionObj.has(Field.ORIGINATOR_ADDRS_EXTRA)) {
            sortedTransactionObj.add(Field.ORIGINATOR_ADDRS_EXTRA, transactionObj.get(Field.ORIGINATOR_ADDRS_EXTRA).getAsJsonObject());
        }

        sortedTransactionObj.addProperty(Field.BENEFICIARY_VASP_CODE, transactionObj.get(Field.BENEFICIARY_VASP_CODE).getAsString());
        sortedTransactionObj.add(Field.BENEFICIARY_ADDRS, transactionObj.get(Field.BENEFICIARY_ADDRS).getAsJsonArray());
        if (transactionObj.has(Field.BENEFICIARY_ADDRS_EXTRA)) {
            sortedTransactionObj.add(Field.BENEFICIARY_ADDRS_EXTRA, transactionObj.get(Field.BENEFICIARY_ADDRS_EXTRA).getAsJsonObject());
        }
        
        sortedTransactionObj.addProperty(Field.TRANSACTION_CURRENCY, transactionObj.get(Field.TRANSACTION_CURRENCY).getAsString());
        sortedTransactionObj.addProperty(Field.AMOUNT, transactionObj.get(Field.AMOUNT).getAsNumber());
        
        jsonObj.add(Field.TRANSACTION, sortedTransactionObj);
        jsonObj.addProperty(Field.DATA_DT, src.getData_dt());
        
        if (src.getExpire_date() != 0l) {
            jsonObj.addProperty(Field.EXPIRE_DATE, src.getExpire_date());
        }
        
        if (!StringUtil.isNullOrEmpty(src.getSignature())) {
            jsonObj.addProperty(Field.SIGNATURE, src.getSignature());
        }
        return jsonObj;
    }

}
