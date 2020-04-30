/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.coolbitx.sygna.bridge;

import com.coolbitx.sygna.bridge.model.BeneficiaryEndpointUrl;
import com.coolbitx.sygna.json.BeneficiaryEndpointUrlSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author kunming.liu
 */
public class BeneficiaryEndpointUrlSerializerTest {

    Gson gson = new GsonBuilder()
            .registerTypeAdapter(BeneficiaryEndpointUrl.class, new BeneficiaryEndpointUrlSerializer())
            .create();

    @Test
    public void testBeneficiaryEndpointUrlSerializer() {
        String signature = null;
        String vaspCode = "VASPUSNY1";
        String callbackPermissionRequestUrl = "https://api.sygna.io/api/v1.1.0/bridge/permission-request";
        String callbackTxIdUrl = "https://api.sygna.io/api/v1.1.0/bridge/txid";

        //should ignore signature if signature is empty
        BeneficiaryEndpointUrl instance = new BeneficiaryEndpointUrl(signature, vaspCode, callbackPermissionRequestUrl, callbackTxIdUrl);
        JsonElement jsonElement = gson.toJsonTree(instance, BeneficiaryEndpointUrl.class);
        String expectedMessage
                = String.format("{\"vasp_code\":\"%s\",\"callback_permission_request_url\":\"%s\",\"callback_txid_url\":\"%s\"}",
                        vaspCode, callbackPermissionRequestUrl, callbackTxIdUrl);
        assertEquals(jsonElement.toString(), expectedMessage);

        //should ignore callbackTxIdUrl if callbackTxIdUrl is empty
        BeneficiaryEndpointUrl instance1 = new BeneficiaryEndpointUrl(signature, vaspCode, callbackPermissionRequestUrl, null);
        JsonElement jsonElement1 = gson.toJsonTree(instance1, BeneficiaryEndpointUrl.class);
        String expectedMessage1
                = String.format("{\"vasp_code\":\"%s\",\"callback_permission_request_url\":\"%s\"}",
                        vaspCode, callbackPermissionRequestUrl);
        assertEquals(jsonElement1.toString(), expectedMessage1);

        //should ignore callbackPermissionRequestUrl if callbackPermissionRequestUrl is empty
        BeneficiaryEndpointUrl instance2 = new BeneficiaryEndpointUrl(signature, vaspCode, "", callbackTxIdUrl);
        JsonElement jsonElement2 = gson.toJsonTree(instance2, BeneficiaryEndpointUrl.class);
        String expectedMessage2
                = String.format("{\"vasp_code\":\"%s\",\"callback_txid_url\":\"%s\"}",
                        vaspCode, callbackTxIdUrl);
        assertEquals(jsonElement2.toString(), expectedMessage2);

        signature = "123456789";
        BeneficiaryEndpointUrl isntance3 = new BeneficiaryEndpointUrl(signature, vaspCode, callbackPermissionRequestUrl, callbackTxIdUrl);
        JsonElement jsonElement3 = gson.toJsonTree(isntance3, BeneficiaryEndpointUrl.class);
        String expectedMessage3
                = String.format("{\"vasp_code\":\"%s\",\"callback_permission_request_url\":\"%s\",\"callback_txid_url\":\"%s\",\"signature\":\"%s\"}",
                        vaspCode, callbackPermissionRequestUrl, callbackTxIdUrl, signature);
        assertEquals(jsonElement3.toString(), expectedMessage3);
    }
}
