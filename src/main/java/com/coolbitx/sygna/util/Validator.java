package com.coolbitx.sygna.util;

import java.util.Calendar;

import com.coolbitx.sygna.bridge.enums.PermissionStatus;
import com.coolbitx.sygna.bridge.enums.RejectCode;
import com.coolbitx.sygna.config.BridgeConfig;
import com.google.gson.JsonObject;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;

public class Validator {

    public static void validateSignature(String signature) throws Exception {
        if (signature == null || signature.length() != 128) {
            throw new Exception("signature length should be 128");
        }
    }

    public static void validateTransferId(String transferId) throws Exception {
        if (transferId == null || transferId.length() != 64) {
            throw new Exception("transferId length should be 64");
        }
    }

    public static void validatePermissionStatus(String permissionStatus) throws Exception {
        if (permissionStatus == null || (!permissionStatus.equals(PermissionStatus.ACCEPTED.getStatus())
                && !permissionStatus.equals(PermissionStatus.REJECTED.getStatus()))) {
            throw new Exception(String.format("permissionStatus should be equal to one of the allowed values[%s,%s]",
                    PermissionStatus.ACCEPTED.getStatus(), PermissionStatus.REJECTED.getStatus()));
        }
    }

    public static void validateExpireDate(Calendar calendar, long expireDate) throws Exception {
        if (expireDate == 0l) {
            return;
        }
        Calendar cloneCalendar = (Calendar) calendar.clone();
        cloneCalendar.add(Calendar.SECOND, BridgeConfig.EXPIRE_DATE_MIN_OFFSET);
        if (expireDate < cloneCalendar.getTimeInMillis()) {
            throw new Exception("expire_date should be at least 180 seconds away from the current time");
        }
    }

    public static void validateRejectData(String permissionStatus, RejectCode rejectCode, String rejectMessage)
            throws Exception {
        if (!permissionStatus.equals(PermissionStatus.REJECTED.getStatus())) {
            return;
        }
        if (rejectCode == RejectCode.NULL) {
            throw new Exception("rejectCode cannot be blank if permissionStatus is REJECTED");
        }
        if (rejectCode == RejectCode.BVRC999 && StringUtil.isNullOrEmpty(rejectMessage)) {
            throw new Exception("rejectMessage cannot be blank if rejectCode is BVRC999");
        }

    }

    public static void validateTxid(String txid) throws Exception {
        if (StringUtil.isNullOrEmpty(txid)) {
            throw new Exception("txid length should NOT be shorter than 1");
        }
    }

    public static void validatePrivateInfo(String privateInfo) throws Exception {
        if (StringUtil.isNullOrEmpty(privateInfo)) {
            throw new Exception("privateInfo length should NOT be shorter than 1");
        }
    }

    public static void validateTransactionSchema(JsonObject transaction) throws Exception {
        JSONObject jsonObject = new JSONObject(transaction.toString());
        validateTransactionSchema(jsonObject);
    }

    public static void validateTransactionSchema(JSONObject transaction) throws Exception {
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("transaction_schema.json");
        JSONObject rawSchema = new JSONObject(new JSONTokener(inputStream));
        Schema schema = SchemaLoader.load(rawSchema);
        schema.validate(transaction);
    }

    public static void validateDataDate(String dataDate) throws Exception {
        if (StringUtil.isNullOrEmpty(dataDate)) {
            throw new Exception("dataDate length should NOT be shorter than 1");
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        sdf.parse(dataDate);
    }

    public static void validateUrl(String url) throws Exception {
        if (StringUtil.isNullOrEmpty(url)) {
            throw new Exception("url length should NOT be shorter than 1");
        }
        URL instance = new URL(url);
        System.out.print(String.format("validateUrl url = ", instance.toString()));
    }
}
