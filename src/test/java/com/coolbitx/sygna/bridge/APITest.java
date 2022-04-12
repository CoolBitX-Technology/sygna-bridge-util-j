package com.coolbitx.sygna.bridge;

import com.coolbitx.sygna.bridge.model.Field;
import com.coolbitx.sygna.config.BridgeConfig;
import com.coolbitx.sygna.util.StringUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class APITest {

	private final static String API_KEY = "{{API_KEY}}";
	private final static String PRIVATE_KEY = "{{PRIVATE_KEY}}";
	private final static String VASP_CODE = "{{VASP_CODE}}";
	private final static String CALLBACK_URL = "{{CALLBACK_URL}}";

	private API api = new API(API_KEY, BridgeConfig.SYGNA_BRIDGE_API_TEST_DOMAIN);

	@Test
	public void testGetVASP() throws Exception {
		if(API_KEY.equals("{{API_KEY}}")) {
			System.out.print("Please fill your credential and run the test again");
			assertEquals(true, true);
			return;
		}
		
		
		JsonArray vasps = api.getVASPList(false, false);

		System.out.printf("vasps = %s", vasps.toString());
		assertEquals(vasps.size() > 0, true);

		for (int i = 0; i < vasps.size(); i++) {
			JsonObject vasp = vasps.get(i).getAsJsonObject();
			String vaspCode = vasp.get("vasp_code").getAsString();
			assertEquals(vaspCode.length() > 0, true);

			String vaspName = vasp.get("vasp_name").getAsString();
			assertEquals(vaspName.length() > 0, true);

			String vaspPubkey = vasp.get("vasp_pubkey").getAsString();
			assertEquals(vaspPubkey.length() > 0, true);

			String requlatorStatus = vasp.get("regulatoryStatus").getAsString();
			assertEquals(requlatorStatus.equals("Regulated") || requlatorStatus.equals("Not Regulated Yet"), true);

		}
	}

	@Test
	public void testPostBeneficiaryURL() throws Exception {
		if(API_KEY.equals("{{API_KEY}}")) {
			System.out.print("Please fill your credential and run the test again");
			assertEquals(true, true);
			return;
		}
		JsonArray vasps = api.getVASPList(false, false);
		String callbackPermissionRequestUrl = CALLBACK_URL + "/permission-request";
		String callbackTxIdUrl = CALLBACK_URL + "/txid";
		String callbackValidateAddrUrl = CALLBACK_URL + "/address-validation";

		JsonObject beneficiaryEndpointUrl = new JsonObject();
		beneficiaryEndpointUrl.addProperty(Field.VASP_CODE, VASP_CODE);
		if (!StringUtil.isNullOrEmpty(callbackPermissionRequestUrl)) {
			beneficiaryEndpointUrl.addProperty(Field.CALLBACK_PERMISSION_REQUEST_URL, callbackPermissionRequestUrl);
		}
		if (!StringUtil.isNullOrEmpty(callbackTxIdUrl)) {
			beneficiaryEndpointUrl.addProperty(Field.CALLBACK_TXID_URL, callbackTxIdUrl);
		}
		if (!StringUtil.isNullOrEmpty(callbackValidateAddrUrl)) {
			beneficiaryEndpointUrl.addProperty(Field.CALLBACK_VALIDATE_ADDR_URL, callbackValidateAddrUrl);
		}

		JsonObject signedBeneficiaryEndpointUrl = Crypto.signBeneficiaryEndpointUrl(beneficiaryEndpointUrl,
				PRIVATE_KEY);

		JsonObject response = api.postBeneficiaryEndpointUrl(signedBeneficiaryEndpointUrl);

		System.out.printf("response = %s", response.toString());
		String status = response.get("status").getAsString();
		assertEquals(status.equals("OK"), true);

	}

}
