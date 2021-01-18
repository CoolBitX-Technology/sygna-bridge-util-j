package com.coolbitx.sygna.bridge;

import com.coolbitx.sygna.bridge.model.Field;
import com.coolbitx.sygna.config.BridgeConfig;
import com.coolbitx.sygna.model.NetkiMessages;
import com.coolbitx.sygna.util.ECDSA;
import com.coolbitx.sygna.util.ECIES;
import com.coolbitx.sygna.util.Validator;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.protobuf.util.JsonFormat;
import java.security.Security;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class Crypto {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * Encrypt private info object to hex string.
     *
     * @param data
     * @param publicKey
     * @return
     * @throws Exception
     */
    public static String encryptPrivateObj(JsonObject data, String publicKey) throws Exception {
        final String msgString = new Gson().toJson(data);
        return ECIES.encrypt(msgString, publicKey);
    }
    
     /**
     * Encrypt private info object to hex string.
     *
     * @param data
     * @param publicKey
     * @return
     * @throws Exception
     */
    public static String encryptPrivateObj(NetkiMessages.Originator data, String publicKey) throws Exception {
        String jsonString = JsonFormat.printer().print(data);
        JsonObject sensitiveDataObj = new Gson().fromJson(jsonString, JsonObject.class);
        return encryptPrivateObj(sensitiveDataObj, publicKey);
    }
    
    /**
     * Encrypt private info object to hex string.
     *
     * @param data
     * @param publicKey
     * @return
     * @throws Exception
     */
    public static String encryptPrivateObj(NetkiMessages.Beneficiary data, String publicKey) throws Exception {
        String jsonString = JsonFormat.printer().print(data);
        JsonObject sensitiveDataObj = new Gson().fromJson(jsonString, JsonObject.class);
        return encryptPrivateObj(sensitiveDataObj, publicKey);
    }

    /**
     * Decrypt private info from recipient server.
     *
     * @param privateMsg
     * @param privateKey
     * @return
     * @throws Exception
     */
    public static JsonObject decryptPrivateObj(String privateMsg, String privateKey) throws Exception {
        final String decrypted = ECIES.decrypt(privateMsg, privateKey);
        return new Gson().fromJson(decrypted, JsonObject.class);
    }

    /**
     * @see
     * <a href="https://developers.sygna.io/reference#bridgepermissionrequest-3">Bridge/PermissionRequest/data</a>
     *
     * @param permissionRequest
     * @param privateKey
     * @return signed permissionRequest data
     * @throws Exception
     */
    public static JsonObject signPermissionRequest(JsonObject permissionRequest,
            String privateKey) throws Exception {
        return signObject(permissionRequest, privateKey);
    }

    /**
     * @see
     * <a href="https://developers.sygna.io/reference#bridgepermissionrequest-3">Bridge/PermissionRequest/callback</a>
     *
     * @param callback
     * @param privateKey
     * @return signed callback data
     * @throws Exception
     */
    public static JsonObject signCallBack(JsonObject callback, String privateKey) throws Exception {
        return signObject(callback, privateKey);
    }

    /**
     * @see
     * <a href="https://developers.sygna.io/reference#bridgepermission-3">Bridge/Permission</a>
     *
     * @param permission
     * @param privateKey
     * @return signed permission data
     * @throws Exception
     */
    public static JsonObject signPermission(JsonObject permission, String privateKey)
            throws Exception {
        return signObject(permission, privateKey);
    }

    /**
     * @see
     * <a href="https://developers.sygna.io/reference#bridgetransactionid-3">Bridge/TransactionID</a>
     * @param transactionId
     * @param privateKey
     * @return signed txid data
     * @throws Exception
     */
    public static JsonObject signTxId(JsonObject transactionId, String privateKey)
            throws Exception {
        return signObject(transactionId, privateKey);
    }

    /**
     * Sign Objects.
     *
     * @param obj
     * @param privateKey
     * @return original object adding a signature field
     * @throws Exception
     */
    public static JsonObject signObject(JsonObject obj, String privateKey) throws Exception {
        Validator.validatePrivateKey(privateKey);
        obj.addProperty(Field.SIGNATURE, "");
        Gson gson = new Gson();
        final String signature = ECDSA.sign(gson.toJson(obj), privateKey);
        obj.addProperty(Field.SIGNATURE, signature);
        return obj;
    }

    /**
     * {@code publicKey} defaults to {@link BridgeConfig#SYGNA_BRIDGE_CENTRAL_PUBKEY}
     * {@link Crypto#verifyObject(JsonObject, String)}
     *
     * @param obj
     * @return
     * @throws Exception
     */
    public static boolean verifyObject(JsonObject obj) throws Exception {
        return verifyObject(obj, BridgeConfig.SYGNA_BRIDGE_CENTRAL_PUBKEY);
    }

    /**
     * Verify JSON Object with provided Public Key or default sygna bridge
     * Public Key
     *
     * @param obj
     * @param publicKey
     * @return
     * @throws Exception
     */
    public static boolean verifyObject(JsonObject obj, String publicKey) throws Exception {
        final String signature = obj.get(Field.SIGNATURE).getAsString();
        System.out.printf("Verify Signature:%s\n", signature);
        obj.addProperty(Field.SIGNATURE, "");
        Gson gson = new Gson();
        final String msg = gson.toJson(obj);
        System.out.printf("Message:\n%s\n", msg);
        return ECDSA.verify(msg, signature, publicKey);
    }

    /**
     * @see
     * <a href="https://developers.sygna.io/reference#bridgebeneficiaryendpointurl">Bridge/VASP/BeneficiaryEndpointUrl</a>
     *
     * @param beneficiaryEndpointUrl
     * @param privateKey
     * @return signed beneficiaryEndpointUrl data
     * @throws Exception
     */
    public static JsonObject signBeneficiaryEndpointUrl(JsonObject beneficiaryEndpointUrl, String privateKey) throws Exception {
        return signObject(beneficiaryEndpointUrl, privateKey);
    }
}
