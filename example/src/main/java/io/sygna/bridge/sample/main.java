/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.sygna.bridge.sample;

import com.coolbitx.sygna.bridge.API;
import com.coolbitx.sygna.bridge.Crypto;
import com.coolbitx.sygna.bridge.enums.PermissionStatus;
import com.coolbitx.sygna.bridge.model.Field;
import com.coolbitx.sygna.config.BridgeConfig;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 *
 * @author kunming.liu
 */
public class main {

    public static final String ORIGINATOR_API_KEY = "{{ORIGINATOR_API_KEY}}";
    public static final String ORIGINATOR_PRIVATE_KEY = "{{ORIGINATOR_PRIVATE_KEY}}";
    public static final String ORIGINATOR_PUBLIC_KEY = "{{ORIGINATOR_PUBLIC_KEY}}";

    public static final String BENEFICIARY_API_KEY = "{{BENEFICIARY_API_KEY}}";
    public static final String BENEFICIARY_PRIVATE_KEY = "{{BENEFICIARY_PRIVATE_KEY}}";
    public static final String BENEFICIARY_PUBLIC_KEY = "{{BENEFICIARY_PUBLIC_KEY}}";

    public static final String SENSITIVE_DATA = "{" +
    "  \"originator\": {" +
    "    \"originator_persons\": [" +
    "      {" +
    "        \"natural_person\": {" +
    "          \"name\": {" +
    "            \"name_identifiers\": [" +
    "              {" +
    "                \"primary_identifier\": \"Wu Xinli\"," +
    "                \"name_identifier_type\": \"LEGL\"" +
    "              }" +
    "            ]" +
    "          }," +
    "          \"national_identification\": {" +
    "            \"national_identifier\": \"446005\"," +
    "            \"national_identifier_type\": \"RAID\"," +
    "            \"registration_authority\": \"RA000553\"" +
    "          }," +
    "          \"country_of_residence\": \"TZ\"" +
    "        }" +
    "      }" +
    "    ]," +
    "    \"account_numbers\": [" +
    "      \"3KvJ1uHPShhEAWyqsBEzhfXyeh1TXKAd7D\"" +
    "    ]" +
    "  }," +
    "  \"beneficiary\": {" +
    "    \"beneficiary_persons\": [" +
    "      {" +
    "        \"legal_person\": {" +
    "          \"name\": {" +
    "            \"name_identifiers\": [" +
    "              {" +
    "                \"legal_person_name\": \"ABC Limited\"," +
    "                \"legal_person_name_identifier_type\": \"LEGL\"" +
    "              }" +
    "            ]" +
    "          }" +
    "        }" +
    "      }" +
    "    ]," +
    "    \"account_numbers\": [" +
    "      \"3F4ReDwiMLu8LrAiXwwD2DhH8U9xMrUzUf\"" +
    "    ]" +
    "  }" +
    "}";

    public static void main(String args[]) throws Exception {
        testSignAndVerify();
//        testEncodeAndVerify();
//        testGetVASP();
//        testGetVASPPublicKey();
//        testGetCurrencies();
//        testPostBeneficiaryEndPointUrl();
//        testPostPermissionRequest();
//        testPostRetry();
//        testPostPermission();
//        testPostTransactionID();
    }

    private static void testSignAndVerify() throws Exception {
        JsonObject obj = new JsonObject();
        obj.addProperty("key", "value");

        JsonObject signedObject = Crypto.signObject(obj, ORIGINATOR_PRIVATE_KEY);
        System.out.printf("signedObject %s\n", signedObject.toString());

        boolean isCorrect = Crypto.verifyObject(obj, ORIGINATOR_PUBLIC_KEY);
        System.out.printf("isCorrect %s\n", isCorrect);
    }

    private static void testEncodeAndVerify() throws Exception {
        JsonParser parser = new JsonParser();
        JsonObject sensitiveData = parser.parse(SENSITIVE_DATA).getAsJsonObject();
        String privateInfo = Crypto.encryptPrivateObj(sensitiveData, ORIGINATOR_PUBLIC_KEY);
        JsonObject decryptedPrivateInfo = Crypto.decryptPrivateObj(privateInfo, ORIGINATOR_PRIVATE_KEY);
        boolean isEqual = decryptedPrivateInfo.equals(sensitiveData);

        System.out.printf("privateInfo %s\n", privateInfo);
        System.out.printf("decryptedPrivateInfo %s\n", decryptedPrivateInfo.toString());
        System.out.printf("isEqual %s\n", isEqual);
    }

    private static void testGetVASP() throws Exception {
        API api = new API(ORIGINATOR_API_KEY, BridgeConfig.SYGNA_BRIDGE_API_TEST_DOMAIN);
        JsonArray vasp = Util.getVASP(api, true, false);
        System.out.printf("vasp %s\n", vasp.toString());
    }

    private static void testGetVASPPublicKey() throws Exception {
        API api = new API(ORIGINATOR_API_KEY, BridgeConfig.SYGNA_BRIDGE_API_TEST_DOMAIN);
        String publicKey = Util.getVASPPbulicKey(api, "VASPUSNY1", true, false);
        System.out.printf("publicKey %s\n", publicKey);
    }

    private static void testGetCurrencies() throws Exception {
        API api = new API(ORIGINATOR_API_KEY, BridgeConfig.SYGNA_BRIDGE_API_TEST_DOMAIN);
        JsonObject currencies = Util.getCurrencies(api, "sygna:0x80000090", null, null);
        System.out.printf("currencies %s\n", currencies.toString());
    }

    private static void testPostBeneficiaryEndPointUrl() throws Exception {
        API api = new API(BENEFICIARY_API_KEY, BridgeConfig.SYGNA_BRIDGE_API_TEST_DOMAIN);

        String vaspCode = "VASPUSNY2";
        String callbackPermissionRequestUrl = "https://google.com";
        String callbackTxIdUrl = "https://stackoverflow.com";
        String callbackValidateAddrUrl = "https://github.com";

        JsonObject status = Util.postBeneficiaryEndpointUrl(api,
                vaspCode,
                callbackPermissionRequestUrl,
                callbackTxIdUrl,
                callbackValidateAddrUrl, BENEFICIARY_PRIVATE_KEY);

        System.out.printf("status %s\n", status);
    }

    private static void testPostPermissionRequest() throws Exception {
        API api = new API(ORIGINATOR_API_KEY, BridgeConfig.SYGNA_BRIDGE_API_TEST_DOMAIN);

        JsonParser parser = new JsonParser();
        JsonObject sensitiveData = parser.parse(SENSITIVE_DATA).getAsJsonObject();
        String privateInfo = Crypto.encryptPrivateObj(sensitiveData, BENEFICIARY_PUBLIC_KEY);

        String originatorVASPCode = "VASPUSNY1";

        JsonObject originatorAddrExtraInfo = new JsonObject();
        originatorAddrExtraInfo.addProperty("tag", "123");

        JsonArray originatorAddrExtraInfoArray = new JsonArray();
        originatorAddrExtraInfoArray.add(originatorAddrExtraInfo);

        JsonObject originatorAddr = new JsonObject();
        originatorAddr.addProperty(Field.ADDRESS, "r3kmLJN5D28dHuH8vZNUZpMC43pEHpaocV");
        originatorAddr.add(Field.ADDR_EXTRA_INFO, originatorAddrExtraInfoArray);

        JsonArray originatorAddrs = new JsonArray();
        originatorAddrs.add(originatorAddr);

        String beneficiaryVASPCode = "VASPUSNY2";

        JsonObject beneficiaryAddr = new JsonObject();
        beneficiaryAddr.addProperty(Field.ADDRESS, "rAPERVgXZavGgiGv6xBgtiZurirW2yAmY");

        JsonArray beneficiaryAddrs = new JsonArray();
        beneficiaryAddrs.add(beneficiaryAddr);

        String currencyId = "sygna:0x80000090";
        String amount = "12.5";

        String dataDt = "2020-07-13T05:56:53.088Z";
        String callbackUrl = "https://7434116d30db72c01911efd735cfefdc.m.pipedream.net";

        JsonObject status = Util.postPermissionRequest(
                api,
                privateInfo,
                originatorVASPCode,
                originatorAddrs,
                beneficiaryVASPCode,
                beneficiaryAddrs,
                currencyId,
                amount,
                dataDt,
                callbackUrl,
                ORIGINATOR_PRIVATE_KEY);

        System.out.printf("status %s\n", status);
    }

    private static void testPostRetry() throws Exception {
        API api = new API(BENEFICIARY_API_KEY, BridgeConfig.SYGNA_BRIDGE_API_TEST_DOMAIN);

        String vaspCode = "VASPUSNY2";

        JsonObject status = Util.postRetry(
                api,
                vaspCode);

        System.out.printf("status %s\n", status);
    }

    private static void testPostPermission() throws Exception {
        API api = new API(BENEFICIARY_API_KEY, BridgeConfig.SYGNA_BRIDGE_API_TEST_DOMAIN);

        String transferId = "3f24bc05dc15a63c194a381fefeb933205c2aeeee6d62832848f990995946f6e";

        JsonObject status = Util.postPermission(
                api,
                transferId,
                PermissionStatus.ACCEPTED,
                null,
                null,
                BENEFICIARY_PRIVATE_KEY);

        System.out.printf("status %s\n", status);
    }

    private static void testPostTransactionID() throws Exception {
        API api = new API(ORIGINATOR_API_KEY, BridgeConfig.SYGNA_BRIDGE_API_TEST_DOMAIN);

        String transferId = "3f24bc05dc15a63c194a381fefeb933205c2aeeee6d62832848f990995946f6e";
        String txId = "AE58B2A20401F86551740C594CAC964B963B2AFA963A0D4DAD61C3AC7A3FB37F";

        JsonObject status = Util.postTransactionID(
                api,
                transferId,
                txId,
                ORIGINATOR_PRIVATE_KEY);

        System.out.printf("status %s\n", status);
    }
}
