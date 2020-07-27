package com.coolbitx.sygna.bridge;

import com.coolbitx.sygna.bridge.enums.PermissionStatus;
import com.coolbitx.sygna.bridge.enums.RejectCode;
import com.coolbitx.sygna.bridge.model.Field;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class CryptoTest {

    private final static String PUBLIC_KEY = "045b409c8c15fd82744ce4f7f86d65f27d605d945d4c4eee0e4e2515a3894b9d157483cc5e49c62c07b46cd59bc980445d9cf987622d66df20c6c3634f6eb05085";
    private final static String PRIVATE_KEY = "bf76d2680f23f6fc28111afe0179b8704c8e203a5faa5112f8aa52721f78fe6a";
    private final static String CALLBACK_URL = "https://api.sygna.io/v2/bridge/";

    @Test
    public void testEncryptAndDecrypt() throws Exception {
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
        String privateInfo = Crypto.encryptPrivateObj(sensitiveDataObj, PUBLIC_KEY);
        JsonObject decodedPrivateInfo = Crypto.decryptPrivateObj(privateInfo, PRIVATE_KEY);
        boolean isEqual = decodedPrivateInfo.equals(sensitiveDataObj);
        assertEquals(isEqual, true);
    }

    @Test
    public void testSignPermissionRequest() throws Exception {
        String privateInfo = "0405a39f02fb74cb0a748ff70adf0e4b7a8910befbaa536682fd3e4d1feed551c4e5d27bf85e";
        String dataDate = "2019-07-29T06:29:00.123Z";

        JsonObject originatorAddr = new JsonObject();
        originatorAddr.addProperty(Field.ADDRESS, "rAPERVgXZavGgiGv6xBgtiZurirW2yAmY");

        JsonArray originatorAddrs = new JsonArray();
        originatorAddrs.add(originatorAddr);

        JsonObject originatorVASP = new JsonObject();
        originatorVASP.addProperty(Field.VASP_CODE, "VASPUSNY1");
        originatorVASP.add(Field.ADDRS, originatorAddrs);

        JsonObject beneficiaryAddr = new JsonObject();
        beneficiaryAddr.addProperty(Field.ADDRESS, "rU2mEJSLqBRkYLVTv55rFTgQajkLTnT6mA");

        JsonArray beneficiaryAddrs = new JsonArray();
        beneficiaryAddrs.add(beneficiaryAddr);

        JsonObject beneficiaryVASP = new JsonObject();
        beneficiaryVASP.addProperty(Field.VASP_CODE, "VASPUSNY2");
        beneficiaryVASP.add(Field.ADDRS, beneficiaryAddrs);

        JsonObject transaction = new JsonObject();
        transaction.add(Field.ORIGINATOR_VASP, originatorVASP);
        transaction.add(Field.BENEFICIARY_VASP, beneficiaryVASP);
        transaction.addProperty(Field.CURRENCY_ID, "sygna:0x80000090");
        transaction.addProperty(Field.AMOUNT, "0.973");

        JsonObject permissionRequestData = new JsonObject();
        permissionRequestData.addProperty(Field.PRIVATE_INFO, privateInfo);
        permissionRequestData.add(Field.TRANSACTION, transaction);
        permissionRequestData.addProperty(Field.DATA_DT, dataDate);

        JsonObject cloneObj = permissionRequestData.deepCopy();
        JsonObject signedObj = Crypto.signPermissionRequest(cloneObj, PRIVATE_KEY);
        String signature = signedObj.get("signature").getAsString();

        boolean isVerified = Crypto.verifyObject(cloneObj, PUBLIC_KEY);
        assertEquals(isVerified, true);

        String expectedSignature = "d335f9ee28e50163a364c1954f58736615618975d3561c78010430d666ffded00abee0763915e68aa8bfa50a077153e89b0acb6cd42537fdfbb3e3adebdd7eff";
        assertEquals(signature, expectedSignature);

        JsonObject originatorAddrExtraInfo = new JsonObject();
        originatorAddrExtraInfo.addProperty("tag", "123456");

        JsonArray originatorAddrExtraInfoArray = new JsonArray();
        originatorAddrExtraInfoArray.add(originatorAddrExtraInfo);

        permissionRequestData
                .getAsJsonObject(Field.TRANSACTION)
                .getAsJsonObject(Field.ORIGINATOR_VASP)
                .getAsJsonArray(Field.ADDRS)
                .get(0)
                .getAsJsonObject().add(Field.ADDR_EXTRA_INFO, originatorAddrExtraInfoArray);

        cloneObj = permissionRequestData.deepCopy();
        signedObj = Crypto.signPermissionRequest(cloneObj, PRIVATE_KEY);
        signature = signedObj.get("signature").getAsString();

        isVerified = Crypto.verifyObject(signedObj, PUBLIC_KEY);
        assertEquals(isVerified, true);

        expectedSignature = "8f851abce04b5805206bc3ea0f59a6a55dd7e1c36de48efaf770d6da1fc4e357752b1d7a1744df862930a5303fdaaa502e000bab5933e9187e034cba5747528f";
        assertEquals(signature, expectedSignature);

        JsonObject beneficiaryAddrExtraInfo = new JsonObject();
        beneficiaryAddrExtraInfo.addProperty("message", "abcde");

        JsonArray beneficiarybeneficiaryAddrExtraInfoArray = new JsonArray();
        beneficiarybeneficiaryAddrExtraInfoArray.add(beneficiaryAddrExtraInfo);

        permissionRequestData
                .getAsJsonObject(Field.TRANSACTION)
                .getAsJsonObject(Field.BENEFICIARY_VASP)
                .getAsJsonArray(Field.ADDRS)
                .get(0)
                .getAsJsonObject().add(Field.ADDR_EXTRA_INFO, beneficiarybeneficiaryAddrExtraInfoArray);

        cloneObj = permissionRequestData.deepCopy();
        signedObj = Crypto.signPermissionRequest(cloneObj, PRIVATE_KEY);
        signature = signedObj.get("signature").getAsString();

        isVerified = Crypto.verifyObject(signedObj, PUBLIC_KEY);
        assertEquals(isVerified, true);

        expectedSignature = "eea80a206feba1163f5423e27efb4c4cc498876ee7502e12eb19bc5c403d6bd22bf38077706d94eeaf9e55bdfe8cc73b800ef96bb62595d3f7ec6ffbfe6e7acb";
        assertEquals(signature, expectedSignature);

        //add expire_date
        long expire_date = 4107667801000l;
        permissionRequestData.addProperty(Field.EXPIRE_DATE, expire_date);

        cloneObj = permissionRequestData.deepCopy();
        signedObj = Crypto.signPermissionRequest(cloneObj, PRIVATE_KEY);
        signature = signedObj.get("signature").getAsString();

        isVerified = Crypto.verifyObject(signedObj, PUBLIC_KEY);
        assertEquals(isVerified, true);

        expectedSignature = "83d755289cd7f08d359cec0619f9ef14b35c4b1eb6630cd6b3107bdfd94991763b238fb9ebcdb78662d11fa9e706896050a8aaa75e065afe65518dea2007b78a";
        assertEquals(signature, expectedSignature);

        //add need_validate_addr
        boolean need_validate_addr = true;
        permissionRequestData.addProperty(Field.NEED_VALIDATE_ADDR, need_validate_addr);

        cloneObj = permissionRequestData.deepCopy();
        signedObj = Crypto.signPermissionRequest(cloneObj, PRIVATE_KEY);
        signature = signedObj.get("signature").getAsString();

        isVerified = Crypto.verifyObject(signedObj, PUBLIC_KEY);
        assertEquals(isVerified, true);

        expectedSignature = "d7b60bfd16e58c0c5974a28eb28caaf812fae1854b1911ef52c4a375aeac5dfa363a9a8d508bbd9c850ab322e750621e7e59fa900b5d465ea9739d2bc1014b85";
        assertEquals(signature, expectedSignature);
    }

    @Test
    public void testSignCallback() throws Exception {
        JsonObject callback = new JsonObject();
        callback.addProperty(Field.CALLBACK_URL, CALLBACK_URL);
        JsonObject signedObj = Crypto.signCallBack(callback, PRIVATE_KEY);
        JsonObject cloneSignedObj = signedObj.deepCopy();
        boolean isVerified = Crypto.verifyObject(cloneSignedObj, PUBLIC_KEY);
        assertEquals(isVerified, true);

        String expectedSignature = "825a820f4e331acc6f53a0b745b7caba139c922131f9356f7b4d914040b9d3fa469764289e81cf989a5c143aa9f0ae88fc1357ca81584041b09639faab178672";
        String signature = signedObj.get("signature").getAsString();
        assertEquals(signature, expectedSignature);
    }

    @Test
    public void testSignPermissionIfAccpeted() throws Exception {
        String transfer_id = "6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b";

        JsonObject permission = new JsonObject();
        permission.addProperty(Field.TRANSFER_ID, transfer_id);
        permission.addProperty(Field.PERMISSION_STATUS, PermissionStatus.ACCEPTED.getStatus());

        JsonObject cloneObj = permission.deepCopy();
        JsonObject signedObj = Crypto.signPermission(cloneObj, PRIVATE_KEY);
        String signature = signedObj.get("signature").getAsString();

        boolean isVerified = Crypto.verifyObject(signedObj, PUBLIC_KEY);
        assertEquals(isVerified, true);

        String expectedSignature = "8500fde0806c6f8c94db848c4096cbc7deee3ee659b6dce3cb3accea8391c81122b46245801669b3da200e4311e8ef4012587be183bc00bed372204899a57595";
        assertEquals(signature, expectedSignature);

        //add expire_date
        long expire_date = 4107667801000l;
        permission.addProperty(Field.EXPIRE_DATE, expire_date);

        cloneObj = permission.deepCopy();
        signedObj = Crypto.signPermission(cloneObj, PRIVATE_KEY);
        signature = signedObj.get("signature").getAsString();

        isVerified = Crypto.verifyObject(signedObj, PUBLIC_KEY);
        assertEquals(isVerified, true);

        expectedSignature = "e4f0893278051c4b67a0e62fe85249c6a710374a1852aa3c19525193815721e74212601dc25ef52486d490efe49dd9a3d7a4a7dcaf3d40e995c9baed42bb5b9f";
        assertEquals(signature, expectedSignature);
    }

    @Test
    public void testSignPermissionIfRejected() throws Exception {
        String transfer_id = "6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b";

        JsonObject permission = new JsonObject();
        permission.addProperty(Field.TRANSFER_ID, transfer_id);
        permission.addProperty(Field.PERMISSION_STATUS, PermissionStatus.REJECTED.getStatus());
        permission.addProperty(Field.REJECT_CODE, RejectCode.BVRC001.getRejectCode());

        JsonObject cloneObj = permission.deepCopy();
        JsonObject signedObj = Crypto.signPermission(cloneObj, PRIVATE_KEY);
        String signature = signedObj.get("signature").getAsString();

        boolean isVerified = Crypto.verifyObject(signedObj, PUBLIC_KEY);
        assertEquals(isVerified, true);

        String expectedSignature = "d0aa0ef942207bfc2f478b72a654286ac8f99125c16c9969bb95da32aa374d0f235830398c2d35795f31d21958a9c3ee5eb6fd2f732efe363d2fd029e46b9243";
        assertEquals(signature, expectedSignature);

        //add reject_message
        String rejectMessage = "service_downtime";
        permission.addProperty(Field.REJECT_MESSAGE, rejectMessage);
        cloneObj = permission.deepCopy();

        signedObj = Crypto.signPermission(cloneObj, PRIVATE_KEY);
        signature = signedObj.get("signature").getAsString();

        isVerified = Crypto.verifyObject(signedObj, PUBLIC_KEY);
        assertEquals(isVerified, true);

        expectedSignature = "71bb4fdd606af346ff74f68c78b906848a44368fbc961c8b366e9d31494fefb24588157c7eeed6256bc3dae05263123e9cfd21f2e1c97c7b9f32cfae3429f654";
        assertEquals(signature, expectedSignature);

        //add expire_date
        long expire_date = 4107667801000l;
        permission.addProperty(Field.EXPIRE_DATE, expire_date);
        cloneObj = permission.deepCopy();

        signedObj = Crypto.signPermission(cloneObj, PRIVATE_KEY);
        signature = signedObj.get("signature").getAsString();

        isVerified = Crypto.verifyObject(signedObj, PUBLIC_KEY);
        assertEquals(isVerified, true);

        expectedSignature = "c91cf61918a9fd7f05ad57085ed4819125716920913f77deebd2198e4a7c1ade28c836559fecacb1c80a440750d144b7611851eb51e93564aa241fe7258207ba";
        assertEquals(signature, expectedSignature);
    }

    @Test
    public void testSignTxId() throws Exception {
        String txId = "1a0c9bef489a136f7e05671f7f7fada2b9d96ac9f44598e1bcaa4779ac564dcd";
        String transferId = "eeac79bb6ad673bfb4444b3bed1191c4b084270445becb7fdc2af7a80bb66aab";

        JsonObject transactionID = new JsonObject();
        transactionID.addProperty(Field.TRANSFER_ID, transferId);
        transactionID.addProperty(Field.TX_ID, txId);

        JsonObject signedObj = Crypto.signTxId(transactionID, PRIVATE_KEY);
        JsonObject cloneSignedObj = signedObj.deepCopy();
        boolean isVerified = Crypto.verifyObject(cloneSignedObj, PUBLIC_KEY);
        assertEquals(isVerified, true);

        String expectedSignature = "9c9def21dd6860dbeae18362115d2adb6c98fe1e655965d503af0f3d7ad893b03adebcedc8d5a6a0e71e9c5d32d00e518c6dd056cf095af976c66aaef490712b";
        String signature = signedObj.get("signature").getAsString();
        assertEquals(signature, expectedSignature);
    }

    @Test
    public void testSignBeneficiaryEndpointUrl() throws Exception {
        String vaspCode = "VASPUSNY1";
        String callbackPermissionRequestUrl = "https://test-api.sygna.io/v2/bridge/transaction/permission-request";
        String callbackTxIdUrl = "https://test-api.sygna.io/v2/bridge/transaction/txid";
        String callbackValidateAddrUrl = "https://test-api.sygna.io/v2/bridge/transaction/permission-request";

        JsonObject beneficiaryEndpointUrl = new JsonObject();
        beneficiaryEndpointUrl.addProperty(Field.VASP_CODE, vaspCode);
        beneficiaryEndpointUrl.addProperty(Field.CALLBACK_PERMISSION_REQUEST_URL, callbackPermissionRequestUrl);

        JsonObject cloneObj = beneficiaryEndpointUrl.deepCopy();
        JsonObject signedObj = Crypto.signBeneficiaryEndpointUrl(cloneObj, PRIVATE_KEY);
        String signature = signedObj.get("signature").getAsString();

        boolean isVerified = Crypto.verifyObject(signedObj, PUBLIC_KEY);
        assertEquals(isVerified, true);

        String expectedSignature = "bfcf8889f5ea3155b382ccf768362be5aa059ffbbf66028ebf4bf7e509b90a114c8f8188af0ec0cd7fed77d4ccac1dd3c1c6d9661fbd1525181fa820c6f08576";
        assertEquals(signature, expectedSignature);

        beneficiaryEndpointUrl.addProperty(Field.CALLBACK_TXID_URL, callbackTxIdUrl);
        cloneObj = beneficiaryEndpointUrl.deepCopy();
        signedObj = Crypto.signBeneficiaryEndpointUrl(cloneObj, PRIVATE_KEY);
        signature = signedObj.get("signature").getAsString();

        isVerified = Crypto.verifyObject(signedObj, PUBLIC_KEY);
        assertEquals(isVerified, true);

        expectedSignature = "d8685168506cc71ffc5294117ea1fa2269f6375ee7eec1fa9a50778a33e7aa4c11944f11bba64afb785d38c2edc810c251072e14deea76daabc899ccaad9d67f";
        assertEquals(signature, expectedSignature);

        beneficiaryEndpointUrl.addProperty(Field.CALLBACK_VALIDATE_ADDR_URL, callbackValidateAddrUrl);
        cloneObj = beneficiaryEndpointUrl.deepCopy();
        signedObj = Crypto.signBeneficiaryEndpointUrl(cloneObj, PRIVATE_KEY);
        signature = signedObj.get("signature").getAsString();

        isVerified = Crypto.verifyObject(signedObj, PUBLIC_KEY);
        assertEquals(isVerified, true);

        expectedSignature = "b2c74f7c1c6afdac52d6cfb27dd3692201e49e85bfeefe3bb8fc74629ff27d3e297fd1484f0011177b61e6253b2bebba7faca90e1fcd61097030f40ab7009a1a";
        assertEquals(signature, expectedSignature);
    }
}
