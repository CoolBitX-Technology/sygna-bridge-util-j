package com.coolbitx.sygna.bridge;

import com.coolbitx.sygna.bridge.enums.RejectCode;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class CryptoTest {

    private final static String PUBLIC_KEY = "045b409c8c15fd82744ce4f7f86d65f27d605d945d4c4eee0e4e2515a3894b9d157483cc5e49c62c07b46cd59bc980445d9cf987622d66df20c6c3634f6eb05085";
    private final static String PRIVATE_KEY = "bf76d2680f23f6fc28111afe0179b8704c8e203a5faa5112f8aa52721f78fe6a";
    private final static String CALLBACK_URL = "https://google.com";

    @Test
    public void testEncodeAndDecode() throws Exception {
        String sensitiveData
                = "{"
                + "    \"originator\": {"
                + "        \"name\": \"Antoine Griezmann\","
                + "        \"date_of_birth\":\"1991-03-21\""
                + "    },"
                + "    \"beneficiary\":{"
                + "        \"name\": \"Leo Messi\""
                + "    }"
                + "}";
        JsonObject sensitiveDataObj = new Gson().fromJson(sensitiveData, JsonObject.class);
        String privateInfo = Crypto.sygnaEncodePrivateObj(sensitiveDataObj, PUBLIC_KEY);
        JsonObject decodedPrivateInfo = Crypto.sygnaDecodePrivateObj(privateInfo, PRIVATE_KEY);
        boolean isEqual = decodedPrivateInfo.equals(sensitiveDataObj);
        assertEquals(isEqual, true);
    }

    @Test
    public void testSignPermissionRequest() throws Exception {
        String privateInfo = "0405a39f02fb74cb0a748ff70adf0e4b7a8910befbaa536682fd3e4d1feed551c4e5d27bf85e";
        String dataDate = "2019-07-29T06:29:00.123Z";

        JsonArray originator_addrs = new JsonArray();
        originator_addrs.add("3KvJ1uHPShhEAWyqsBEzhfXyeh1TXKAd7D");
        JsonArray beneficiary_addrs = new JsonArray();
        beneficiary_addrs.add("3F4ReDwiMLu8LrAiXwwD2DhH8U9xMrUzUf");

        JsonObject transaction = new JsonObject();
        transaction.add("beneficiary_addrs", beneficiary_addrs);
        transaction.addProperty("amount", 4);
        transaction.add("originator_addrs", originator_addrs);
        transaction.addProperty("originator_vasp_code", "VASPUSNY1");
        transaction.addProperty("transaction_currency", "0x80000000");
        transaction.addProperty("beneficiary_vasp_code", "VASPUSNY2");

        JsonObject signedObj = Crypto.signPermissionRequest(privateInfo, transaction, dataDate, PRIVATE_KEY);
        JsonObject cloneSignedObj = signedObj.deepCopy();
        boolean isVerified = Crypto.verifyObject(cloneSignedObj, PUBLIC_KEY);
        assertEquals(isVerified, true);

        String expectedSignature = "03afa3865dd4d144080da5c3304186b19b688cd844b40b54548897b199af7bee69a8033b2adedeff9f33f067f2b54d662c3ace4d6cc5bdc750de81254c263f12";
        String signature = signedObj.get("signature").getAsString();
        assertEquals(signature, expectedSignature);

        //add originator_addrs_extra
        JsonObject originator_addrs_extra = new JsonObject();
        originator_addrs_extra.addProperty("DT", "001");
        transaction.add("originator_addrs_extra", originator_addrs_extra);
        signedObj = Crypto.signPermissionRequest(privateInfo, transaction, dataDate, PRIVATE_KEY);
        cloneSignedObj = signedObj.deepCopy();
        isVerified = Crypto.verifyObject(cloneSignedObj, PUBLIC_KEY);
        assertEquals(isVerified, true);

        expectedSignature = "e352991e809d4cd1be068cb8eefa43d11ef03ffecf37dc247b61fb9f718bb8660ef1e923aa9b6e701738f4e31bfe59824c4386df4ea8a5f08276402f7bce7d19";
        signature = signedObj.get("signature").getAsString();
        assertEquals(signature, expectedSignature);

        //add beneficiary_addrs_extra
        JsonObject beneficiary_addrs_extra = new JsonObject();
        beneficiary_addrs_extra.addProperty("DT", "002");
        transaction.add("beneficiary_addrs_extra", beneficiary_addrs_extra);
        signedObj = Crypto.signPermissionRequest(privateInfo, transaction, dataDate, PRIVATE_KEY);
        cloneSignedObj = signedObj.deepCopy();
        isVerified = Crypto.verifyObject(cloneSignedObj, PUBLIC_KEY);
        assertEquals(isVerified, true);

        expectedSignature = "e8114f2faf2d17dad513f03da6f804281af898ed289b2de524a6903121ee8b807e6affa16a8d311b5656ab0c96da69ebaeb60e65142a849fcd362a0ecda42e32";
        signature = signedObj.get("signature").getAsString();
        assertEquals(signature, expectedSignature);

        //add expire_date
        long expire_date = 4107667801000l;
        signedObj = Crypto.signPermissionRequest(privateInfo, transaction, dataDate, PRIVATE_KEY, expire_date);
        cloneSignedObj = signedObj.deepCopy();
        isVerified = Crypto.verifyObject(cloneSignedObj, PUBLIC_KEY);
        assertEquals(isVerified, true);

        expectedSignature = "a12c8c1b201106039a58f9ba3161739aea57edc66330e81604f9afa970b3f89d362a440747c730be010a106b1b12ffd83ce3935980a2fdafddd118639ac80fd4";
        signature = signedObj.get("signature").getAsString();
        assertEquals(signature, expectedSignature);
    }

    @Test
    public void testSignCallback() throws Exception {
        JsonObject signedObj = Crypto.signCallBack(CALLBACK_URL, PRIVATE_KEY);
        JsonObject cloneSignedObj = signedObj.deepCopy();
        boolean isVerified = Crypto.verifyObject(cloneSignedObj, PUBLIC_KEY);
        assertEquals(isVerified, true);

        String expectedSignature = "db6bef6b2201f3b7d42783ba6579758ad8d0e1bad8b2b732d499758e73c185e34e009f84ca68f1927c9c2ffee53bb730871a40faf555d82e28b8a211f25b213b";
        String signature = signedObj.get("signature").getAsString();
        assertEquals(signature, expectedSignature);
    }

    @Test
    public void testSignPermissionIfAccpeted() throws Exception {
        String transfer_id = "6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b";
        String permission_status = "ACCEPTED";

        JsonObject signedObj = Crypto.signPermission(transfer_id, permission_status, PRIVATE_KEY);
        JsonObject cloneSignedObj = signedObj.deepCopy();
        boolean isVerified = Crypto.verifyObject(cloneSignedObj, PUBLIC_KEY);
        assertEquals(isVerified, true);

        String expectedSignature = "8500fde0806c6f8c94db848c4096cbc7deee3ee659b6dce3cb3accea8391c81122b46245801669b3da200e4311e8ef4012587be183bc00bed372204899a57595";
        String signature = signedObj.get("signature").getAsString();
        assertEquals(signature, expectedSignature);

        //add expire_date
        long expire_date = 4107667801000l;
        signedObj = Crypto.signPermission(transfer_id, permission_status, PRIVATE_KEY, expire_date);
        cloneSignedObj = signedObj.deepCopy();
        isVerified = Crypto.verifyObject(cloneSignedObj, PUBLIC_KEY);
        assertEquals(isVerified, true);

        expectedSignature = "e4f0893278051c4b67a0e62fe85249c6a710374a1852aa3c19525193815721e74212601dc25ef52486d490efe49dd9a3d7a4a7dcaf3d40e995c9baed42bb5b9f";
        signature = signedObj.get("signature").getAsString();
        assertEquals(signature, expectedSignature);
    }

    @Test
    public void testSignPermissionIfRejected() throws Exception {
        String transfer_id = "6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b";
        String permission_status = "REJECTED";
        RejectCode rejectCode = RejectCode.BVRC001;

        JsonObject signedObj = Crypto.signPermission(transfer_id, permission_status, PRIVATE_KEY, rejectCode);
        JsonObject cloneSignedObj = signedObj.deepCopy();
        boolean isVerified = Crypto.verifyObject(cloneSignedObj, PUBLIC_KEY);
        assertEquals(isVerified, true);

        String expectedSignature = "d0aa0ef942207bfc2f478b72a654286ac8f99125c16c9969bb95da32aa374d0f235830398c2d35795f31d21958a9c3ee5eb6fd2f732efe363d2fd029e46b9243";
        String signature = signedObj.get("signature").getAsString();
        assertEquals(signature, expectedSignature);

        //add reject_message
        String rejectMessage = "service_downtime";
        signedObj = Crypto.signPermission(transfer_id, permission_status, PRIVATE_KEY, rejectCode, rejectMessage);
        cloneSignedObj = signedObj.deepCopy();
        isVerified = Crypto.verifyObject(cloneSignedObj, PUBLIC_KEY);
        assertEquals(isVerified, true);

        expectedSignature = "71bb4fdd606af346ff74f68c78b906848a44368fbc961c8b366e9d31494fefb24588157c7eeed6256bc3dae05263123e9cfd21f2e1c97c7b9f32cfae3429f654";
        signature = signedObj.get("signature").getAsString();
        assertEquals(signature, expectedSignature);

        //add expire_date
        long expire_date = 4107667801000l;
        signedObj = Crypto.signPermission(transfer_id, permission_status, PRIVATE_KEY, expire_date, rejectCode, rejectMessage);
        cloneSignedObj = signedObj.deepCopy();
        isVerified = Crypto.verifyObject(cloneSignedObj, PUBLIC_KEY);
        assertEquals(isVerified, true);

        expectedSignature = "4df61a7a68d3b550e1ebd26b08243b8752685502ae30107f355eeb0d3c41c1c23982291b7bacfdc29ce17131a2e825db950ae45611b61bc12e80345dad704e61";
        signature = signedObj.get("signature").getAsString();
        assertEquals(signature, expectedSignature);
    }

    @Test
    public void testSignTxId() throws Exception {
        String txId = "1a0c9bef489a136f7e05671f7f7fada2b9d96ac9f44598e1bcaa4779ac564dcd";
        String transferId = "eeac79bb6ad673bfb4444b3bed1191c4b084270445becb7fdc2af7a80bb66aab";

        JsonObject signedObj = Crypto.signTxId(transferId, txId, PRIVATE_KEY);
        JsonObject cloneSignedObj = signedObj.deepCopy();
        boolean isVerified = Crypto.verifyObject(cloneSignedObj, PUBLIC_KEY);
        assertEquals(isVerified, true);

        String expectedSignature = "9c9def21dd6860dbeae18362115d2adb6c98fe1e655965d503af0f3d7ad893b03adebcedc8d5a6a0e71e9c5d32d00e518c6dd056cf095af976c66aaef490712b";
        String signature = signedObj.get("signature").getAsString();
        assertEquals(signature, expectedSignature);
    }
}
