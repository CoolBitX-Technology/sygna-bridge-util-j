package com.coolbitx.sygna.util;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;
import org.bouncycastle.cert.X509v3CertificateBuilder;

import com.google.gson.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.io.*;
import java.util.*;
import java.math.BigInteger;

import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.cert.Certificate;

import com.coolbitx.sygna.model.AttestationCertificate;
import com.coolbitx.sygna.model.AttestationInformation;

public class CertificateCreater {
    public static List<AttestationCertificate> gernateCertificate(
        String parentCertificatePem, 
        String parentPrivateKeyPem, 
        JsonArray attestations
    ) throws Exception {
        // read "root" certificate
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        List<X509Certificate> rootCerts = (List<X509Certificate>) cf.generateCertificates(
            new ByteArrayInputStream(parentCertificatePem.getBytes("UTF_8"))
        );
        X509Certificate rootCert = rootCerts.get(0);
        JcaX509CertificateHolder rootCertJca = new JcaX509CertificateHolder(rootCert);

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(
            Base64.getDecoder().decode(parentPrivateKeyPem.getBytes("UTF_8"))
        );
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PrivateKey parentPrivateKey = kf.generatePrivate(keySpec);
        

        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);

        // Setup start date to yesterday and end date for 1 year validity
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        Date startDate = calendar.getTime();

        calendar.add(Calendar.YEAR, 1);
        Date endDate = calendar.getTime();

        List<AttestationCertificate> ret = new ArrayList<AttestationCertificate>();
        for (JsonElement attestation : attestations) {
            // json object to class
            Gson gson = new Gson();
            AttestationInformation attInfo = gson.fromJson(attestation, AttestationInformation.class);
            String principal = attInfo.attestation.toPrinciple(
                attInfo.data,
                attInfo.ivmsConstraint
            );

            KeyPair keyPair = kpg.generateKeyPair();
            PKCS10CertificationRequest csr = generateCSR(principal, keyPair);

            X509v3CertificateBuilder issuedCertBuilder = new X509v3CertificateBuilder(
                rootCertJca.getSubject(),
                new BigInteger(Long.toString(new SecureRandom().nextLong())),
                startDate,
                endDate,
                csr.getSubject(),
                csr.getSubjectPublicKeyInfo()
            );

            // Sign the new KeyPair with the root cert Private Key
            // ContentSigner csrContentSigner = csrBuilder.build(keyPair.getPrivate());
            X509CertificateHolder issuedCertHolder = issuedCertBuilder.build(
                new JCESigner(parentPrivateKey, "SHA256withRSA")
            );
            X509Certificate issuedCert = new JcaX509CertificateConverter().getCertificate(issuedCertHolder);

            // return signed cert and private key
            ret.add(new AttestationCertificate(
                attestation.getAsJsonObject().get("attestation").getAsString(), 
                objectToPemString(issuedCert), 
                objectToPemString(keyPair.getPrivate())
            ));
        }

        return ret;
    }

    public static JsonObject attestationCertificateToOwnersData(
        List<AttestationCertificate> attestationCertificates
    ) {
        for (AttestationCertificate attestationCertificate : attestationCertificates) {
            JsonObject obj = new JsonObject();
            obj.addProperty("attestation", attestationCertificate.attestation);
            obj.addProperty("pki_type", "x509+sha256");
            obj.addProperty("pki_data", attestationCertificate.certificatePem);

            // sign with private key

        }
        return new JsonObject();
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
     * Transform Object to String in PEM format.
     *
     * @param objectToParse one of PrivateKey / PublicKey / Certificate.
     * @return String in PEM format.
     */
    private static String objectToPemString(Object objectToParse) throws Exception {
        StringWriter stringWriter = new StringWriter();
        PemWriter pemWriter = new PemWriter(stringWriter);
        if(objectToParse instanceof PrivateKey) {
            PrivateKey pk = (PrivateKey) objectToParse;
            pemWriter.writeObject(new PemObject("PRIVATE KEY", pk.getEncoded()));
        }
        if(objectToParse instanceof PublicKey) {
            PublicKey pubkey = (PublicKey) objectToParse;
            pemWriter.writeObject(new PemObject("PUBLIC KEY", pubkey.getEncoded()));
        }
        if(objectToParse instanceof Certificate) {
            Certificate cert = (Certificate) objectToParse;
            pemWriter.writeObject(new PemObject("CERTIFICATE", cert.getEncoded()));
        }
        pemWriter.flush();
        pemWriter.close();
        return stringWriter.toString();
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

