package com.coolbitx.sygna.util;

import static javax.xml.bind.DatatypeConverter.parseHexBinary;
import static javax.xml.bind.DatatypeConverter.printHexBinary;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.AlgorithmParameters;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.ECPrivateKeySpec;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.ECPointUtil;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;

public class ECIES {

	/**
	 * Sygna Bridge ECIES Encode.
	 * 
	 * @param msg       text to encode (in {@link StandardCharsets#UTF_8} plain
	 *                  text)
	 * @param publicKey recipient's uncompressed publicKey in hex form
	 * @return hex string of encoded private message
	 * @throws Exception
	 */
	public static String encode(String msg, String publicKey) throws Exception {
		String result = null;
		// add provider only if it's not in the JVM
		AlgorithmParameters parameters = AlgorithmParameters.getInstance("EC");
		parameters.init(new ECGenParameterSpec("secp256k1"));

		ECParameterSpec ecParameterSpec = parameters.getParameterSpec(ECParameterSpec.class);

		// Initialise our private key:
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");
		keyGen.initialize(ecParameterSpec, new SecureRandom());
		KeyPair keypair = keyGen.generateKeyPair();

		// random number x elliptic curve G
		ECPublicKey ecpubkey = (ECPublicKey) keypair.getPublic();
		String pubkey = "04" + StringUtil.leftPadWithZeroes(ecpubkey.getW().getAffineX().toString(16), 64)
				+ StringUtil.leftPadWithZeroes(ecpubkey.getW().getAffineY().toString(16), 64);

		KeyFactory kf = KeyFactory.getInstance("EC");

		// Read other's public key:
		ECPublicKey ecPub = getPublicKeyFromBytes(parseHexBinary(publicKey));

		String fullPub = "04" + StringUtil.leftPadWithZeroes(ecPub.getW().getAffineX().toString(16), 64)
				+ StringUtil.leftPadWithZeroes(ecPub.getW().getAffineY().toString(16), 64);
		System.out.println("Uncompressed PubKey:" + fullPub);

		// Perform key agreement
		KeyAgreement ka = KeyAgreement.getInstance("ECDH");
		ka.init(kf.generatePrivate(new PKCS8EncodedKeySpec(keypair.getPrivate().getEncoded())));
		ka.doPhase(ecPub, true);

		// Read shared secret
		byte[] sharedSecret = ka.generateSecret();

		// Derive a key from the shared secret
		MessageDigest hash = MessageDigest.getInstance("SHA-512");
		hash.update(sharedSecret);
		byte[] derivedKey = hash.digest();
		byte[] encryptKey = new byte[derivedKey.length / 2];
		byte[] macKey = new byte[derivedKey.length / 2];
		System.arraycopy(derivedKey, 0, encryptKey, 0, encryptKey.length);
		System.arraycopy(derivedKey, encryptKey.length, macKey, 0, macKey.length);

		// Encrypt
		byte[] plaintext = msg.getBytes(StandardCharsets.UTF_8);
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		byte[] iv = ByteBuffer.allocate(16).putInt(0).array();
		IvParameterSpec ivSpec = new IvParameterSpec(iv);

		SecretKeySpec encryptSpec = new SecretKeySpec(encryptKey, "AES");
		cipher.init(Cipher.ENCRYPT_MODE, encryptSpec, ivSpec);
		byte[] ciphertext = cipher.doFinal(plaintext);

		SecretKey secretKey = new SecretKeySpec(macKey, "HmacSHA1");
		Mac mac = Mac.getInstance(secretKey.getAlgorithm());
		mac.init(secretKey);
		mac.update(iv);
		mac.update(parseHexBinary(pubkey));
		mac.update(ciphertext);
		byte[] macResult = mac.doFinal();
		result = pubkey + printHexBinary(macResult) + printHexBinary(ciphertext);

		return result;
	}

	/**
	 * Sygna Bridge ECIES Decode.
	 * 
	 * @param encodedMsg whole hex string encrypted by
	 *                   {@link ECIES#encode(String, String)}.
	 * @param privateKey
	 * @return
	 * @throws Exception
	 */
	public static String decode(String encodedMsg, String privateKey) throws Exception {
		String result = null;
		AlgorithmParameters parameters = AlgorithmParameters.getInstance("EC");
		parameters.init(new ECGenParameterSpec("secp256k1"));

		ECParameterSpec ecParameterSpec = parameters.getParameterSpec(ECParameterSpec.class);

		KeyFactory kf = KeyFactory.getInstance("EC");

		byte[] iv = ByteBuffer.allocate(16).putInt(0).array();
		IvParameterSpec ivSpec = new IvParameterSpec(iv);
		// Decrypt
		String ephemX = encodedMsg.substring(2, 66);
		String ephemY = encodedMsg.substring(66, 130);
		String macResult = encodedMsg.substring(130, 130 + 40).toUpperCase();
		System.out.println("macResult:" + macResult);
		String ciphertext = encodedMsg.substring(130 + 40, encodedMsg.length());
		System.out.println("ciphertext:" + ciphertext);

		PrivateKey ecPriv = getPrivateKey(parseHexBinary(privateKey));

		// Read other's public key:
		ECPoint ecPoint = new ECPoint(new BigInteger(ephemX, 16), new BigInteger(ephemY, 16));
		ECPublicKeySpec ecPub = new ECPublicKeySpec(ecPoint, ecParameterSpec);
		// Perform key agreement
		KeyAgreement ka = KeyAgreement.getInstance("ECDH");
		ka.init(ecPriv);
		ka.doPhase(kf.generatePublic(ecPub), true);

		// Read shared secret
		byte[] sharedSecret = ka.generateSecret();

		// Derive a key from the shared secret
		MessageDigest hash = MessageDigest.getInstance("SHA-512");
		hash.update(sharedSecret);
		byte[] derivedKey = hash.digest();
		byte[] decryptKey = new byte[derivedKey.length / 2];
		byte[] macKey = new byte[derivedKey.length / 2];
		System.arraycopy(derivedKey, 0, decryptKey, 0, decryptKey.length);
		System.arraycopy(derivedKey, decryptKey.length, macKey, 0, macKey.length);

		SecretKey secretKey = new SecretKeySpec(macKey, "HmacSHA1");
		Mac mac = Mac.getInstance(secretKey.getAlgorithm());
		mac.init(secretKey);
		mac.update(iv);
		StringBuilder ephemPublicKey = new StringBuilder("04");
		ephemPublicKey.append(ephemX);
		ephemPublicKey.append(ephemY);
		mac.update(parseHexBinary(ephemPublicKey.toString()));
		mac.update(parseHexBinary(ciphertext));
		String bigMac = printHexBinary(mac.doFinal()).toUpperCase();
		if (!macResult.equals(bigMac)) {
			throw new Exception("Mac invalid");
		}
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		SecretKeySpec decryptSpec = new SecretKeySpec(decryptKey, "AES");
		cipher.init(Cipher.DECRYPT_MODE, decryptSpec, ivSpec);
		byte[] plaintext = cipher.doFinal(parseHexBinary(ciphertext));
		result = new String(plaintext, StandardCharsets.UTF_8);

		return result;
	}

	static PrivateKey getPrivateKey(byte[] privateKeyBytes) {
		try {
			KeyFactory kf = KeyFactory.getInstance("EC");
			AlgorithmParameters parameters = AlgorithmParameters.getInstance("EC");
			parameters.init(new ECGenParameterSpec("secp256k1"));

			ECParameterSpec ecParameterSpec = parameters.getParameterSpec(ECParameterSpec.class);
			BigInteger privNum = new BigInteger(1, privateKeyBytes);
			ECPrivateKeySpec ecPrivateKeySpec = new ECPrivateKeySpec(privNum, ecParameterSpec);

			return kf.generatePrivate(ecPrivateKeySpec);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getClass().getSimpleName() + "occurred when trying to get private key from raw bytes\n"
					+ e.toString());
			return null;
		}
	}
	
	static ECPublicKey getPublicKeyFromBytes(byte[] pubKey)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		ECNamedCurveParameterSpec spec = ECNamedCurveTable.getParameterSpec("secp256k1");
		KeyFactory kf = KeyFactory.getInstance("EC");
		ECNamedCurveSpec params = new ECNamedCurveSpec("secp256k1", spec.getCurve(), spec.getG(), spec.getN());
		ECPoint point = ECPointUtil.decodePoint(params.getCurve(), pubKey);
		String xpub = point.getAffineX().toString(16);
		String ypub = point.getAffineY().toString(16);
		System.out.println("X-pub:" + xpub);
		System.out.println("Y-pub:" + ypub);
		System.out.println("Compressed:" + printHexBinary(EC5Util.convertPoint(params, point, true).getEncoded(true)));
		ECPublicKeySpec pubKeySpec = new ECPublicKeySpec(point, params);
		ECPublicKey pk = (ECPublicKey) kf.generatePublic(pubKeySpec);
		return pk;
	}

}
