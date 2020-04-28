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
        String beneficiaryEndpointUrl = "https://api.sygna.io/api/v1.1.0/bridge/";

        //should ignore signature if signature is empty
        BeneficiaryEndpointUrl instance = new BeneficiaryEndpointUrl(signature, vaspCode, beneficiaryEndpointUrl);
        JsonElement jsonElement = gson.toJsonTree(instance, BeneficiaryEndpointUrl.class);
        String expectedMessage
                = String.format("{\"vasp_code\":\"%s\",\"beneficiary_endpoint_url\":\"%s\"}",
                        vaspCode, beneficiaryEndpointUrl);
        assertEquals(jsonElement.toString(), expectedMessage);

        signature = "123456789";
        BeneficiaryEndpointUrl isntance1 = new BeneficiaryEndpointUrl(signature, vaspCode, beneficiaryEndpointUrl);
        JsonElement jsonElement1 = gson.toJsonTree(isntance1, BeneficiaryEndpointUrl.class);
        String expectedMessage1
                = String.format("{\"vasp_code\":\"%s\",\"beneficiary_endpoint_url\":\"%s\",\"signature\":\"%s\"}",
                        vaspCode, beneficiaryEndpointUrl, signature);
        assertEquals(jsonElement1.toString(), expectedMessage1);
    }
}
