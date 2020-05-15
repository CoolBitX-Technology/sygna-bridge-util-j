package com.coolbitx.sygna.bridge;

import com.coolbitx.sygna.bridge.enums.PermissionStatus;
import com.coolbitx.sygna.bridge.enums.RejectCode;
import com.coolbitx.sygna.bridge.model.BeneficiaryEndpointUrl;

import org.junit.Test;

import com.coolbitx.sygna.bridge.model.Callback;
import com.coolbitx.sygna.bridge.model.Permission;
import com.coolbitx.sygna.bridge.model.PermissionRequest;
import com.coolbitx.sygna.bridge.model.Transaction;
import com.coolbitx.sygna.bridge.model.Vasp;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.net.MalformedURLException;
import java.text.ParseException;
import org.everit.json.schema.ValidationException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ApiTest {

    private final static String DOMAIN = "https://test-api.sygna.io/sb/";
    private final static String ORIGINATOR_API_KEY = "a973dc6b71115c6126370191e70fe84d87150067da0ab37616eecd3ae16e288d";
    private final static String BENEFICIARY_API_KEY = "b94c6668bbdf654c805374c13bc7b675f00abc50ec994dbce322d7fb0138c875";
    private final static String CALLBACK_URL = "https://api.sygna.io/api/v1.1.0/bridge/";

    @Test
    public void testGetVaspList() throws Exception {
        API api = new API(ORIGINATOR_API_KEY, DOMAIN);
        api.getVASPList();
        api.getVASPList(true);
        api.getVASPList(false);
    }

    @Test
    public void testGetVaspByCode() throws Exception {
        API api = new API(ORIGINATOR_API_KEY, DOMAIN);
        String vaspCode = "VASPJPJT4";
        String publicKey = api.getVASPPublicKey(vaspCode);
        assertEquals(publicKey.length(), 130); // Uncompressed Public Key
        api.getVASPPublicKey(vaspCode, false);
        api.getVASPPublicKey(vaspCode, true);
    }

    @Test(expected = Exception.class)
    public void testGetVaspByCodeFailed() throws Exception {
        API api = new API(ORIGINATOR_API_KEY, DOMAIN);
        api.getVASPPublicKey(" ");
    }

    @Test
    public void testGetSB() throws Exception {
        API api = new API(ORIGINATOR_API_KEY, DOMAIN);
        String url = DOMAIN + "api/v1/bridge/vasp";
        JsonObject obj = api.getSB(url);
        Gson gson = new Gson();
        gson.fromJson(obj, Vasp.class);
    }

    @Test
    public void testPostTransactionIdFailed() throws Exception {
        String txId = "1a0c9bef489a136f7e05671f7f7fada2b9d96ac9f44598e1bcaa4779ac564dcd";
        String transferId = "eeac79bb6ad673bfb4444b3bed1191c4b084270445becb7fdc2af7a80bb66aab";
        String signature = "7e440ed0a978d05863d9b5fd674f4dbba264a1ce5fed72cd9bb2bfb16b4deaba54152d2b61f59d0f1ebf7e16fd6f575260851e9f5a1496e8f0f0805faa2f277a";

        API api = new API(ORIGINATOR_API_KEY, DOMAIN);
        try {
            api.postTransactionId(new Transaction(transferId, txId, "123"));
            fail("expected exception was not occured.");
        } catch (Exception e) {
            assertEquals(e.getMessage(), "signature length should be 128");
        }

        try {
            api.postTransactionId(new Transaction("123", txId, signature));
            fail("expected exception was not occured.");
        } catch (Exception e) {
            assertEquals(e.getMessage(), "transferId length should be 64");
        }

        try {
            api.postTransactionId(new Transaction(transferId, "", signature));
            fail("expected exception was not occured.");
        } catch (Exception e) {
            assertEquals(e.getMessage(), "txid length should NOT be shorter than 1");
        }
    }

    @Test
    public void testPostTransactionId() throws Exception {
        String txId = "1a0c9bef489a136f7e05671f7f7fada2b9d96ac9f44598e1bcaa4779ac564dcd";
        String transferId = "eeac79bb6ad673bfb4444b3bed1191c4b084270445becb7fdc2af7a80bb66aab";
        String signature = "7e440ed0a978d05863d9b5fd674f4dbba264a1ce5fed72cd9bb2bfb16b4deaba54152d2b61f59d0f1ebf7e16fd6f575260851e9f5a1496e8f0f0805faa2f277a";
        Transaction tran = new Transaction(transferId, txId, signature);
        API api = new API(ORIGINATOR_API_KEY, DOMAIN);
        JsonObject obj = api.postTransactionId(tran);
        System.out.println("return:" + obj.toString());
        String errorCode = obj.get("err_code").getAsString();
        assertEquals(errorCode, "010319");
    }

    @Test
    public void testGetStatus() throws Exception {
        String transferId = "eeac79bb6ad673bfb4444b3bed1191c4b084270445becb7fdc2af7a80bb66aab";
        API api = new API(ORIGINATOR_API_KEY, DOMAIN);
        JsonObject obj = api.getStatus(transferId);
        System.out.println("return:" + obj.toString());
        String errorCode = obj.get("err_code").getAsString();
        assertEquals(errorCode, "010319");
    }

    @Test
    public void testpostPermissionFailed() throws Exception {
        String status = PermissionStatus.ACCEPTED.getStatus();
        String transferId = "7b70c6f935441941695af5033435941cacc909685669df73628e5c68e32f231f";
        String signature = "cb93bb7add0a7475244870e54c5bf119eb642263cd07b1c5b91e3e14dca5f54f71d99e9e6a0387c88a7f1cbc3fd0932111cbbb5040f2b59986382ab03a3adf42";
        RejectCode rejectCode = RejectCode.BVRC999;

        API api = new API(ORIGINATOR_API_KEY, DOMAIN);
        try {
            api.postPermission(new Permission("12345", transferId, status));
            fail("expected exception was not occured.");
        } catch (Exception e) {
            assertEquals(e.getMessage(), "signature length should be 128");
        }

        try {
            api.postPermission(new Permission(signature, "12345", status));
            fail("expected exception was not occured.");
        } catch (Exception e) {
            assertEquals(e.getMessage(), "transferId length should be 64");
        }

        try {
            api.postPermission(new Permission(signature, transferId, "12345"));
            fail("expected exception was not occured.");
        } catch (Exception e) {
            assertEquals(e.getMessage(), String.format("permissionStatus should be equal to one of the allowed values[%s,%s]",
                    PermissionStatus.ACCEPTED.getStatus(), PermissionStatus.REJECTED.getStatus()));
        }

        try {
            api.postPermission(new Permission(signature, transferId, status, 1583654299000l));
            fail("expected exception was not occured.");
        } catch (Exception e) {
            assertEquals(e.getMessage(), "expire_date should be at least 180 seconds away from the current time");
        }

        status = PermissionStatus.REJECTED.getStatus();
//        try {
//            api.postPermission(new Permission(signature, transferId, status));
//            fail("expected exception was not occured.");
//        } catch (Exception e) {
//            assertEquals(e.getMessage(), "rejectCode cannot be blank if permissionStatus is REJECTED");
//        }

//        try {
//            api.postPermission(new Permission(signature, transferId, status, rejectCode));
//            fail("expected exception was not occured.");
//        } catch (Exception e) {
//            assertEquals(e.getMessage(), "rejectMessage cannot be blank if rejectCode is BVRC999");
//        }

//        try {
//            api.postPermission(new Permission(signature, transferId, status, rejectCode, ""));
//            fail("expected exception was not occured.");
//        } catch (Exception e) {
//            assertEquals(e.getMessage(), "rejectMessage cannot be blank if rejectCode is BVRC999");
//        }
    }

    @Test
    public void testPostPermission() throws Exception {
        String status = "ACCEPTED";
        String transferId = "7b70c6f935441941695af5033435941cacc909685669df73628e5c68e32f231f";
        String signature = "cb93bb7add0a7475244870e54c5bf119eb642263cd07b1c5b91e3e14dca5f54f71d99e9e6a0387c88a7f1cbc3fd0932111cbbb5040f2b59986382ab03a3adf42";

        Permission perm = new Permission(signature, transferId, status);
        API api = new API(BENEFICIARY_API_KEY, DOMAIN);
        JsonObject obj = api.postPermission(perm);
        System.out.println("return:" + obj.toString());
        String errorCode = obj.get("err_code").getAsString();
        assertEquals(errorCode, "010215"); // "The transfer already has permission status"
    }

    @Test
    public void testpostPermissionRequestFailed() throws Exception {
        String permReqSig = "48f5220f45d65c7f586e5b147f3ebd7e1803c1a86183498b62d74151517311465a80a3d1b742df191ffaee6329192b6d63e49ea1598467c001cc732e8df6e566";
        String privateInfo = "0405a39f02fb74cb0a748ff70adf0e4b7a8910befbaa536682fd3e4d1feed551c4e5d27bf85e03836bbb975e83e620529139d31644cee17f60f089b5b44a89513c336e262dd2a686e6460339ec3a8eacbdeda77bbb7d18bec45bb4288bd959dae803176ddf535b634e6ea9367fffb5f811a10c540a4eacbd8ceb9a4b5631a1b64dfa7ac9c4f52dff09f14d18652e9eb30b0b4e56a145c6d912a89c5442a0673b83728cf6cbc527e3004eeaf609573edb2f2844c1ec68a1cf97917a9213ce695eb3624a32f7";
        String dataDate = "2019-07-29T06:29:00.123Z";
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

        String callbackSig = "a32fd8dc7e0eb143d3b4e9d590170962c59b9b4b2d927342182339bb375ce08d6b84fca5dd7a5d952332c78c45a2377d026dae0279871fb1847ad68acc61c155";

        API api = new API(ORIGINATOR_API_KEY, DOMAIN);
        try {
            api.postPermissionRequest(
                    new PermissionRequest("12345", privateInfo, transaction, dataDate),
                    new Callback(callbackSig, CALLBACK_URL)
            );
            fail("expected exception was not occured.");
        } catch (Exception e) {
            assertEquals(e.getMessage(), "signature length should be 128");
        }

        try {
            api.postPermissionRequest(
                    new PermissionRequest(permReqSig, "", transaction, dataDate),
                    new Callback(callbackSig, CALLBACK_URL)
            );
            fail("expected exception was not occured.");
        } catch (Exception e) {
            assertEquals(e.getMessage(), "privateInfo length should NOT be shorter than 1");
        }

        try {
            JsonObject cloneTransactionObject = transaction.deepCopy();
            cloneTransactionObject.remove("originator_vasp_code");
            api.postPermissionRequest(
                    new PermissionRequest(permReqSig, privateInfo, cloneTransactionObject, dataDate),
                    new Callback(callbackSig, CALLBACK_URL)
            );
            fail("expected exception was not occured.");
        } catch (Exception e) {
            boolean isValidationException = (e instanceof ValidationException);
            assertEquals(isValidationException, true);
        }

        try {
            api.postPermissionRequest(
                    new PermissionRequest(permReqSig, privateInfo, transaction, "123"),
                    new Callback(callbackSig, CALLBACK_URL)
            );
            fail("expected exception was not occured.");
        } catch (Exception e) {
            boolean isParseException = (e instanceof ParseException);
            assertEquals(isParseException, true);
        }

        try {
            api.postPermissionRequest(
                    new PermissionRequest(permReqSig, privateInfo, transaction, dataDate, 1583654299000l),
                    new Callback(callbackSig, CALLBACK_URL)
            );
            fail("expected exception was not occured.");
        } catch (Exception e) {
            assertEquals(e.getMessage(), "expire_date should be at least 180 seconds away from the current time");
        }

        try {
            api.postPermissionRequest(
                    new PermissionRequest(permReqSig, privateInfo, transaction, dataDate),
                    new Callback("123", CALLBACK_URL)
            );
            fail("expected exception was not occured.");
        } catch (Exception e) {
            assertEquals(e.getMessage(), "signature length should be 128");
        }

        try {
            api.postPermissionRequest(
                    new PermissionRequest(permReqSig, privateInfo, transaction, dataDate),
                    new Callback(callbackSig, "123")
            );
            fail("expected exception was not occured.");
        } catch (Exception e) {
            boolean isMalformedURLException = (e instanceof MalformedURLException);
            assertEquals(isMalformedURLException, true);
        }
    }

    @Test
    public void testPostPermissionRequest() throws Exception {
        String permReqSig = "48f5220f45d65c7f586e5b147f3ebd7e1803c1a86183498b62d74151517311465a80a3d1b742df191ffaee6329192b6d63e49ea1598467c001cc732e8df6e566";
        String privateInfo = "0405a39f02fb74cb0a748ff70adf0e4b7a8910befbaa536682fd3e4d1feed551c4e5d27bf85e03836bbb975e83e620529139d31644cee17f60f089b5b44a89513c336e262dd2a686e6460339ec3a8eacbdeda77bbb7d18bec45bb4288bd959dae803176ddf535b634e6ea9367fffb5f811a10c540a4eacbd8ceb9a4b5631a1b64dfa7ac9c4f52dff09f14d18652e9eb30b0b4e56a145c6d912a89c5442a0673b83728cf6cbc527e3004eeaf609573edb2f2844c1ec68a1cf97917a9213ce695eb3624a32f7";
        String dataDate = "2019-07-29T06:29:00.123Z";
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

        String callbackSig = "a32fd8dc7e0eb143d3b4e9d590170962c59b9b4b2d927342182339bb375ce08d6b84fca5dd7a5d952332c78c45a2377d026dae0279871fb1847ad68acc61c155";
        Callback callback = new Callback(callbackSig, CALLBACK_URL);
        API api = new API(ORIGINATOR_API_KEY, DOMAIN);
        PermissionRequest permReq = new PermissionRequest(permReqSig, privateInfo, transaction, dataDate);
        JsonObject obj = api.postPermissionRequest(permReq, callback);
        System.out.println("return:" + obj.toString());
        String errorCode = obj.get("err_code").getAsString();
        assertEquals(errorCode, "010211"); // No permission
    }

    @Test
    public void testPostBeneficiaryEndpointUrlFailed() throws Exception {
        String vaspCode = "VASPUSNY1";
        String callbackPermissionRequest_url
                = "https://api.sygna.io/api/v1.1.0/bridge/permission-request";
        String callbackTxidUrl = "https://api.sygna.io/api/v1.1.0/bridge/txid";
        String signature = "9bae1abb5864a5af4dbf3b5ca524fdc067b93247c7f120fd9056394290feb1d7016132a96a165992ad170346749b716a43efef01a6160bfebf26e1c2164ff02a";

        API api = new API(BENEFICIARY_API_KEY, DOMAIN);
        try {
            api.postBeneficiaryEndpointUrl(new BeneficiaryEndpointUrl("123", vaspCode, callbackPermissionRequest_url, callbackTxidUrl));
            fail("expected exception was not occured.");
        } catch (Exception e) {
            assertEquals(e.getMessage(), "signature length should be 128");
        }

        try {
            api.postBeneficiaryEndpointUrl(new BeneficiaryEndpointUrl(signature, null, callbackPermissionRequest_url, callbackTxidUrl));
            fail("expected exception was not occured.");
        } catch (Exception e) {
            assertEquals(e.getMessage(), "vaspCode length should NOT be shorter than 1");
        }

        try {
            api.postBeneficiaryEndpointUrl(new BeneficiaryEndpointUrl(signature, vaspCode, "123", callbackTxidUrl));
            fail("expected exception was not occured.");
        } catch (Exception e) {
            boolean isMalformedURLException = (e instanceof MalformedURLException);
            assertEquals(isMalformedURLException, true);
        }

        try {
            api.postBeneficiaryEndpointUrl(new BeneficiaryEndpointUrl(signature, vaspCode, callbackPermissionRequest_url, "123"));
            fail("expected exception was not occured.");
        } catch (Exception e) {
            boolean isMalformedURLException = (e instanceof MalformedURLException);
            assertEquals(isMalformedURLException, true);
        }

        try {
            api.postBeneficiaryEndpointUrl(new BeneficiaryEndpointUrl(signature, vaspCode, "", ""));
            fail("expected exception was not occured.");
        } catch (Exception e) {
            assertEquals(e.getMessage(), "Selecting one or more of the following property is mandatory: 'callbackPermissionRequestUrl', 'callbackTxIdUrl'");
        }
    }

    @Test
    public void testPostBeneficiaryEndpointUrl() throws Exception {
        String vaspCode = "VASPUSNY1";
        String callbackPermissionRequest_url
                = "https://test-api.sygna.io/sb/api/v1.1.0/bridge/transaction/permission-request";
        String callbackTxidUrl = "https://test-api.sygna.io/sb/api/v1.1.0/bridge/transaction/txid";
        String signature = "05a42299f6d97e96ac21ea2fd90dc4c9b1b42e553ea6e81111b8568a274fb1f6511fa2084eb28fb4a60a2dec3bd30ef987a7c399c04549b1a0f3bf5e6c99eb39";

        API api = new API(BENEFICIARY_API_KEY, DOMAIN);
        JsonObject obj = api.postBeneficiaryEndpointUrl(new BeneficiaryEndpointUrl(signature, vaspCode, callbackPermissionRequest_url, callbackTxidUrl));
        System.out.println("return:" + obj.toString());
        String status = obj.get("status").getAsString();
        assertEquals(status, "OK");
    }
}
