package com.coolbitx.sygna.util;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.Signature;
import java.security.interfaces.ECPublicKey;
import java.util.Arrays;

import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.ECKey.ECDSASignature;
import org.bitcoinj.core.Sha256Hash;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Encoding;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.util.encoders.Hex;

public class ECDSA {

    /**
     * Sign {@link StandardCharsets#UTF_8} message with private message
     *
     * @param message
     * @param privateKey
     * @return
     * @throws Exception
     */
    public static String sign(String message, String privateKey) throws Exception {

        byte[] priKeyByte = Hex.decode(privateKey);
        ECKey ecKey = ECKey.fromPrivate(priKeyByte);
        byte[] hashMsg = Sha256Hash.hash(message.getBytes(StandardCharsets.UTF_8));
        Sha256Hash hash = Sha256Hash.wrap(hashMsg);
        ECDSASignature sig = ecKey.sign(hash);
        byte[] res = sig.toCanonicalised().encodeToDER();
        return getRawSignature(res);
    }

    /**
     * Verify Message({@link StandardCharsets#UTF_8}) with signature and
     * publicKey.
     *
     * @param message
     * @param signature
     * @param publicKey
     * @return
     * @throws Exception
     */
    public static boolean verify(String message, String signature, String publicKey) throws Exception {
        Signature sign = Signature.getInstance("SHA256withECDSA");
        ECPublicKey ecPub = null;
        ecPub = ECIES.getPublicKeyFromBytes(Hex.decode(publicKey));
        sign.initVerify(ecPub);
        byte[] inputData;
        inputData = message.getBytes(StandardCharsets.UTF_8);
        sign.update(inputData);
        byte[] derSignature = getDerEncodeSignature(Hex.decode(signature));
        boolean verifyResult = sign.verify(derSignature);
        return verifyResult;
    }

    /**
     * Get ASN.1 DER Signature
     *
     * @param signature Raw(r|s) Signature
     * @return
     * @throws IOException
     */
    private static byte[] getDerEncodeSignature(byte[] signature) throws IOException {
        BigInteger r = new BigInteger(1, Arrays.copyOfRange(signature, 0, 32));
        BigInteger s = new BigInteger(1, Arrays.copyOfRange(signature, 32, 64));
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(new ASN1Integer(r));
        v.add(new ASN1Integer(s));
        return new DERSequence(v).getEncoded(ASN1Encoding.DER);
    }

    /**
     * Get R|S Signature
     *
     * @param derSignature ASN.1 DER
     * @return Hex String of Raw(r|s) Signature
     * @throws Exception
     */
    private static String getRawSignature(byte[] derSignature) throws Exception {
        ASN1Primitive asn1 = toAsn1Primitive(derSignature);
        String rawSignature = "";
        if (asn1 instanceof ASN1Sequence) {
            ASN1Sequence asn1Sequence = (ASN1Sequence) asn1;
            ASN1Encodable[] asn1Encodables = asn1Sequence.toArray();
            for (ASN1Encodable asn1Encodable : asn1Encodables) {
                ASN1Primitive asn1Primitive = asn1Encodable.toASN1Primitive();
                if (asn1Primitive instanceof ASN1Integer) {
                    ASN1Integer asn1Integer = (ASN1Integer) asn1Primitive;
                    BigInteger integer = asn1Integer.getValue();
                    String hexStr = StringUtil.leftPadWithZeroes(integer.toString(16), 64);
                    rawSignature += hexStr;
                }
            }
        }
        if (rawSignature.length() != 128) {
            System.out.printf("rawSignature:%s\n", rawSignature);
            throw new Exception("Expect signature length to be 128.");
        }
        return rawSignature;
    }

    private static ASN1Primitive toAsn1Primitive(byte[] data) throws Exception {
        try (ByteArrayInputStream inStream = new ByteArrayInputStream(data);
                ASN1InputStream asnInputStream = new ASN1InputStream(inStream);) {
            return asnInputStream.readObject();
        }
    }

}
