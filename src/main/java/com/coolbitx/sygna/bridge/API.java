package com.coolbitx.sygna.bridge;

import java.util.ArrayList;

import com.coolbitx.sygna.bridge.model.Callback;
import com.coolbitx.sygna.bridge.model.Field;
import com.coolbitx.sygna.bridge.model.Permission;
import com.coolbitx.sygna.bridge.model.PermissionRequest;
import com.coolbitx.sygna.bridge.model.Transaction;
import com.coolbitx.sygna.bridge.model.Vasp;
import com.coolbitx.sygna.bridge.model.VaspDetail;
import com.coolbitx.sygna.config.BridgeConfig;
import com.coolbitx.sygna.json.CallbackSerializer;
import com.coolbitx.sygna.json.PermissionRequestSerializer;
import com.coolbitx.sygna.json.PermissionSerializer;
import com.coolbitx.sygna.json.TransactionSerializer;
import com.coolbitx.sygna.net.HttpClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import static java.lang.String.format;

public class API {

    private final String apiKey;
    private final String domain;

    public API(String apiKey, String sygnaBridgeDomain) {
        super();
        this.apiKey = apiKey;
        this.domain = sygnaBridgeDomain;
    }

    /**
     * {@code validate} defaults to {@link Boolean#TRUE}
     * {@code isProd} defaults to {@link Boolean#FALSE}
     *
     * @see API#getVASPPublicKey(String, boolean, boolean)
     *
     * @param vaspCode
     * @return
     * @throws Exception
     */
    public String getVASPPublicKey(String vaspCode) throws Exception {
        return getVASPPublicKey(vaspCode, true, false);
    }

    /**
     * {@code isProd} defaults to {@link Boolean#FALSE}
     *
     * @see API#getVASPPublicKey(String, boolean, boolean)
     *
     * @param vaspCode
     * @param validate whether to validate returned vasp list data.
     * @return
     * @throws Exception
     */
    public String getVASPPublicKey(String vaspCode, boolean validate) throws Exception {
        return getVASPPublicKey(vaspCode, validate, false);
    }
    
        /**
     * A Wrapper function of getVASPList to return specific VASP's Public Key.
     *
     * @param vaspCode
     * @param validate whether to validate returned vasp list data.
     * @param isProd environment is production or test
     * @return uncompressed Public Key
     * @throws Exception
     */
    public String getVASPPublicKey(String vaspCode, boolean validate,boolean isProd) throws Exception {
        final ArrayList<VaspDetail> vasps = getVASPList(validate);
        for (VaspDetail item : vasps) {
            if (vaspCode.equals(item.getVasp_code())) {
                return item.getVasp_pubkey();
            }
        }
        throw new Exception("Invalid vasp_code");
    }

    /**
     * {@code validate} defaults to {@link Boolean#TRUE}
     * {@code isProd} defaults to {@link Boolean#FALSE}
     *
     * @see API#getVASPList(boolean, boolean)
     *
     * @return
     * @throws Exception
     */
    public ArrayList<VaspDetail> getVASPList() throws Exception {
        return getVASPList(true,false);
    }

    /**
     * {@code isProd} defaults to {@link Boolean#FALSE}
     *
     * @see API#getVASPList(boolean, boolean)
     * 
     * @param validate whether to validate returned vasp list data.
     *
     * @return
     * @throws Exception
     */
    public ArrayList<VaspDetail> getVASPList(boolean validate) throws Exception {
        return getVASPList(validate,false);
    }
    
        /**
     * Get list of registered VASP associated with publicKey.
     *
     * @param validate whether to validate returned vasp list data.
     * @param isProd environment is production or test
     * @return
     * @throws Exception
     */
    public ArrayList<VaspDetail> getVASPList(boolean validate, boolean isProd) throws Exception {
        final String url = this.domain + "api/v1/bridge/vasp";
        Gson gson = new Gson();
        JsonObject obj = getSB(url);

        final Vasp result = gson.fromJson(obj, Vasp.class);
        if (result.getVasp_data().size() <= 0) {
            throw new Exception(format("Request VASPs failed: %s", gson.toJson(obj)));
        }
        if (!validate) {
            return result.getVasp_data();
        }
        final boolean valid = Crypto.verifyObject(obj, isProd ? BridgeConfig.SYGNA_BRIDGE_CENTRAL_PUBKEY : BridgeConfig.SYGNA_BRIDGE_CENTRAL_PUBKEY_TEST);
        if (!valid) {
            throw new Exception(format("get VASP info error: invalid signature."));
        }
        return result.getVasp_data();
    }

    /**
     * Notify Sygna Bridge that you have confirmed specific permission Request
     * from other VASP. Should be called by Beneficiary Server
     *
     * @param perm
     * @return
     * @throws Exception
     */
    public JsonObject postPermission(Permission perm) throws Exception {
        perm.check();
        final String url = this.domain + "api/v1/bridge/transaction/permission";
        Gson gson = new GsonBuilder().registerTypeAdapter(Permission.class, new PermissionSerializer()).create();
        return postSB(url, (JsonObject) gson.toJsonTree(perm, Permission.class));
    }

    /**
     * Get detail of particular transaction permission request
     *
     * @param transferId
     * @return
     * @throws Exception
     */
    public JsonObject getStatus(String transferId) throws Exception {
        final String url = this.domain + "api/v1/bridge/transaction/status?transfer_id=" + transferId;
        return getSB(url);
    }

    /**
     * Should be called by Originator.
     *
     * @param permReq Private sender info encoded by
     * {@link Crypto#sygnaEncodePrivateObj(JsonObject, String)}
     * @param callback
     * @return { {@link Field#TRANSFER_ID} : {@link String} }
     * @throws Exception
     */
    public JsonObject postPermissionRequest(PermissionRequest permReq, Callback callback) throws Exception {
        permReq.check();
        callback.check();
        final String url = this.domain + "api/v1/bridge/transaction/permission-request";
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(PermissionRequest.class, new PermissionRequestSerializer())
                .registerTypeAdapter(Callback.class, new CallbackSerializer())
                .create();
        JsonObject obj = new JsonObject();
        obj.add("data", gson.toJsonTree(permReq, PermissionRequest.class));
        obj.add("callback", gson.toJsonTree(callback, Callback.class));
        return postSB(url, obj);
    }

    /**
     * Send broadcasted transaction id to Sygna Bridge for purpose of storage.
     *
     * @param tx
     * @return
     * @throws Exception
     */
    public JsonObject postTransactionId(Transaction tx) throws Exception {
        tx.check();
        Gson gson = new GsonBuilder().registerTypeAdapter(Transaction.class, new TransactionSerializer()).create();
        final String url = this.domain + "api/v1/bridge/transaction/txid";
        return postSB(url, (JsonObject) gson.toJsonTree(tx, Transaction.class));
    }

    /**
     * HTTP Post request to Sygna Bridge
     *
     * @param url
     * @param obj
     * @return
     * @throws Exception
     */
    public JsonObject postSB(String url, JsonObject obj) throws Exception {
        JsonObject headers = new JsonObject();
        headers.addProperty("api_key", this.apiKey);
        final JsonObject response = HttpClient.post(url, headers, obj, BridgeConfig.HTTP_TIMEOUT);
        return response;
    }

    /**
     * *
     * HTTP GET request to Sygna Bridge
     *
     * @param url
     * @return
     * @throws Exception
     */
    public JsonObject getSB(String url) throws Exception {
        JsonObject headers = new JsonObject();
        headers.addProperty("api_key", this.apiKey);
        final JsonObject response = HttpClient.get(url, headers, BridgeConfig.HTTP_TIMEOUT);
        return response;
    }

}
