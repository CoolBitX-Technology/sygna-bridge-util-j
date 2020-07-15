package com.coolbitx.sygna.bridge;

import com.coolbitx.sygna.bridge.enums.PermissionStatus;
import com.coolbitx.sygna.bridge.model.Field;

import org.junit.Test;

import com.coolbitx.sygna.config.BridgeConfig;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import static org.junit.Assert.assertEquals;

public class ApiTest {

    private final static String DOMAIN = BridgeConfig.SYGNA_BRIDGE_API_TEST_DOMAIN;
    private final static String ORIGINATOR_API_KEY = "a973dc6b71115c6126370191e70fe84d87150067da0ab37616eecd3ae16e288d";
    private final static String BENEFICIARY_API_KEY = "b94c6668bbdf654c805374c13bc7b675f00abc50ec994dbce322d7fb0138c875";
    private final static String CALLBACK_URL = "https://api.sygna.io/v2/bridge/";

    @Test
    public void testGetVaspList() throws Exception {
//        API api = new API(ORIGINATOR_API_KEY, DOMAIN);
//        api.getVASPList();
//        api.getVASPList(true);
//        api.getVASPList(false);
    }
//    @Test
//    public void testGetVaspByCode() throws Exception {
//        API api = new API(ORIGINATOR_API_KEY, DOMAIN);
//        String vaspCode = "VASPJPJT4";
//        String publicKey = api.getVASPPublicKey(vaspCode);
//        assertEquals(publicKey.length(), 130); // Uncompressed Public Key
//        api.getVASPPublicKey(vaspCode, false);
//        api.getVASPPublicKey(vaspCode, true);
//    }
//    @Test(expected = Exception.class)
//    public void testGetVaspByCodeFailed() throws Exception {
//        API api = new API(ORIGINATOR_API_KEY, DOMAIN);
//        api.getVASPPublicKey(" ");
//    }
//    @Test
//    public void testPostTransactionId() throws Exception {
//        String txId = "1a0c9bef489a136f7e05671f7f7fada2b9d96ac9f44598e1bcaa4779ac564dcd";
//        String transferId = "eeac79bb6ad673bfb4444b3bed1191c4b084270445becb7fdc2af7a80bb66aab";
//        String signature = "7e440ed0a978d05863d9b5fd674f4dbba264a1ce5fed72cd9bb2bfb16b4deaba54152d2b61f59d0f1ebf7e16fd6f575260851e9f5a1496e8f0f0805faa2f277a";
//        
//        JsonObject transactionID = new JsonObject();
//        transactionID.addProperty(Field.TRANSFER_ID, transferId);
//        transactionID.addProperty(Field.TX_ID, txId);
//        transactionID.addProperty(Field.SIGNATURE, signature);
//        API api = new API(ORIGINATOR_API_KEY, DOMAIN);
//        
//        JsonObject obj = api.postTransactionId(transactionID);
//        System.out.println("return:" + obj.toString());
//        String errorCode = obj.get("err_code").getAsString();
//        assertEquals(errorCode, "010319");
//    }
//    @Test
//    public void testGetStatus() throws Exception {
//        String transferId = "eeac79bb6ad673bfb4444b3bed1191c4b084270445becb7fdc2af7a80bb66aab";
//        API api = new API(ORIGINATOR_API_KEY, DOMAIN);
//        JsonObject obj = api.getStatus(transferId);
//        System.out.println("return:" + obj.toString());
//        String errorCode = obj.get("err_code").getAsString();
//        assertEquals(errorCode, "010319");
//    }
//    @Test
//    public void testPostPermission() throws Exception {
//        String permissionStatus = PermissionStatus.ACCEPTED.getStatus();
//        String transferId = "7b70c6f935441941695af5033435941cacc909685669df73628e5c68e32f231f";
//        String signature = "cb93bb7add0a7475244870e54c5bf119eb642263cd07b1c5b91e3e14dca5f54f71d99e9e6a0387c88a7f1cbc3fd0932111cbbb5040f2b59986382ab03a3adf42";
//        
//        JsonObject permission = new JsonObject();
//        permission.addProperty(Field.TRANSFER_ID, transferId);
//        permission.addProperty(Field.PERMISSION_STATUS, permissionStatus);
//        permission.addProperty(Field.SIGNATURE, signature);
//        
//        API api = new API(BENEFICIARY_API_KEY, DOMAIN);
//        JsonObject obj = api.postPermission(permission);
//        System.out.println("return:" + obj.toString());
//        String errorCode = obj.get("err_code").getAsString();
//        assertEquals(errorCode, "010319"); // "The transfer does not exist."
//    }
//    @Test
//    public void testPostPermissionRequest() throws Exception {
//        String permReqSig = "9771205efb0b9351e0128c7a5a7328859f08df584a54f58a4827dd4bf89063db6447dbcf1707e415d820e1040300431b4f9bb2bef9497ec314b129714a2074ea";
//        String privateInfo = "0405a39f02fb74cb0a748ff70adf0e4b7a8910befbaa536682fd3e4d1feed551c4e5d27bf85e03836bbb975e83e620529139d31644cee17f60f089b5b44a89513c336e262dd2a686e6460339ec3a8eacbdeda77bbb7d18bec45bb4288bd959dae803176ddf535b634e6ea9367fffb5f811a10c540a4eacbd8ceb9a4b5631a1b64dfa7ac9c4f52dff09f14d18652e9eb30b0b4e56a145c6d912a89c5442a0673b83728cf6cbc527e3004eeaf609573edb2f2844c1ec68a1cf97917a9213ce695eb3624a32f7";
//        String dataDate = "2019-07-29T06:29:00.123Z";
//        String callbackSig = "825a820f4e331acc6f53a0b745b7caba139c922131f9356f7b4d914040b9d3fa469764289e81cf989a5c143aa9f0ae88fc1357ca81584041b09639faab178672";
//
//        JsonObject originatorAddr = new JsonObject();
//        originatorAddr.addProperty(Field.ADDRESS, "rAPERVgXZavGgiGv6xBgtiZurirW2yAmY");
//
//        JsonObject originatorAddrExtraInfo = new JsonObject();
//        originatorAddrExtraInfo.addProperty("tag", "123456");
//
//        JsonArray originatorAddrExtraInfoArray = new JsonArray();
//        originatorAddrExtraInfoArray.add(originatorAddrExtraInfo);
//        
//        originatorAddr.add(Field.ADDR_EXTRA_INFO, originatorAddrExtraInfoArray);
//
//        JsonArray originatorAddrs = new JsonArray();
//        originatorAddrs.add(originatorAddr);
//
//        JsonObject originatorVASP = new JsonObject();
//        originatorVASP.addProperty(Field.VASP_CODE, "VASPUSNY1");
//        originatorVASP.add(Field.ADDRS, originatorAddrs);
//
//        JsonObject beneficiaryAddr = new JsonObject();
//        beneficiaryAddr.addProperty(Field.ADDRESS, "rU2mEJSLqBRkYLVTv55rFTgQajkLTnT6mA");
//
//        JsonArray beneficiaryAddrs = new JsonArray();
//        beneficiaryAddrs.add(beneficiaryAddr);
//
//        JsonObject beneficiaryVASP = new JsonObject();
//        beneficiaryVASP.addProperty(Field.VASP_CODE, "VASPUSNY2");
//        beneficiaryVASP.add(Field.ADDRS, beneficiaryAddrs);
//
//        JsonObject transaction = new JsonObject();
//        transaction.add(Field.ORIGINATOR_VASP, originatorVASP);
//        transaction.add(Field.BENEFICIARY_VASP, beneficiaryVASP);
//        transaction.addProperty(Field.CURRENCY_ID, "sygna:0x80000090");
//        transaction.addProperty(Field.AMOUNT, "0.973");
//
//        JsonObject permissionRequestData = new JsonObject();
//        permissionRequestData.addProperty(Field.PRIVATE_INFO, privateInfo);
//        permissionRequestData.add(Field.TRANSACTION, transaction);
//        permissionRequestData.addProperty(Field.DATA_DT, dataDate);
//        permissionRequestData.addProperty(Field.SIGNATURE, permReqSig);
//
//        JsonObject permissionRequestCallback = new JsonObject();
//        permissionRequestCallback.addProperty(Field.CALLBACK_URL, CALLBACK_URL);
//        permissionRequestCallback.addProperty(Field.SIGNATURE, callbackSig);
//
//        JsonObject permissionRequest = new JsonObject();
//        permissionRequest.add(Field.DATA, permissionRequestData);
//        permissionRequest.add(Field.CALLBACK, permissionRequestCallback);
//
//        API api = new API(ORIGINATOR_API_KEY, DOMAIN);
//        JsonObject obj = api.postPermissionRequest(permissionRequest);
//        System.out.println("return:" + obj.toString());
//        String errorCode = obj.get("err_code").getAsString();
//        assertEquals(errorCode, "010211"); // No permission
//    }
//    @Test
//    public void testPostBeneficiaryEndpointUrl() throws Exception {
//        String vaspCode = "VASPUSNY1";
//        String callbackPermissionRequest_url
//                = "https://test-api.sygna.io/v2/bridge/transaction/permission-request";
//        String callbackTxidUrl = "https://test-api.sygna.io/v2/bridge/transaction/txid";
//        String callbackValidateAddrUrl = "https://test-api.sygna.io/v2/bridge/permission-request";
//
//        String signature = "8c56e537fcc3c48a1022c27a1a1862ddf13e57261b2e80d74e8337124d26e4f3096be9c3d0a2fdab1c036dd148a4ca3eeb6959fcf0fdcd865d4985431c2d20ef";
//
//        JsonObject beneficiaryEndpointUrl = new JsonObject();
//        beneficiaryEndpointUrl.addProperty(Field.VASP_CODE, vaspCode);
//        beneficiaryEndpointUrl.addProperty(Field.CALLBACK_PERMISSION_REQUEST_URL, callbackPermissionRequest_url);
//        beneficiaryEndpointUrl.addProperty(Field.CALLBACK_TXID_URL, callbackTxidUrl);
//        beneficiaryEndpointUrl.addProperty(Field.CALLBACK_VALIDATE_ADDR_URL, callbackValidateAddrUrl);
//        beneficiaryEndpointUrl.addProperty(Field.SIGNATURE, signature);
//
//        API api = new API(BENEFICIARY_API_KEY, DOMAIN);
//        JsonObject obj = api.postBeneficiaryEndpointUrl(beneficiaryEndpointUrl);
//        System.out.println("return:" + obj.toString());
//        String errorCode = obj.get("err_code").getAsString();
//        assertEquals(errorCode, "010207"); // Verify beneficiary's signature failed.
//    }
//    
//    @Test
//    public void testPostRety() throws Exception {
//        String vaspCode = "VASPUSNY4";
//
//        JsonObject retryObj = new JsonObject();
//        retryObj.addProperty(Field.VASP_CODE, vaspCode);
//
//        API api = new API(BENEFICIARY_API_KEY, DOMAIN);
//        JsonObject obj = api.postRetry(retryObj);
//        System.out.println("return:" + obj.toString());
//        String errorCode = obj.get("err_code").getAsString();
//        assertEquals(errorCode, "010211"); // No permission
//    }
//
//    @Test
//    public void testGetCurrencies() throws Exception {
//        String currencyId = "sygna:0x80000090";
//        String currencySymbol = "XRP";
//        String currencyName = "XRP";
//
//        API api = new API(ORIGINATOR_API_KEY, DOMAIN);
//        JsonObject currencies = api.getCurrencies(currencyId, currencySymbol, currencyName);
//        JsonArray supportedCoins = currencies.getAsJsonArray("supported_coins");
//
//        assertEquals(supportedCoins.size(), 1);
//        assertEquals(supportedCoins.get(0).getAsJsonObject().get("currency_id").getAsString(), currencyId);
//        assertEquals(supportedCoins.get(0).getAsJsonObject().get("currency_symbol").getAsString(), currencySymbol);
//        assertEquals(supportedCoins.get(0).getAsJsonObject().get("currency_name").getAsString(), currencyName);
//        assertEquals(supportedCoins.get(0).getAsJsonObject().get("is_active").getAsBoolean(), true);
//
//        currencyId = "sygna:0x80000000";
//        currencies = api.getCurrencies(currencyId, null, null);
//        supportedCoins = currencies.getAsJsonArray("supported_coins");
//
//        assertEquals(currencies.size(), 1);
//        assertEquals(supportedCoins.get(0).getAsJsonObject().get("currency_id").getAsString(), currencyId);
//        assertEquals(supportedCoins.get(0).getAsJsonObject().get("currency_symbol").getAsString(), "BTC");
//        assertEquals(supportedCoins.get(0).getAsJsonObject().get("currency_name").getAsString(), "Bitcoin");
//        assertEquals(supportedCoins.get(0).getAsJsonObject().get("is_active").getAsBoolean(), true);
//    }
}
