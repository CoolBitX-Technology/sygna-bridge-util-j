package com.coolbitx.sygna.bridge;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.coolbitx.sygna.util.ECDSA;

public class EcdsaTest {

    private final static String PRV_KEY = "87c6578c29c9d864ca795d2a095beee1aabef1d6b284df7ec1b5e624045ae3db";
    private final static String PUB_KEY = "04629dac91cbe671b38b20822f03fe39252a0f93505111c330fbf531af91f3a05e439ec27c4e8ad0b705408bbe9f1e225beeb2b1a33b1b7a23a20040a8c95fca61";
    private final static String PUB_KEY_F = "04559d91b7e516e8d10ceac09611f58932b6b7481860c3ca75ead31bdc27e3910f2a88def59ba57a952b1e67529a47f28b36742a23150ecbc1ec45ed11407605c0";

    @Test
    public void testSign() throws Exception {
        String signData = "{\"vasp_data\":[{\"vasp_code\":\"VASPJPJT4\",\"vasp_name\":\"VASP4 in Tokyo, Japan\",\"vasp_pubkey\":\"04670af26edc74b1ae4e4acb6cef65dc0c3914528296aa48a6412f00cf0576d735d99e7cdd9da3daaef6fded244553597be9272d6cd2065a52cc7157264a2a4836\"},{\"vasp_code\":\"VASPUSNY1\",\"vasp_name\":\"VASP1 in New York, USA\",\"vasp_pubkey\":\"048709ef46f46c7e89b58987b606dc54eda62f88424517667305d91b3e86b8847f1b44a9659831880a15885ec43a722f76c356ec0ee373a273a0a7900dcd077339\"},{\"vasp_code\":\"SVCEJPJT\",\"vasp_name\":\"SBIVC\",\"vasp_pubkey\":\"0480664cd8fd8c93f0220eb0c2bab467608d90a291ea037b9932387f56a656f35e9a976a1623232643f82b1da0cf5b9a3bf2bda2b5cf30e5ee85a0d5a2011ea4f1\"},{\"vasp_code\":\"VASPUSNY2\",\"vasp_name\":\"VASP2 in New York, USA\",\"vasp_pubkey\":\"04b1f14590a37c5c5fdcdc4f6d606eb383a79d5f6d72c210ec4fab47c2e9a59b4fd1149d8e8fa31ac1a04a9142cda2a479c642fb606eaac14c874fd7426e379f54\"},{\"vasp_code\":\"VASPJPJT3\",\"vasp_name\":\"VASP3 in Tokyo, Japan\",\"vasp_pubkey\":\"04247bc554740852792dee49b8359cb25c74f2335d2c6e2025cd0880a06c8da1d51d461f720046c84da1dab106dfdac0452c92f09de7022a4cef5c4f3f6f3064d5\"}],\"signature\":\"\"}";
        String sig = ECDSA.sign(signData, PRV_KEY);

        boolean positiveResult = ECDSA.verify(signData, sig, PUB_KEY);
        boolean negativeResult = ECDSA.verify(signData, sig, PUB_KEY_F);
        assertTrue(positiveResult);
        assertFalse(negativeResult);
    }

}
