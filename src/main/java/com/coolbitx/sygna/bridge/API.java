package com.coolbitx.sygna.bridge;

import com.coolbitx.sygna.bridge.model.Field;
import com.coolbitx.sygna.config.BridgeConfig;
import com.coolbitx.sygna.net.HttpClient;
import com.coolbitx.sygna.util.StringUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
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
    public String getVASPPublicKey(String vaspCode, boolean validate, boolean isProd) throws Exception {
        final JsonArray vasps = getVASPList(validate, isProd);
        for (JsonElement item : vasps) {
            JsonObject itemObject = item.getAsJsonObject();
            if (vaspCode.equals(itemObject.get("vasp_code").getAsString())) {
                return itemObject.get("vasp_pubkey").getAsString();
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
    public JsonArray getVASPList() throws Exception {
        return getVASPList(true, false);
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
    public JsonArray getVASPList(boolean validate) throws Exception {
        return getVASPList(validate, false);
    }

    /**
     * Get list of registered VASP associated with publicKey.
     *
     * @param validate whether to validate returned vasp list data.
     * @param isProd environment is production or test
     * @return
     * @throws Exception
     */
    public JsonArray getVASPList(boolean validate, boolean isProd) throws Exception {
        final String url = this.domain + "v2/bridge/vasp";
        JsonObject obj = getSB(url);

        JsonArray vaspData = obj.getAsJsonArray("vasp_data");
        if (vaspData.size() <= 0) {
            throw new Exception(format("Request VASPs failed: %s", obj.toString()));
        }
        if (!validate) {
            return vaspData;
        }
        final boolean valid = Crypto.verifyObject(obj, isProd ? BridgeConfig.SYGNA_BRIDGE_CENTRAL_PUBKEY : BridgeConfig.SYGNA_BRIDGE_CENTRAL_PUBKEY_TEST);
        if (!valid) {
            throw new Exception(format("get VASP info error: invalid signature."));
        }
        return vaspData;
    }

    /**
     * Notify Sygna Bridge that you have confirmed specific permission Request
     * from other VASP. Should be called by Beneficiary Server
     *
     * @see
     * <a href="https://developers.sygna.io/reference#bridgepermission-3">Bridge/Permission</a>
     *
     * @param data
     * @return status
     * @throws Exception
     */
    public JsonObject postPermission(JsonObject data) throws Exception {
        final String url = this.domain + "v2/bridge/transaction/permission";
        return postSB(url, data);
    }

    /**
     * Get detail of particular transaction permission request
     *
     * @see
     * <a href="https://developers.sygna.io/reference#bridgestatus-3">Bridge/Status</a>
     *
     * @param transferId
     * @return status
     * @throws Exception
     */
    public JsonObject getStatus(String transferId) throws Exception {
        final String url = this.domain + "v2/bridge/transaction/status?transfer_id=" + transferId;
        return getSB(url);
    }

    /**
     * Should be called by Originator.
     *
     * @see
     * <a href="https://developers.sygna.io/reference#bridgepermissionrequest-3">Bridge/PermissionRequest</a>
     *
     * @param data
     * @return Unique transfer_id
     * @throws Exception
     */
    public JsonObject postPermissionRequest(JsonObject data) throws Exception {
        final String url = this.domain + "v2/bridge/transaction/permission-request";
        return postSB(url, data);
    }

    /**
     * Send broadcasted transaction id to Sygna Bridge for purpose of storage.
     *
     * @see
     * <a href="https://developers.sygna.io/reference#bridgetransactionid-3">Bridge/TransactionID</a>
     *
     * @param data
     * @return status
     * @throws Exception
     */
    public JsonObject postTransactionId(JsonObject data) throws Exception {
        final String url = this.domain + "v2/bridge/transaction/txid";
        return postSB(url, data);
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
        headers.addProperty("x-api-key", this.apiKey);
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
        headers.addProperty("x-api-key", this.apiKey);
        final JsonObject response = HttpClient.get(url, headers, BridgeConfig.HTTP_TIMEOUT);
        return response;
    }

    /**
     * revise beneficiary endpoint url
     *
     * @see
     * <a href="https://developers.sygna.io/reference#bridgebeneficiaryendpointurl">Bridge/VASP/BeneficiaryEndpointUrl</a>
     *
     * @param data
     * @return status
     * @throws Exception
     */
    public JsonObject postBeneficiaryEndpointUrl(JsonObject data) throws Exception {
        final String url = this.domain + "v2/bridge/vasp/beneficiary-endpoint-url";
        return postSB(url, data);
    }

    /**
     * retrieve the lost transfer requests
     *
     * @see
     * <a href="https://developers.sygna.io/reference#bridgeretry-3">Bridge/Retry</a>
     *
     * @param data
     * @return retryItems
     * @throws Exception
     */
    public JsonObject postRetry(JsonObject data) throws Exception {
        final String url = this.domain + "v2/bridge/transaction/retry";
        return postSB(url, data);
    }

    /**
     * Get supported currencies
     *
     * @see
     * <a href="https://developers.sygna.io/reference#bridgecurrencies">Bridge/Currencies</a>
     *
     * @param currencyId
     * @param currencySymbol
     * @param currencyName
     * @return supported currencies
     * @throws Exception
     */
    public JsonObject getCurrencies(String currencyId, String currencySymbol, String currencyName) throws Exception {
        StringBuilder queryStringBuilder = new StringBuilder();
        if (!StringUtil.isNullOrEmpty(currencyId)) {
            queryStringBuilder.append(Field.CURRENCY_ID)
                    .append("=")
                    .append(currencyId)
                    .append("&");
        }
        if (!StringUtil.isNullOrEmpty(currencySymbol)) {
            queryStringBuilder.append(Field.CURRENCY_SYMBOL)
                    .append("=")
                    .append(currencySymbol)
                    .append("&");
        }
        if (!StringUtil.isNullOrEmpty(currencyName)) {
            queryStringBuilder.append(Field.CURRENCY_NAME)
                    .append("=")
                    .append(currencyName)
                    .append("&");
        }
        String url = this.domain + "v2/bridge/transaction/currencies";
        if (queryStringBuilder.length() != 0) {
            queryStringBuilder = queryStringBuilder
                    .deleteCharAt(queryStringBuilder.length() - 1)
                    .insert(0, "?");
        }
        queryStringBuilder.insert(0, url);
        return getSB(queryStringBuilder.toString());
    }

}
