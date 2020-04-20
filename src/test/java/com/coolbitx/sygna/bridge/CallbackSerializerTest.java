/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.coolbitx.sygna.bridge;

import com.coolbitx.sygna.bridge.model.Callback;
import com.coolbitx.sygna.json.CallbackSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author kunming.liu
 */
public class CallbackSerializerTest {

    Gson gson = new GsonBuilder()
            .registerTypeAdapter(Callback.class, new CallbackSerializer())
            .create();

    @Test
    public void testCallbackSerializer() {
        String signature = null;
        String callbackUrl = "https://google.com";

        //should ignore signature if signature is empty
        Callback callback = new Callback(signature, callbackUrl);
        JsonElement jsonElement = gson.toJsonTree(callback, Callback.class);
        String expectedMessage
                = String.format("{\"callback_url\":\"%s\"}",
                        callbackUrl);
        assertEquals(jsonElement.toString(), expectedMessage);

        signature = "123456789";
        Callback callback1 = new Callback(signature, callbackUrl);
        JsonElement jsonElement1 = gson.toJsonTree(callback1, Callback.class);
        String expectedMessage1
                = String.format("{\"callback_url\":\"%s\",\"signature\":\"%s\"}",
                        callbackUrl, signature);
        assertEquals(jsonElement1.toString(), expectedMessage1);
    }
}
