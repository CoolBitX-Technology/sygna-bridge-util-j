/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.coolbitx.sygna.bridge;

import com.coolbitx.sygna.bridge.enums.PermissionStatus;
import com.coolbitx.sygna.bridge.enums.RejectCode;
import com.coolbitx.sygna.bridge.model.Permission;
import com.coolbitx.sygna.json.PermissionSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author kunming.liu
 */
public class PermissionSerializerTest {

    Gson gson = new GsonBuilder()
            .registerTypeAdapter(Permission.class, new PermissionSerializer())
            .create();

    @Test
    public void testPermissionSerializeIfAccepted() {
        String signature = "";
        String transferId = "456";
        String permissionStatus = PermissionStatus.ACCEPTED.getStatus();

        //should ignore ignore signature if signature is null
        Permission pemission = new Permission(signature, transferId, permissionStatus);
        JsonElement jsonElement = gson.toJsonTree(pemission, Permission.class);
        String expectedMessage
                = String.format("{\"transfer_id\":\"%s\",\"permission_status\":\"%s\"}",
                        transferId, permissionStatus);
        assertEquals(jsonElement.toString(), expectedMessage);

        //should ignore expireDate if expireDate is 0l
        Permission pemission1 = new Permission(signature, transferId, permissionStatus, 0l);
        JsonElement jsonElement1 = gson.toJsonTree(pemission1, Permission.class);
        assertEquals(jsonElement1.toString(), expectedMessage);

        //should ignore rejectCode if permissionStatus is accepted
        Permission pemission2 = new Permission(signature, transferId, permissionStatus, RejectCode.BVRC001);
        JsonElement jsonElement2 = gson.toJsonTree(pemission2, Permission.class);
        assertEquals(jsonElement2.toString(), expectedMessage);

        //should ignore rejectCode and rejectMessage if  permissionStatus is accepted
        Permission pemission3 = new Permission(signature, transferId, permissionStatus, RejectCode.BVRC001, "error");
        JsonElement jsonElement3 = gson.toJsonTree(pemission3, Permission.class);
        assertEquals(jsonElement3.toString(), expectedMessage);

        long expireDate = 1582107090l;
        Permission pemission4 = new Permission(signature, transferId, permissionStatus, expireDate);
        JsonElement jsonElement4 = gson.toJsonTree(pemission4, Permission.class);
        String expectedMessage4
                = String.format("{\"transfer_id\":\"%s\",\"permission_status\":\"%s\",\"expire_date\":%d}",
                        transferId, permissionStatus, expireDate);
        assertEquals(jsonElement4.toString(), expectedMessage4);

        //should ignore rejectCode if permissionStatus is accepted
        Permission pemission5 = new Permission(signature, transferId, permissionStatus, expireDate, RejectCode.BVRC001);
        JsonElement jsonElement5 = gson.toJsonTree(pemission5, Permission.class);
        assertEquals(jsonElement5.toString(), expectedMessage4);

        //should ignore rejectCode and rejectMessage if  permissionStatus is accepted
        Permission pemission6 = new Permission(signature, transferId, permissionStatus, expireDate, RejectCode.BVRC001, "error");
        JsonElement jsonElement6 = gson.toJsonTree(pemission6, Permission.class);
        assertEquals(jsonElement6.toString(), expectedMessage4);

        signature = "1234";
        //should signature be added at the tail
        Permission pemission7 = new Permission(signature, transferId, permissionStatus);
        JsonElement jsonElement7 = gson.toJsonTree(pemission7, Permission.class);
        String expectedMessage7
                = String.format("{\"transfer_id\":\"%s\",\"permission_status\":\"%s\",\"signature\":\"%s\"}",
                        transferId, permissionStatus, signature);
        assertEquals(jsonElement7.toString(), expectedMessage7);

        //should ignore expireDate if expireDate is 0l
        Permission pemission8 = new Permission(signature, transferId, permissionStatus, 0l);
        JsonElement jsonElement8 = gson.toJsonTree(pemission8, Permission.class);
        assertEquals(jsonElement8.toString(), expectedMessage7);

        //should ignore rejectCode if permissionStatus is accepted
        Permission pemission9 = new Permission(signature, transferId, permissionStatus, RejectCode.BVRC001);
        JsonElement jsonElement9 = gson.toJsonTree(pemission9, Permission.class);
        assertEquals(jsonElement9.toString(), expectedMessage7);

        //should ignore rejectCode and rejectMessage if  permissionStatus is accepted
        Permission pemission10 = new Permission(signature, transferId, permissionStatus, RejectCode.BVRC001, "error");
        JsonElement jsonElement10 = gson.toJsonTree(pemission10, Permission.class);
        assertEquals(jsonElement10.toString(), expectedMessage7);

        Permission pemission11 = new Permission(signature, transferId, permissionStatus, expireDate);
        JsonElement jsonElement11 = gson.toJsonTree(pemission11, Permission.class);
        String expectedMessage11
                = String.format("{\"transfer_id\":\"%s\",\"permission_status\":\"%s\",\"expire_date\":%d,\"signature\":\"%s\"}",
                        transferId, permissionStatus, expireDate, signature);
        assertEquals(jsonElement11.toString(), expectedMessage11);

        //should ignore rejectCode if permissionStatus is accepted
        Permission pemission12 = new Permission(signature, transferId, permissionStatus, expireDate, RejectCode.BVRC001);
        JsonElement jsonElement12 = gson.toJsonTree(pemission12, Permission.class);
        assertEquals(jsonElement12.toString(), expectedMessage11);

        //should ignore rejectCode and rejectMessage if  permissionStatus is accepted
        Permission pemission13 = new Permission(signature, transferId, permissionStatus, expireDate, RejectCode.BVRC001, "error");
        JsonElement jsonElement13 = gson.toJsonTree(pemission13, Permission.class);
        assertEquals(jsonElement13.toString(), expectedMessage11);
    }

    @Test
    public void testPermissionSerializeIfRejected() {
        String signature = null;
        String transferId = "456";
        String permissionStatus = PermissionStatus.REJECTED.getStatus();
        RejectCode rejectCode = RejectCode.BVRC001;
        String rejectMessage = "unsupported_currency";

        //should ignore ignore signature if signature is null
        Permission pemission = new Permission(signature, transferId, permissionStatus, rejectCode);
        JsonElement jsonElement = gson.toJsonTree(pemission, Permission.class);
        String expectedMessage
                = String.format("{\"transfer_id\":\"%s\",\"permission_status\":\"%s\",\"reject_code\":\"%s\"}",
                        transferId, permissionStatus, rejectCode.getRejectCode());
        assertEquals(jsonElement.toString(), expectedMessage);

        //should ignore expireDate if expireDate is 0l
        Permission pemission1 = new Permission(signature, transferId, permissionStatus, 0l, rejectCode);
        JsonElement jsonElement1 = gson.toJsonTree(pemission1, Permission.class);
        assertEquals(jsonElement1.toString(), expectedMessage);

        //should ignore rejectMessage if rejectMessage is empty
        Permission pemission2 = new Permission(signature, transferId, permissionStatus, RejectCode.BVRC001, null);
        JsonElement jsonElement2 = gson.toJsonTree(pemission2, Permission.class);
        assertEquals(jsonElement2.toString(), expectedMessage);

        Permission pemission3 = new Permission(signature, transferId, permissionStatus, RejectCode.BVRC001, rejectMessage);
        JsonElement jsonElement3 = gson.toJsonTree(pemission3, Permission.class);
        String expectedMessage3
                = String.format("{\"transfer_id\":\"%s\",\"permission_status\":\"%s\",\"reject_code\":\"%s\",\"reject_message\":\"%s\"}",
                        transferId, permissionStatus, rejectCode.getRejectCode(), rejectMessage);
        assertEquals(jsonElement3.toString(), expectedMessage3);

        long expireDate = 1582107090l;
        Permission pemission4 = new Permission(signature, transferId, permissionStatus, expireDate, rejectCode);
        JsonElement jsonElement4 = gson.toJsonTree(pemission4, Permission.class);
        String expectedMessage4
                = String.format("{\"transfer_id\":\"%s\",\"permission_status\":\"%s\",\"expire_date\":%d,\"reject_code\":\"%s\"}",
                        transferId, permissionStatus, expireDate, rejectCode.getRejectCode());
        assertEquals(jsonElement4.toString(), expectedMessage4);

        //should ignore rejectMessage if rejectMessage is empty
        Permission pemission5 = new Permission(signature, transferId, permissionStatus, expireDate, RejectCode.BVRC001, null);
        JsonElement jsonElement5 = gson.toJsonTree(pemission5, Permission.class);
        assertEquals(jsonElement5.toString(), expectedMessage4);

        Permission pemission6 = new Permission(signature, transferId, permissionStatus, expireDate, RejectCode.BVRC001, rejectMessage);
        JsonElement jsonElement6 = gson.toJsonTree(pemission6, Permission.class);
        String expectedMessage6
                = String.format("{\"transfer_id\":\"%s\",\"permission_status\":\"%s\",\"expire_date\":%d,\"reject_code\":\"%s\",\"reject_message\":\"%s\"}",
                        transferId, permissionStatus, expireDate, rejectCode.getRejectCode(), rejectMessage);
        assertEquals(jsonElement6.toString(), expectedMessage6);

        signature = "1234";
        //should signature be added at the tail
        Permission pemission7 = new Permission(signature, transferId, permissionStatus, rejectCode);
        JsonElement jsonElement7 = gson.toJsonTree(pemission7, Permission.class);
        String expectedMessage7
                = String.format("{\"transfer_id\":\"%s\",\"permission_status\":\"%s\",\"reject_code\":\"%s\",\"signature\":\"%s\"}",
                        transferId, permissionStatus, rejectCode.getRejectCode(), signature);
        assertEquals(jsonElement7.toString(), expectedMessage7);

        //should ignore expireDate if expireDate is 0l
        Permission pemission8 = new Permission(signature, transferId, permissionStatus, 0l, rejectCode);
        JsonElement jsonElement8 = gson.toJsonTree(pemission8, Permission.class);
        assertEquals(jsonElement8.toString(), expectedMessage7);

        //should ignore rejectMessage if rejectMessage is empty
        Permission pemission9 = new Permission(signature, transferId, permissionStatus, RejectCode.BVRC001, null);
        JsonElement jsonElement9 = gson.toJsonTree(pemission9, Permission.class);
        assertEquals(jsonElement9.toString(), expectedMessage7);

        Permission pemission10 = new Permission(signature, transferId, permissionStatus, RejectCode.BVRC001, rejectMessage);
        JsonElement jsonElement10 = gson.toJsonTree(pemission10, Permission.class);
        String expectedMessage10
                = String.format("{\"transfer_id\":\"%s\",\"permission_status\":\"%s\",\"reject_code\":\"%s\",\"reject_message\":\"%s\",\"signature\":\"%s\"}",
                        transferId, permissionStatus, rejectCode.getRejectCode(), rejectMessage, signature);
        assertEquals(jsonElement10.toString(), expectedMessage10);

        Permission pemission11 = new Permission(signature, transferId, permissionStatus, expireDate, rejectCode);
        JsonElement jsonElement11 = gson.toJsonTree(pemission11, Permission.class);
        String expectedMessage11
                = String.format("{\"transfer_id\":\"%s\",\"permission_status\":\"%s\",\"expire_date\":%d,\"reject_code\":\"%s\",\"signature\":\"%s\"}",
                        transferId, permissionStatus, expireDate, rejectCode.getRejectCode(), signature);
        assertEquals(jsonElement11.toString(), expectedMessage11);

        //should ignore rejectMessage if rejectMessage is empty
        Permission pemission12 = new Permission(signature, transferId, permissionStatus, expireDate, RejectCode.BVRC001, null);
        JsonElement jsonElement12 = gson.toJsonTree(pemission12, Permission.class);
        assertEquals(jsonElement12.toString(), expectedMessage11);

        Permission pemission13 = new Permission(signature, transferId, permissionStatus, expireDate, RejectCode.BVRC001, rejectMessage);
        JsonElement jsonElement13 = gson.toJsonTree(pemission13, Permission.class);
        String expectedMessage13
                = String.format("{\"transfer_id\":\"%s\",\"permission_status\":\"%s\",\"expire_date\":%d,\"reject_code\":\"%s\",\"reject_message\":\"%s\",\"signature\":\"%s\"}",
                        transferId, permissionStatus, expireDate, rejectCode.getRejectCode(), rejectMessage, signature);
        assertEquals(jsonElement13.toString(), expectedMessage13);
    }

}
