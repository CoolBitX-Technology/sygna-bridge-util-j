/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.sygna.bridge.sample;

import com.coolbitx.sygna.bridge.Crypto;
import com.coolbitx.sygna.bridge.API;
import com.coolbitx.sygna.bridge.enums.PermissionStatus;
import com.coolbitx.sygna.bridge.enums.RejectCode;
import com.coolbitx.sygna.bridge.model.Field;
import com.coolbitx.sygna.util.StringUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 *
 * @author kunming.liu
 */
public class Util {

    public static JsonObject postPermissionRequest(
            API api,
            String privateInfo,
            String originatorVASPCode,
            JsonArray originatorAddrs,
            String beneficiaryVASPCode,
            JsonArray beneficiaryAddrs,
            String currencyId,
            String amount,
            String dataDate,
            String callbackUrl,
            String privateKey) throws Exception {

        JsonObject originatorVASP = new JsonObject();
        originatorVASP.addProperty(Field.VASP_CODE, originatorVASPCode);
        originatorVASP.add(Field.ADDRS, originatorAddrs);

        JsonObject beneficiaryVASP = new JsonObject();
        beneficiaryVASP.addProperty(Field.VASP_CODE, beneficiaryVASPCode);
        beneficiaryVASP.add(Field.ADDRS, beneficiaryAddrs);

        JsonObject transaction = new JsonObject();
        transaction.add(Field.ORIGINATOR_VASP, originatorVASP);
        transaction.add(Field.BENEFICIARY_VASP, beneficiaryVASP);
        transaction.addProperty(Field.CURRENCY_ID, currencyId);
        transaction.addProperty(Field.AMOUNT, amount);

        JsonObject permissionRequestData = new JsonObject();
        permissionRequestData.addProperty(Field.PRIVATE_INFO, privateInfo);
        permissionRequestData.add(Field.TRANSACTION, transaction);
        permissionRequestData.addProperty(Field.DATA_DT, dataDate);

        JsonObject signedPermissionRequestData = Crypto.signPermissionRequest(permissionRequestData, privateKey);

        JsonObject callbackData = new JsonObject();
        callbackData.addProperty(Field.CALLBACK_URL, callbackUrl);

        JsonObject signedCallbackData = Crypto.signCallBack(callbackData, privateKey);

        JsonObject body = new JsonObject();
        body.add(Field.DATA, signedPermissionRequestData);
        body.add(Field.CALLBACK, signedCallbackData);

        return api.postPermissionRequest(body);
    }

    public static JsonObject postPermission(
            API api,
            String transferId,
            PermissionStatus permissionStatus,
            RejectCode rejectCode,
            String rejectMessage,
            String privateKey) throws Exception {

        JsonObject permissionData = new JsonObject();
        permissionData.addProperty(Field.TRANSFER_ID, transferId);
        permissionData.addProperty(Field.PERMISSION_STATUS, permissionStatus.getStatus());

        if (permissionStatus.equals(PermissionStatus.REJECTED)) {
            permissionData.addProperty(Field.REJECT_CODE, rejectCode.getRejectCode());
            permissionData.addProperty(Field.REJECT_MESSAGE, rejectMessage);
        }
        JsonObject signedPermissionData = Crypto.signPermissionRequest(permissionData, privateKey);

        return api.postPermission(signedPermissionData);
    }

    public static JsonObject postTransactionID(
            API api,
            String transferId,
            String transactionID,
            String privateKey) throws Exception {

        JsonObject txId = new JsonObject();
        txId.addProperty(Field.TRANSFER_ID, transferId);
        txId.addProperty(Field.TX_ID, transactionID);

        JsonObject signedTxId = Crypto.signTxId(txId, privateKey);

        return api.postTransactionId(signedTxId);
    }

    public static JsonObject postBeneficiaryEndpointUrl(
            API api,
            String vaspCode,
            String callbackPermissionRequestUrl,
            String callbackTxIdUrl,
            String callbackValidateAddrUrl,
            String privateKey) throws Exception {

        JsonObject beneficiaryEndpointUrl = new JsonObject();
        beneficiaryEndpointUrl.addProperty(Field.VASP_CODE, vaspCode);
        if (!StringUtil.isNullOrEmpty(callbackPermissionRequestUrl)) {
            beneficiaryEndpointUrl.addProperty(Field.CALLBACK_PERMISSION_REQUEST_URL, callbackPermissionRequestUrl);
        }
        if (!StringUtil.isNullOrEmpty(callbackTxIdUrl)) {
            beneficiaryEndpointUrl.addProperty(Field.CALLBACK_TXID_URL, callbackTxIdUrl);
        }
        if (!StringUtil.isNullOrEmpty(callbackValidateAddrUrl)) {
            beneficiaryEndpointUrl.addProperty(Field.CALLBACK_VALIDATE_ADDR_URL, callbackValidateAddrUrl);
        }

        JsonObject signedBeneficiaryEndpointUrl = Crypto.signBeneficiaryEndpointUrl(beneficiaryEndpointUrl, privateKey);

        return api.postBeneficiaryEndpointUrl(signedBeneficiaryEndpointUrl);
    }

    public static JsonObject postRetry(
            API api,
            String vaspCode) throws Exception {

        JsonObject retryData = new JsonObject();
        retryData.addProperty(Field.VASP_CODE, vaspCode);

        return api.postRetry(retryData);
    }

    public static JsonArray getVASP(
            API api,
            boolean isNeedValidate,
            boolean isProd) throws Exception {

        return api.getVASPList(isNeedValidate, isProd);
    }

    public static String getVASPPbulicKey(
            API api,
            String vaspCode,
            boolean isNeedValidate,
            boolean isProd) throws Exception {

        return api.getVASPPublicKey(vaspCode, isNeedValidate, isProd);
    }

    public static JsonObject getCurrencies(
            API api,
            String currencyId,
            String currencySymbol,
            String currencyName) throws Exception {

        return api.getCurrencies(currencyId, currencySymbol, currencyName);
    }
}
