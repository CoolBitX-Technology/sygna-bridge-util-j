/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.coolbitx.sygna.bridge;

import com.coolbitx.sygna.bridge.model.PermissionRequest;
import com.coolbitx.sygna.json.PermissionRequestSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author kunming.liu
 */
public class PermissionRequestSerializerTest {

    Gson gson = new GsonBuilder()
            .registerTypeAdapter(PermissionRequest.class, new PermissionRequestSerializer())
            .create();

    @Test
    public void testPermissionRequestSerializer() {
        String signature = "";
        String privateInfo = "0405a39f02fb74cb0a748ff70adf0e4b7a8910befbaa536682fd3e4d1feed551c4e5d27bf85e03836bbb975e83e620529139d31644cee17f60f089b5b44a89513c336e262dd2a686e6460339ec3a8eacbdeda77bbb7d18bec45bb4288bd959dae803176ddf535b634e6ea9367fffb5f811a10c540a4eacbd8ceb9a4b5631a1b64dfa7ac9c4f52dff09f14d18652e9eb30b0b4e56a145c6d912a89c5442a0673b83728cf6cbc527e3004eeaf609573edb2f2844c1ec68a1cf97917a9213ce695eb3624a32f7";
        String dataDate = "2019-07-29T06:29:00.123Z";
        long expireDate = 1582107090l;

        JsonObject transaction = new JsonObject();

        JsonArray originator_addrs = new JsonArray();
        originator_addrs.add("3KvJ1uHPShhEAWyqsBEzhfXyeh1TXKAd7D");

        JsonArray beneficiary_addrs = new JsonArray();
        beneficiary_addrs.add("3F4ReDwiMLu8LrAiXwwD2DhH8U9xMrUzUf");

        transaction.addProperty("transaction_currency", "0x80000000");
        transaction.add("originator_addrs", originator_addrs);
        transaction.addProperty("originator_vasp_code", "VASPUSNY1");
        transaction.addProperty("amount", 0.973);
        transaction.addProperty("beneficiary_vasp_code", "VASPUSNY2");
        transaction.add("beneficiary_addrs", beneficiary_addrs);

        //should ignore signature if signature is empty
        PermissionRequest permissionRequest = new PermissionRequest(signature, privateInfo, transaction, dataDate);
        JsonElement jsonElement = gson.toJsonTree(permissionRequest, PermissionRequest.class);
        String expectedMessage
                = String.format("{\"private_info\":\"%s\",\"transaction\":{"
                        + "\"originator_vasp_code\":\"%s\",\"originator_addrs\":%s,"
                        + "\"beneficiary_vasp_code\":\"%s\",\"beneficiary_addrs\":%s,"
                        + "\"transaction_currency\":\"%s\",\"amount\":%s},"
                        + "\"data_dt\":\"%s\"}",
                        privateInfo,
                        transaction.get("originator_vasp_code").getAsString(), transaction.get("originator_addrs").getAsJsonArray().toString(),
                        transaction.get("beneficiary_vasp_code").getAsString(), transaction.get("beneficiary_addrs").getAsJsonArray().toString(),
                        transaction.get("transaction_currency").getAsString(), transaction.get("amount").getAsNumber().toString(),
                        dataDate);
        assertEquals(jsonElement.toString(), expectedMessage);

        //should ignore expire_date if expire_date is 0l
        PermissionRequest permissionRequest1 = new PermissionRequest(signature, privateInfo, transaction, dataDate, 0l);
        JsonElement jsonElement1 = gson.toJsonTree(permissionRequest1, PermissionRequest.class);
        assertEquals(jsonElement1.toString(), expectedMessage);

        //add originator_addrs_extra
        JsonObject transaction1 = new JsonObject();
        JsonArray originator_addrs1 = new JsonArray();
        originator_addrs1.add("3KvJ1uHPShhEAWyqsBEzhfXyeh1TXKAd7D");
        originator_addrs1.add("16bUGjvunVp7LqygLHrTvHyvbvfeuRCWAh");
        JsonArray beneficiary_addrs1 = new JsonArray();
        beneficiary_addrs1.add("3F4ReDwiMLu8LrAiXwwD2DhH8U9xMrUzUf");
        beneficiary_addrs1.add("1DdmZV1iPgffWcYb78mfGtu6wixq7uBqFG");

        transaction1.addProperty("beneficiary_vasp_code", "VASPUSNY2");
        transaction1.addProperty("amount", 0.123456);
        transaction1.add("originator_addrs", originator_addrs1);
        transaction1.addProperty("originator_vasp_code", "VASPUSNY1");
        transaction1.add("beneficiary_addrs", beneficiary_addrs1);
        transaction1.addProperty("transaction_currency", "0x80000000");

        JsonObject originator_addrs_extra = new JsonObject();
        originator_addrs_extra.addProperty("DT", "001");
        transaction1.add("originator_addrs_extra", originator_addrs_extra);

        PermissionRequest permissionRequest2 = new PermissionRequest(signature, privateInfo, transaction1, dataDate);
        JsonElement jsonElement2 = gson.toJsonTree(permissionRequest2, PermissionRequest.class);
        String expectedMessage2
                = String.format("{\"private_info\":\"%s\",\"transaction\":{"
                        + "\"originator_vasp_code\":\"%s\",\"originator_addrs\":%s,"
                        + "\"originator_addrs_extra\":%s,"
                        + "\"beneficiary_vasp_code\":\"%s\",\"beneficiary_addrs\":%s,"
                        + "\"transaction_currency\":\"%s\",\"amount\":%s},"
                        + "\"data_dt\":\"%s\"}",
                        privateInfo,
                        transaction1.get("originator_vasp_code").getAsString(), transaction1.get("originator_addrs").getAsJsonArray().toString(),
                        transaction1.get("originator_addrs_extra").getAsJsonObject().toString(),
                        transaction1.get("beneficiary_vasp_code").getAsString(), transaction1.get("beneficiary_addrs").getAsJsonArray().toString(),
                        transaction1.get("transaction_currency").getAsString(), transaction1.get("amount").getAsNumber().toString(),
                        dataDate);
        assertEquals(jsonElement2.toString(), expectedMessage2);

        //add beneficiary_addrs_extra
        JsonObject beneficiary_addrs_extra = new JsonObject();
        beneficiary_addrs_extra.addProperty("DT", "002");
        transaction1.add("beneficiary_addrs_extra", beneficiary_addrs_extra);

        PermissionRequest permissionRequest3 = new PermissionRequest(signature, privateInfo, transaction1, dataDate);
        JsonElement jsonElement3 = gson.toJsonTree(permissionRequest3, PermissionRequest.class);
        String expectedMessage3
                = String.format("{\"private_info\":\"%s\",\"transaction\":{"
                        + "\"originator_vasp_code\":\"%s\",\"originator_addrs\":%s,"
                        + "\"originator_addrs_extra\":%s,"
                        + "\"beneficiary_vasp_code\":\"%s\",\"beneficiary_addrs\":%s,"
                        + "\"beneficiary_addrs_extra\":%s,"
                        + "\"transaction_currency\":\"%s\",\"amount\":%s},"
                        + "\"data_dt\":\"%s\"}",
                        privateInfo,
                        transaction1.get("originator_vasp_code").getAsString(), transaction1.get("originator_addrs").getAsJsonArray().toString(),
                        transaction1.get("originator_addrs_extra").getAsJsonObject().toString(),
                        transaction1.get("beneficiary_vasp_code").getAsString(), transaction1.get("beneficiary_addrs").getAsJsonArray().toString(),
                        transaction1.get("beneficiary_addrs_extra").getAsJsonObject().toString(),
                        transaction1.get("transaction_currency").getAsString(), transaction1.get("amount").getAsNumber().toString(),
                        dataDate);
        assertEquals(jsonElement3.toString(), expectedMessage3);

        PermissionRequest permissionRequest4 = new PermissionRequest(signature, privateInfo, transaction, dataDate, expireDate);
        JsonElement jsonElement4 = gson.toJsonTree(permissionRequest4, PermissionRequest.class);
        String expectedMessage4
                = String.format("{\"private_info\":\"%s\",\"transaction\":{"
                        + "\"originator_vasp_code\":\"%s\",\"originator_addrs\":%s,"
                        + "\"beneficiary_vasp_code\":\"%s\",\"beneficiary_addrs\":%s,"
                        + "\"transaction_currency\":\"%s\",\"amount\":%s},"
                        + "\"data_dt\":\"%s\",\"expire_date\":%d}",
                        privateInfo,
                        transaction.get("originator_vasp_code").getAsString(), transaction.get("originator_addrs").getAsJsonArray().toString(),
                        transaction.get("beneficiary_vasp_code").getAsString(), transaction.get("beneficiary_addrs").getAsJsonArray().toString(),
                        transaction.get("transaction_currency").getAsString(), transaction.get("amount").getAsNumber().toString(),
                        dataDate, expireDate);
        assertEquals(jsonElement4.toString(), expectedMessage4);

        PermissionRequest permissionRequest5 = new PermissionRequest(signature, privateInfo, transaction1, dataDate, expireDate);
        JsonElement jsonElement5 = gson.toJsonTree(permissionRequest5, PermissionRequest.class);
        String expectedMessage5
                = String.format("{\"private_info\":\"%s\",\"transaction\":{"
                        + "\"originator_vasp_code\":\"%s\",\"originator_addrs\":%s,"
                        + "\"originator_addrs_extra\":%s,"
                        + "\"beneficiary_vasp_code\":\"%s\",\"beneficiary_addrs\":%s,"
                        + "\"beneficiary_addrs_extra\":%s,"
                        + "\"transaction_currency\":\"%s\",\"amount\":%s},"
                        + "\"data_dt\":\"%s\",\"expire_date\":%d}",
                        privateInfo,
                        transaction1.get("originator_vasp_code").getAsString(), transaction1.get("originator_addrs").getAsJsonArray().toString(),
                        transaction1.get("originator_addrs_extra").getAsJsonObject().toString(),
                        transaction1.get("beneficiary_vasp_code").getAsString(), transaction1.get("beneficiary_addrs").getAsJsonArray().toString(),
                        transaction1.get("beneficiary_addrs_extra").getAsJsonObject().toString(),
                        transaction1.get("transaction_currency").getAsString(), transaction1.get("amount").getAsNumber().toString(),
                        dataDate, expireDate);
        assertEquals(jsonElement5.toString(), expectedMessage5);

        signature = "123456789";
        PermissionRequest permissionRequest6 = new PermissionRequest(signature, privateInfo, transaction, dataDate);
        JsonElement jsonElement6 = gson.toJsonTree(permissionRequest6, PermissionRequest.class);
        String expectedMessage6
                = String.format("{\"private_info\":\"%s\",\"transaction\":{"
                        + "\"originator_vasp_code\":\"%s\",\"originator_addrs\":%s,"
                        + "\"beneficiary_vasp_code\":\"%s\",\"beneficiary_addrs\":%s,"
                        + "\"transaction_currency\":\"%s\",\"amount\":%s},"
                        + "\"data_dt\":\"%s\",\"signature\":\"%s\"}",
                        privateInfo,
                        transaction.get("originator_vasp_code").getAsString(), transaction.get("originator_addrs").getAsJsonArray().toString(),
                        transaction.get("beneficiary_vasp_code").getAsString(), transaction.get("beneficiary_addrs").getAsJsonArray().toString(),
                        transaction.get("transaction_currency").getAsString(), transaction.get("amount").getAsNumber().toString(),
                        dataDate, signature);
        assertEquals(jsonElement6.toString(), expectedMessage6);

        //should ignore expire_date if expire_date is 0l
        PermissionRequest permissionRequest7 = new PermissionRequest(signature, privateInfo, transaction, dataDate, 0l);
        JsonElement jsonElement7 = gson.toJsonTree(permissionRequest7, PermissionRequest.class);
        assertEquals(jsonElement7.toString(), expectedMessage6);

        PermissionRequest permissionRequest8 = new PermissionRequest(signature, privateInfo, transaction, dataDate, expireDate);
        JsonElement jsonElement8 = gson.toJsonTree(permissionRequest8, PermissionRequest.class);
        String expectedMessage8
                = String.format("{\"private_info\":\"%s\",\"transaction\":{"
                        + "\"originator_vasp_code\":\"%s\",\"originator_addrs\":%s,"
                        + "\"beneficiary_vasp_code\":\"%s\",\"beneficiary_addrs\":%s,"
                        + "\"transaction_currency\":\"%s\",\"amount\":%s},"
                        + "\"data_dt\":\"%s\",\"expire_date\":%d,\"signature\":\"%s\"}",
                        privateInfo,
                        transaction.get("originator_vasp_code").getAsString(), transaction.get("originator_addrs").getAsJsonArray().toString(),
                        transaction.get("beneficiary_vasp_code").getAsString(), transaction.get("beneficiary_addrs").getAsJsonArray().toString(),
                        transaction.get("transaction_currency").getAsString(), transaction.get("amount").getAsNumber().toString(),
                        dataDate, expireDate, signature);
        assertEquals(jsonElement8.toString(), expectedMessage8);

        PermissionRequest permissionRequest9 = new PermissionRequest(signature, privateInfo, transaction1, dataDate, expireDate);
        JsonElement jsonElement9 = gson.toJsonTree(permissionRequest9, PermissionRequest.class);
        String expectedMessage9
                = String.format("{\"private_info\":\"%s\",\"transaction\":{"
                        + "\"originator_vasp_code\":\"%s\",\"originator_addrs\":%s,"
                        + "\"originator_addrs_extra\":%s,"
                        + "\"beneficiary_vasp_code\":\"%s\",\"beneficiary_addrs\":%s,"
                        + "\"beneficiary_addrs_extra\":%s,"
                        + "\"transaction_currency\":\"%s\",\"amount\":%s},"
                        + "\"data_dt\":\"%s\",\"expire_date\":%d,\"signature\":\"%s\"}",
                        privateInfo,
                        transaction1.get("originator_vasp_code").getAsString(), transaction1.get("originator_addrs").getAsJsonArray().toString(),
                        transaction1.get("originator_addrs_extra").getAsJsonObject().toString(),
                        transaction1.get("beneficiary_vasp_code").getAsString(), transaction1.get("beneficiary_addrs").getAsJsonArray().toString(),
                        transaction1.get("beneficiary_addrs_extra").getAsJsonObject().toString(),
                        transaction1.get("transaction_currency").getAsString(), transaction1.get("amount").getAsNumber().toString(),
                        dataDate, expireDate, signature);
        assertEquals(jsonElement9.toString(), expectedMessage9);

    }
}
