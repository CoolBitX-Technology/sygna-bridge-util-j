package com.coolbitx.sygna.bridge;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.coolbitx.sygna.bridge.enums.PermissionStatus;
import com.coolbitx.sygna.bridge.model.Callback;
import com.coolbitx.sygna.bridge.model.Permission;
import com.coolbitx.sygna.bridge.model.PermissionRequest;
import com.coolbitx.sygna.bridge.model.Transaction;
import com.coolbitx.sygna.bridge.model.Vasp;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ApiTest {

    private final static String API_KEY = "{{API_KEY}}";
    private final static String CALLBACK_URL = "{{CALLBACK_URL}}";
    private final static String DOMAIN = "{{DOMAIN}}";

    private final static API API_UTIL = new API(API_KEY, DOMAIN);

    @Test
    public void testGetVaspList() throws Exception {
        API_UTIL.getVASPList();
        API_UTIL.getVASPList(true);
        API_UTIL.getVASPList(false);
    }

    @Test
    public void testGetVaspByCode() throws Exception {
        final String vaspCode = "VASPJPJT4";
        String publicKey = API_UTIL.getVASPPublicKey(vaspCode);
        assertEquals(publicKey.length(), 130); // Uncompressed Public Key
        API_UTIL.getVASPPublicKey(vaspCode, false);
        API_UTIL.getVASPPublicKey(vaspCode, true);
    }

    @Test(expected = Exception.class)
    public void testGetVaspByCodeFailed() throws Exception {
        API_UTIL.getVASPPublicKey(" ");

    }

    @Test
    public void testGetSB() throws Exception {
        final String url = DOMAIN + "api/v1/bridge/vasp";
        JsonObject obj = API_UTIL.getSB(url);
        Gson gson = new Gson();
        gson.fromJson(obj, Vasp.class);
    }

    @Test
    public void testPostTransactionId() throws Exception {
        final String txId = "1a0c9bef489a136f7e05671f7f7fada2b9d96ac9f44598e1bcaa4779ac564dcd";
        final String transferId = "eeac79bb6ad673bfb4444b3bed1191c4b084270445becb7fdc2af7a80bb66aab";
        final String signature = "7e440ed0a978d05863d9b5fd674f4dbba264a1ce5fed72cd9bb2bfb16b4deaba54152d2b61f59d0f1ebf7e16fd6f575260851e9f5a1496e8f0f0805faa2f277a";
        Transaction tran = new Transaction(transferId, txId, signature);
        JsonObject obj = API_UTIL.postTransactionId(tran);
        System.out.println("return:" + obj.toString());
        String errorCode = obj.get("err_code").getAsString();
        assertEquals(errorCode, "010319");
    }

    @Test
    public void testGetStatus() throws Exception {
        final String transferId = "eeac79bb6ad673bfb4444b3bed1191c4b084270445becb7fdc2af7a80bb66aab";
        JsonObject obj = API_UTIL.getStatus(transferId);
        System.out.println("return:" + obj.toString());
        String errorCode = obj.get("err_code").getAsString();
        assertEquals(errorCode, "010319");
    }

    @Test
    public void testPostPermission() throws Exception {
        final String status = "ACCEPTED";
        final String transferId = "7b70c6f935441941695af5033435941cacc909685669df73628e5c68e32f231f";
        final String signature = "cb93bb7add0a7475244870e54c5bf119eb642263cd07b1c5b91e3e14dca5f54f71d99e9e6a0387c88a7f1cbc3fd0932111cbbb5040f2b59986382ab03a3adf42";

        Permission perm = new Permission(signature, transferId, status);
        JsonObject obj = API_UTIL.postPermission(perm);
        System.out.println("return:" + obj.toString());
        String errorCode = obj.get("err_code").getAsString();
        assertEquals(errorCode, "010215"); // "The transfer already has permission status"
    }

    @Test
    public void testPostPermissionRequest() throws Exception {
        final String permReqSig = "48f5220f45d65c7f586e5b147f3ebd7e1803c1a86183498b62d74151517311465a80a3d1b742df191ffaee6329192b6d63e49ea1598467c001cc732e8df6e566";
        final String privateInfo = "0405a39f02fb74cb0a748ff70adf0e4b7a8910befbaa536682fd3e4d1feed551c4e5d27bf85e03836bbb975e83e620529139d31644cee17f60f089b5b44a89513c336e262dd2a686e6460339ec3a8eacbdeda77bbb7d18bec45bb4288bd959dae803176ddf535b634e6ea9367fffb5f811a10c540a4eacbd8ceb9a4b5631a1b64dfa7ac9c4f52dff09f14d18652e9eb30b0b4e56a145c6d912a89c5442a0673b83728cf6cbc527e3004eeaf609573edb2f2844c1ec68a1cf97917a9213ce695eb3624a32f7";
        final String dataDate = "2019-07-29T06:29:00.123Z";
        JsonObject transaction = new JsonObject();

        JsonArray originator_addrs = new JsonArray();
        originator_addrs.add("3KvJ1uHPShhEAWyqsBEzhfXyeh1TXKAd7D");

        JsonArray beneficiary_addrs = new JsonArray();
        beneficiary_addrs.add("3F4ReDwiMLu8LrAiXwwD2DhH8U9xMrUzUf");

        transaction.addProperty("originator_vasp_code", "VASPUSNY1");
        transaction.add("originator_addrs", originator_addrs);
        transaction.addProperty("beneficiary_vasp_code", "VASPUSNY2");
        transaction.add("beneficiary_addrs", beneficiary_addrs);
        transaction.addProperty("transaction_currency", "0x80000000");
        transaction.addProperty("amount", 0.973);

        final String callbackSig = "1882c95212b5eeef27538558bbf53b84cb6518ee5a4b5b4230871cdf2290282963157bb846885564f4543ed85c6daab10f22010ca211ee63e8b1d00b7e942a85";
        final Callback callback = new Callback(callbackSig, CALLBACK_URL);
        PermissionRequest permReq = new PermissionRequest(permReqSig, privateInfo, transaction, dataDate);
        JsonObject obj = API_UTIL.postPermissionRequest(permReq, callback);
        System.out.println("return:" + obj.toString());
        String errorCode = obj.get("err_code").getAsString();
        assertEquals(errorCode, "010205"); // "Verify originator’s data signature failed."

    }
}
