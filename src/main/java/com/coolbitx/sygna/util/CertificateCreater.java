package com.coolbitx.sygna.util;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.PEMWriter;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.security.*;
import java.io.*;
import java.util.*;

public class CertificateCreater {
    public static String gernateCertificate(String parentCertificate, JsonObject privateInfo) throws Exception {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        KeyPair keyPair = kpg.generateKeyPair();

        StringWriter stringWriter = new StringWriter();
        PemWriter pemWriter = new PemWriter(stringWriter);

        // val stringWriter = StringWriter()
        // val pemWriter = PemWriter(stringWriter)
        // when (objectToParse) {
        //     is PrivateKey -> pemWriter.writeObject(PemObject("PRIVATE KEY", objectToParse.encoded))
        //     is PublicKey -> pemWriter.writeObject(PemObject("PUBLIC KEY", objectToParse.encoded))
        //     is Certificate -> pemWriter.writeObject(PemObject("CERTIFICATE", objectToParse.encoded))
        // }
        // pemWriter.flush()
        // pemWriter.close()
        // return stringWriter.toString()



        return "";
    }

    /**
     * Generate a signed CSR for the provided principal.
     *
     * @param principal with the string for the CN in the CSR.
     * @param keyPair to sign the CSR.
     * @return the CSR generated.
     */
    private static PKCS10CertificationRequest generateCSR(String principal, KeyPair keyPair) throws Exception {
        JCESigner signer = new JCESigner(keyPair.getPrivate(), "SHA256withRSA");

        JcaPKCS10CertificationRequestBuilder csrBuilder = new JcaPKCS10CertificationRequestBuilder(
            new X500Name(principal), keyPair.getPublic()
        );
        ExtensionsGenerator extensionsGenerator = new ExtensionsGenerator();
        extensionsGenerator.addExtension(
            Extension.basicConstraints, true, new BasicConstraints(false)
        );
        csrBuilder.addAttribute(
            PKCSObjectIdentifiers.pkcs_9_at_extensionRequest,
            extensionsGenerator.generate()
        );

        return csrBuilder.build(signer);
    }

    /**
     * Method to create signature configuration for CSR.
     */
    private static class JCESigner implements ContentSigner {
        private String algorithm = "SHA256withRSA".toLowerCase();
        private Signature signature = null;
        private ByteArrayOutputStream outputStream = null;
        private HashMap<String, AlgorithmIdentifier> ALGORITHMS;

        public JCESigner(PrivateKey privateKey, String signatureAlgorithm) {
            this.ALGORITHMS = new HashMap<String, AlgorithmIdentifier>();
            this.ALGORITHMS.put("SHA256withRSA".toLowerCase(), new AlgorithmIdentifier(new ASN1ObjectIdentifier("1.2.840.113549.1.1.11")));
            this.ALGORITHMS.put("SHA1withRSA".toLowerCase(), new AlgorithmIdentifier(new ASN1ObjectIdentifier("1.2.840.113549.1.1.5")));
            try {
                this.outputStream = new ByteArrayOutputStream();
                this.signature = Signature.getInstance(signatureAlgorithm);
                this.signature.initSign(privateKey);
            } catch (GeneralSecurityException gse) {
                throw new IllegalArgumentException(gse.getMessage());
            }
        }

        public AlgorithmIdentifier getAlgorithmIdentifier() {
            AlgorithmIdentifier id = this.ALGORITHMS.get(algorithm);
            if(id == null) {
                throw new IllegalArgumentException("Does not support algorithm: $algorithm");
            }
            return id;
        }

        public OutputStream getOutputStream() {
            return this.outputStream;
        }

        public byte[] getSignature() {
            try {
                this.signature.update(this.outputStream.toByteArray());
                return this.signature.sign();
            } catch (GeneralSecurityException gse) {
                gse.printStackTrace();
                return null;
            }
        }
    }
}

