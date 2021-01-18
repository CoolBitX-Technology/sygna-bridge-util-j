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
import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.protobuf.ByteString;

import java.security.*;
import java.io.*;
import java.util.*;
import java.math.BigInteger;

import java.security.cert.X509Certificate;
import java.security.cert.Certificate;

import com.coolbitx.sygna.model.Attestation;
import com.coolbitx.sygna.model.AttestationCertificate;
import com.coolbitx.sygna.model.AttestationInformation;
import com.coolbitx.sygna.model.NetkiMessages;

public class CertificateCreater {

    private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";

    public static List<AttestationCertificate> generateCertificate(
            String parentCertificatePem,
            String parentPrivateKeyPem,
            JsonArray attestations
    ) throws Exception {
        // read "root" certificate
        X509Certificate rootCert = (X509Certificate) CertificateCreater.stringPemToObject(parentCertificatePem);
        JcaX509CertificateHolder rootCertJca = new JcaX509CertificateHolder(rootCert);
        PrivateKey parentPrivateKey = (PrivateKey) CertificateCreater.stringPemToObject(parentPrivateKeyPem);

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
                    attInfo.ivmsConstraints
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
                    new JCESigner(parentPrivateKey, SIGNATURE_ALGORITHM)
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

    private static NetkiMessages.Attestation generateAttestation(AttestationCertificate attestationCertificate) throws NoSuchAlgorithmException, Exception {
        NetkiMessages.Attestation.Builder messageAttestationUnsignedBuilder = NetkiMessages.Attestation.newBuilder()
                .setPkiType("x509+sha256")
                .setPkiData(ByteString.copyFrom(attestationCertificate.certificatePem.getBytes()))
                .setSignature(ByteString.copyFrom("".getBytes()));

        if (attestationCertificate.attestation != null) {
            NetkiMessages.AttestationType attType = Attestation.valueOf(
                    attestationCertificate.attestation
            ).toAttestationType();
            messageAttestationUnsignedBuilder.setAttestation(attType);
        }
        NetkiMessages.Attestation messageAttestationUnsigned = messageAttestationUnsignedBuilder.build();
        byte[] unsignedByteArr = messageAttestationUnsigned.toByteArray();
        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
        sha256.update(unsignedByteArr);
        String unsignedHexString = Hex.toHexString(sha256.digest());

        PrivateKey privKey = (PrivateKey) stringPemToObject(attestationCertificate.privateKeyPem);
        Signature sigInstance = Signature.getInstance(SIGNATURE_ALGORITHM);
        sigInstance.initSign(privKey);
        sigInstance.update(unsignedHexString.getBytes());
        byte[] signature = sigInstance.sign();
        String base64Sig = Base64.getEncoder().encodeToString(signature);

        NetkiMessages.Attestation finalAtt = NetkiMessages.Attestation.newBuilder()
                .mergeFrom(messageAttestationUnsigned)
                .setSignature(ByteString.copyFrom(base64Sig.getBytes()))
                .build();
        return finalAtt;
    }

    /**
     * Generate originator data with Netki format.
     *
     * @param attestationCertificates a list of attestation certificate
     * @return originator data with Netki format
     * @throws java.lang.Exception
     */
    public static NetkiMessages.Originator attestationCertificateToOriginatorData(
            List<AttestationCertificate> attestationCertificates
    ) throws Exception {
        NetkiMessages.Originator.Builder owner = NetkiMessages.Originator.newBuilder();
        owner.setPrimaryForTransaction(true);

        for (AttestationCertificate attestationCertificate : attestationCertificates) {
            NetkiMessages.Attestation finalAtt = generateAttestation(attestationCertificate);
            owner.addAttestations(finalAtt);
        }
        return owner.build();
    }

    /**
     * Generate beneficiary data with Netki format.
     *
     * @param attestationCertificates a list of attestation certificate
     * @return beneficiary data with Netki format
     * @throws java.lang.Exception
     */
    public static NetkiMessages.Beneficiary attestationCertificateToBeneficiaryData(
            List<AttestationCertificate> attestationCertificates
    ) throws Exception {
        NetkiMessages.Beneficiary.Builder owner = NetkiMessages.Beneficiary.newBuilder();
        owner.setPrimaryForTransaction(true);

        for (AttestationCertificate attestationCertificate : attestationCertificates) {
            NetkiMessages.Attestation finalAtt = generateAttestation(attestationCertificate);
            owner.addAttestations(finalAtt);
        }
        return owner.build();
    }

    /**
     * Generate a signed CSR for the provided principal.
     *
     * @param principal with the string for the CN in the CSR.
     * @param keyPair to sign the CSR.
     * @return the CSR generated.
     */
    private static PKCS10CertificationRequest generateCSR(String principal, KeyPair keyPair) throws Exception {
        JCESigner signer = new JCESigner(keyPair.getPrivate(), SIGNATURE_ALGORITHM);

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
    public static String objectToPemString(Object objectToParse) throws Exception {
        StringWriter stringWriter = new StringWriter();
        PemWriter pemWriter = new PemWriter(stringWriter);
        if (objectToParse instanceof PrivateKey) {
            PrivateKey pk = (PrivateKey) objectToParse;
            pemWriter.writeObject(new PemObject("PRIVATE KEY", pk.getEncoded()));
        }
        if (objectToParse instanceof PublicKey) {
            PublicKey pubkey = (PublicKey) objectToParse;
            pemWriter.writeObject(new PemObject("PUBLIC KEY", pubkey.getEncoded()));
        }
        if (objectToParse instanceof Certificate) {
            Certificate cert = (Certificate) objectToParse;
            pemWriter.writeObject(new PemObject("CERTIFICATE", cert.getEncoded()));
        }
        pemWriter.flush();
        pemWriter.close();
        return stringWriter.toString();
    }

    /**
     * Transform String in PEM format to Object.
     *
     * @param stringToParse in PEM format representing one of PrivateKey /
     * PublicKey / Certificate.
     * @return Object.
     */
    public static Object stringPemToObject(String stringToParse) throws Exception {
        Security.addProvider(new BouncyCastleProvider());

        PEMParser pemParser = new PEMParser(new StringReader(stringToParse));
        Object pemObject = pemParser.readObject();
        if (pemObject instanceof X509CertificateHolder) {
            return new JcaX509CertificateConverter().getCertificate(
                    (X509CertificateHolder) pemObject
            );
        }
        if (pemObject instanceof PrivateKeyInfo) {
            return new JcaPEMKeyConverter().getPrivateKey(
                    (PrivateKeyInfo) pemObject
            );
        }
        if (pemObject instanceof SubjectPublicKeyInfo) {
            return new JcaPEMKeyConverter().getPublicKey(
                    (SubjectPublicKeyInfo) pemObject
            );
        }

        throw new IllegalArgumentException("String not supported");
    }

    /**
     * Method to create signature configuration for CSR.
     */
    public static class JCESigner implements ContentSigner {

        private String algorithm = SIGNATURE_ALGORITHM.toLowerCase();
        private Signature signature = null;
        private ByteArrayOutputStream outputStream = null;
        private HashMap<String, AlgorithmIdentifier> ALGORITHMS;

        public JCESigner(PrivateKey privateKey, String signatureAlgorithm) {
            this.ALGORITHMS = new HashMap<String, AlgorithmIdentifier>();
            this.ALGORITHMS.put(SIGNATURE_ALGORITHM.toLowerCase(), new AlgorithmIdentifier(new ASN1ObjectIdentifier("1.2.840.113549.1.1.11")));
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
            if (id == null) {
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
