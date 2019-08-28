package com.coolbitx.sygna.bridge;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class CryptoTest {
	
	private final static String PUBLIC_KEY = "036410211aae9f73e8ba94674fbf53f9d83f6898ec6b32ea2cb39e9a8dbc6355c4";
	private final static String PRIVATE_KEY = "{{PRIVATE_KEY}}";
	private final static String CALLBACK_URL = "{{CALLBACK_URL}}";
	
	@Test
	public void testEncode() throws Exception {
		String sensitiveData = 
				"{" + 
				"    \"originator\": {" + 
				"        \"name\": \"Antoine Griezmann\"," + 
				"        \"date_of_birth\":\"1991-03-21\"" + 
				"    }," + 
				"    \"beneficiary\":{" + 
				"        \"name\": \"Leo Messi\"" + 
				"    }" + 
				"}";
		JsonObject sensitiveDataObj = new Gson().fromJson(sensitiveData, JsonObject.class);
		String privateInfo = Crypto.sygnaEncodePrivateObj(sensitiveDataObj, PUBLIC_KEY);
		JsonObject decodedPrivateInfo = Crypto.sygnaDecodePrivateObj(privateInfo, PRIVATE_KEY);
		boolean isEqual = decodedPrivateInfo.equals(sensitiveDataObj);
		assertEquals(isEqual, true);
	}
	
	@Test
	public void testSign() throws Exception {
		final String status = "ACCEPTED";
		final String privateInfo = "0405a39f02fb74cb0a748ff70adf0e4b7a8910befbaa536682fd3e4d1feed551c4e5d27bf85e03836bbb975e83e620529139d31644cee17f60f089b5b44a89513c336e262dd2a686e6460339ec3a8eacbdeda77bbb7d18bec45bb4288bd959dae803176ddf535b634e6ea9367fffb5f811a10c540a4eacbd8ceb9a4b5631a1b64dfa7ac9c4f52dff09f14d18652e9eb30b0b4e56a145c6d912a89c5442a0673b83728cf6cbc527e3004eeaf609573edb2f2844c1ec68a1cf97917a9213ce695eb3624a32f7";
		final String dataDate = "2019-07-29T06:29:00.123Z";
		final String txId = "1a0c9bef489a136f7e05671f7f7fada2b9d96ac9f44598e1bcaa4779ac564dcd";
		final String transferId = "eeac79bb6ad673bfb4444b3bed1191c4b084270445becb7fdc2af7a80bb66aab";
		final JsonObject transaction = new JsonObject();
		
		JsonArray originator_addrs = new JsonArray();
		originator_addrs.add("3KvJ1uHPShhEAWyqsBEzhfXyeh1TXKAd7D");
		
		transaction.addProperty("originator_vasp_code", "VASPUSNY1");
		transaction.add("originator_addrs", originator_addrs);
		transaction.addProperty("beneficiary_vasp_code", "VASPUSNY2");
		transaction.addProperty("beneficiary_addr", "3F4ReDwiMLu8LrAiXwwD2DhH8U9xMrUzUf");
		transaction.addProperty("transaction_currency", "0x80000000");
		transaction.addProperty("amount", 4);
		
		JsonObject signedObj = Crypto.signCallBack(CALLBACK_URL, PRIVATE_KEY);
		boolean isVerified = Crypto.verifyObject(signedObj, PUBLIC_KEY);
		assertEquals(isVerified, true);
		
		signedObj = Crypto.signPermission(transferId, status, PRIVATE_KEY);
		isVerified = Crypto.verifyObject(signedObj, PUBLIC_KEY);
		assertEquals(isVerified, true);
		
		signedObj = Crypto.signPermissionRequest(privateInfo, transaction, dataDate, PRIVATE_KEY);
		isVerified = Crypto.verifyObject(signedObj, PUBLIC_KEY);
		assertEquals(isVerified, true);
		
		signedObj = Crypto.signTxId(transferId, txId, PRIVATE_KEY);
		isVerified = Crypto.verifyObject(signedObj, PUBLIC_KEY);
		assertEquals(isVerified, true);
	}

}
