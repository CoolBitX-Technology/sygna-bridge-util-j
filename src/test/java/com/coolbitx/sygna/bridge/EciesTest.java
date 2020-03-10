package com.coolbitx.sygna.bridge;

import static org.junit.Assert.*;

import org.junit.Test;

import com.coolbitx.sygna.util.ECIES;

public class EciesTest {

	private final static String PRV_KEY = "{{PRIVATE_KEY}}";
	private final static String PUB_KEY = "045b409c8c15fd82744ce4f7f86d65f27d605d945d4c4eee0e4e2515a3894b9d157483cc5e49c62c07b46cd59bc980445d9cf987622d66df20c6c3634f6eb05085";
	
        private final static String PUBLIC_KEY = "045b409c8c15fd82744ce4f7f86d65f27d605d945d4c4eee0e4e2515a3894b9d157483cc5e49c62c07b46cd59bc980445d9cf987622d66df20c6c3634f6eb05085";
        private final static String PRIVATE_KEY = "bf76d2680f23f6fc28111afe0179b8704c8e203a5faa5112f8aa52721f78fe6a";
    
	@Test
	public void testDecode() throws Exception {
		String msg = "qwer";
		// Encoded by JavaScript Sygna Bridge Util
		String encoded = "0457cbe091dec461f843701c3d12ff7e194d878823cc1c5512505bf2ea9ee54fccdd3e6e881adbab1ac50635146f27cd4bc3846bf2435c2176db6329e0fd6be195a497fbb60b79588c2e4c722f5c523557b25eca9655149182cf70b2bb193a8372558c74e6";
		String decoded = ECIES.decode(encoded, PRIVATE_KEY);
                
                if(msg.equals(decoded)){
                    System.out.print(String.format("decoded = %s",decoded));
                }else{
                    System.out.println(String.format("msg = %s",msg));
                    System.out.println(String.format("decoded111 = %s",decoded));
                }
		assertEquals(decoded, msg);
	}
	
	@Test
	public void testEncode() throws Exception {
		String msg = "qwer";
		String encoded = ECIES.encode(msg, PUBLIC_KEY);
		String decoded = ECIES.decode(encoded, PRIVATE_KEY);
		assertEquals(decoded, msg);
	}

}
