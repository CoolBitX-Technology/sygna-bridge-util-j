package com.coolbitx.sygna.bridge;

import com.coolbitx.sygna.bridge.model.Field;
import com.coolbitx.sygna.bridge.model.Vasp;
import com.coolbitx.sygna.config.BridgeConfig;
import com.coolbitx.sygna.util.ECDSA;
import com.coolbitx.sygna.util.ECIES;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class Crypto {

	/**
	 * Encode private info object to hex string.
	 * 
	 * @param data
	 * @param publicKey
	 * @return
	 * @throws Exception
	 */
	public static String sygnaEncodePrivateObj(JsonObject data, String publicKey) throws Exception {
		final String msgString = new Gson().toJson(data);
		return ECIES.encode(msgString, publicKey);
	}

	/**
	 * Decode private info from recipient server.
	 * 
	 * @param privateMsg
	 * @param privateKey
	 * @return
	 * @throws Exception
	 */
	public static JsonObject sygnaDecodePrivateObj(String privateMsg, String privateKey) throws Exception {
		final String decoded = ECIES.decode(privateMsg, privateKey);
		return new Gson().fromJson(decoded, JsonObject.class);
	}

	/**
	 * 
	 * @param privateInfo
	 * @param transaction
	 * @param dataDt      TimeStamp in ISO format: yyyy-mm-ddThh:mm:ss[.mmm]+0000
	 * @param privateKey
	 * @return { {@link Field#PRIVATE_INFO}: {@link String},
	 *         {@link Field#TRANSACTION}: {@link JsonObject}, {@link Field#DATA_DT:
	 *         {@link String} }
	 * @throws Exception
	 */
	public static JsonObject signPermissionRequest(String privateInfo, JsonObject transaction, String dataDt,
			String privateKey) throws Exception {
		JsonObject obj = new JsonObject();
		obj.addProperty(Field.PRIVATE_INFO, privateInfo);
		obj.add(Field.TRANSACTION, transaction);
		obj.addProperty(Field.DATA_DT, dataDt);
		return signObject(obj, privateKey);
	}

	/**
	 * @param callbackUrl
	 * @param privateKey
	 * @return { {@link Field#CALL_BACK_URL}: {@link String} }
	 * @throws Exception
	 */
	public static JsonObject signCallBack(String callbackUrl, String privateKey) throws Exception {
		JsonObject obj = new JsonObject();
		obj.addProperty(Field.CALL_BACK_URL, callbackUrl);
		return signObject(obj, privateKey);
	}

	/**
	 * @param transferId
	 * @param permissionStatus
	 * @param privateKey
	 * @return { {@link Field#TRANSFER_ID}: {@link String},
	 *         {@link Field#PERMISSION_STATUS}: {@link String},
	 *         {@link Field#FIELD_SIGNATURE: {@link String} }
	 * @throws Exception
	 */
	public static JsonObject signPermission(String transferId, String permissionStatus, String privateKey)
			throws Exception {
		JsonObject obj = new JsonObject();
		obj.addProperty(Field.TRANSFER_ID, transferId);
		obj.addProperty(Field.PERMISSION_STATUS, permissionStatus);
		return signObject(obj, privateKey);
	}

	/**
	 * @param transferId
	 * @param txId
	 * @param privateKey
	 * @return { {@link Field#TRANSFER_ID}: {@link String}, {@link Field#TX_ID}:
	 *         {@link String}, {@link Field#FIELD_SIGNATURE}: {@link String} }
	 * @throws Exception
	 */
	public static JsonObject signTxId(String transferId, String txId, String privateKey) throws Exception {
		JsonObject obj = new JsonObject();
		obj.addProperty(Field.TRANSFER_ID, transferId);
		obj.addProperty(Field.TX_ID, txId);
		return signObject(obj, privateKey);
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
		obj.addProperty(Field.SIGNATURE, "");
		Gson gson = new Gson();
		final String signature = ECDSA.sign(gson.toJson(obj), privateKey);
		obj.addProperty(Field.SIGNATURE, signature);
		return obj;
	}

	/**
	 * {@code publicKey} defaults to
	 * {@link BridgeConfig#SYGNA_BRIDGE_CENTRAL_PUBKEY}
	 * 
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
	 * Verify JSON Object with provided Public Key or default sygna bridge Public Key
	 * 
	 * @param obj
	 * @param publicKey
	 * @return
	 * @throws Exception
	 */
	public static boolean verifyObject(JsonObject obj, String publicKey) throws Exception {
		final String signature = obj.get(Field.SIGNATURE).getAsString();
		System.out.printf("Verify Signature:%s\n", signature);
		obj.addProperty(Vasp.class.getDeclaredField(Field.SIGNATURE).getName(), "");
		Gson gson = new Gson();
		final String msg = gson.toJson(obj);
		System.out.printf("Message:\n%s\n", msg);
		return ECDSA.verify(msg, signature, publicKey);
	}

}
