package com.coolbitx.sygna.bridge;

import static org.junit.Assert.*;

import org.junit.Test;

import com.coolbitx.sygna.util.ECIES;

public class EciesTest {

    private final static String PRV_KEY = "{{PRIVATE_KEY}}";
    private final static String PUB_KEY = "045b409c8c15fd82744ce4f7f86d65f27d605d945d4c4eee0e4e2515a3894b9d157483cc5e49c62c07b46cd59bc980445d9cf987622d66df20c6c3634f6eb05085";

    @Test
    public void testDecode() throws Exception {
        String msg = "qwer";
        // Encoded by JavaScript Sygna Bridge Util
        String encoded = "045da28c71cc83e81b377891e04700bcd191bf600e44decefaa117c248754ec1fe30ddc9f4c373123b0fb8787d31372e9ec9889d27bfdb1fabc794a1f62b0606ba04859dc4ee6b3ee95fe28f7069d08e16de308440196df2f50eed83af3efb9f74d34c6a42";
        String decoded = ECIES.decode(encoded, PRV_KEY);
        assertEquals(decoded, msg);
    }

    @Test
    public void testEncode() throws Exception {
        String msg = "qwer";
        String encoded = ECIES.encode(msg, PUB_KEY);
        String decoded = ECIES.decode(encoded, PRV_KEY);
        assertEquals(decoded, msg);
    }

}
