package com.coolbitx.sygna.bridge;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ApiTest {

	private final static String API_KEY = "c35be610ddba08b0b9492a2d87dca506563a5484485029f9ca0d0d9b0578e246";
	private final static String DOMAIN = "https://api.sygna.io/staging/sb/";

	private final static API API_UTIL = new API(API_KEY, DOMAIN);

	@Test
	public void testGetVaspList() throws Exception {
		API_UTIL.getVASPList();
		API_UTIL.getVASPList(true);
		API_UTIL.getVASPList(false);
	}

	@Test
	public void testGetVaspByCode() throws Exception {
		final String vaspCode = "VASPJPJT4";
		String publicKey = API_UTIL.getVASPPublicKey(vaspCode);
		assertEquals(publicKey.length(), 130); // Uncompressed Public Key
		API_UTIL.getVASPPublicKey(vaspCode, false);
		API_UTIL.getVASPPublicKey(vaspCode, true);
	}
	
	@Test(expected = Exception.class)
	public void testGetVaspByCodeFailed() throws Exception{
		API_UTIL.getVASPPublicKey(" ");
		
	}
	
}
