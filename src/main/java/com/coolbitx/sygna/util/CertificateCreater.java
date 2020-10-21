package com.coolbitx.sygna.util;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cmp.PollReqContent;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
// import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
// import org.bouncycastle.openssl.jcajce.JcaP;
// import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;
import org.bouncycastle.util.io.pem.PemReader;
import org.bouncycastle.cert.X509v3CertificateBuilder;

import com.google.gson.Gson;
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

public class CertificateCreater {
    public static String gernateCertificate(String parentCertificatePem, String parentPrivateKeyPem, JsonArray attestations) throws Exception {
        // read "root" certificate
        // FileReader fileReader = new FileReader("/path/to/cert.pem");
        // PemReader pemReader = new PemReader(fileReader);
        // PemObject obj = pemReader.readPemObject();
        // pemReader.close(); // sloppy IO handling, be thorough in production code
        // X509CertificateObject certObj = (X509CertificateObject) obj;
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        List<X509Certificate> rootCerts = (List<X509Certificate>) cf.generateCertificates(
            new ByteArrayInputStream(parentCertificatePem.getBytes("UTF_8"))
        );
        X509Certificate rootCert = rootCerts.get(0);
        JcaX509CertificateHolder rootCertJca = new JcaX509CertificateHolder(rootCert);
        // JcaX509CertificateConverter conv = new JcaX509CertificateConverter();
        // conv.

        // load root private key
        // public Key loadPrivateKey(String stored) throws GeneralSecurityException {
        //     PKCS8EncodedKeySpec keySpec =
        //         new PKCS8EncodedKeySpec(
        //             Base64.getDecoder().decode(stored.getBytes(StandardCharsets.UTF_8)));
        //     KeyFactory kf = KeyFactory.getInstance("RSA");
        //     return kf.generatePrivate(keySpec);
        //   }
        
        //   public Key loadPublicKey(String stored) throws GeneralSecurityException {
        //     byte[] data = Base64.getDecoder().decode(stored.getBytes(StandardCharsets.UTF_8));
        //     X509EncodedKeySpec spec = new X509EncodedKeySpec(data);
        //     KeyFactory fact = KeyFactory.getInstance("RSA");
        //     return fact.generatePublic(spec);
        //   }

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(
            Base64.getDecoder().decode(parentPrivateKeyPem.getBytes("UTF_8"))
        );
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PrivateKey parentPrivateKey = kf.generatePrivate(keySpec);
        

        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        // StringWriter stringWriter = new StringWriter();
        // PemWriter pemWriter = new PemWriter(stringWriter);

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
        // Setup start date to yesterday and end date for 1 year validity
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        Date startDate = calendar.getTime();

        calendar.add(Calendar.YEAR, 1);
        Date endDate = calendar.getTime();

        for (JsonElement attestation : attestations) {
            String principal = CertificateCreater.attestationToPrincipal(attestation.getAsJsonObject());
            KeyPair keyPair = kpg.generateKeyPair();
            PKCS10CertificationRequest csr = CertificateCreater.generateCSR(principal, keyPair);

            X509v3CertificateBuilder issuedCertBuilder = new X509v3CertificateBuilder(
                rootCertJca.getSubject(),
                new BigInteger(Long.toString(new SecureRandom().nextLong())),
                startDate,
                endDate,
                csr.getSubject(),
                csr.getSubjectPublicKeyInfo()
            );

            // JcaContentSignerBuilder csrBuilder = new JcaContentSignerBuilder("SHA256withRSA").setProvider("BC");
            // Sign the new KeyPair with the root cert Private Key
            // ContentSigner csrContentSigner = csrBuilder.build(keyPair.getPrivate());
            X509CertificateHolder issuedCertHolder = issuedCertBuilder.build(
                new CertificateCreater.JCESigner(parentPrivateKey, "SHA256withRSA")
            );
            X509Certificate issuedCert = new JcaX509CertificateConverter().getCertificate(issuedCertHolder);

            // return signed cert and private key
        }

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

    private static String attestationToPrincipal(JsonObject attestation) throws Exception {
        // extract data from json object
        String attestationType = attestation.get("attestation").getAsString();
        String ivmsConstraints = attestation.get("ivmsConstraints").getAsString();
        String data = attestation.get("data").getAsString();

        String data64Characters = "";
        String extraData = "";

        if (data.length() > 64) {
            data64Characters = data.substring(0, 64);
            extraData = data.substring(64, data.length());
        } else {
            data64Characters = data;
        }

        String ivmConstraintValue = ivmsConstraints == null ? ivmsConstraints : "";

        if (CertificateCreater.validateIvmsConstraint(attestationType, ivmsConstraints)) {
            throw new Exception(
                String.format("IVMS constrain fail", ivmConstraintValue)
            );
        }

        final String PRINCIPAL_STRING = "CN=%s, C=%s, L=%s, O=%s, OU=%s, ST=%s";
        switch (attestationType) {
            case "LEGAL_PERSON_PRIMARY_NAME": 
                return String.format(
                    PRINCIPAL_STRING,
                    "legalPersonName.primaryIdentifier",
                    extraData,
                    "legalPersonNameType",
                    "legalPrimaryName",
                    data64Characters,
                    ivmConstraintValue
                );
            case "LEGAL_PERSON_SECONDARY_NAME":
                return String.format(
                    PRINCIPAL_STRING,
                    "legalPersonName.secondaryIdentifier",
                    extraData,
                    "legalPersonNameType",
                    "legalSecondaryName",
                    data64Characters,
                    ivmConstraintValue
                );
            case "ADDRESS_DEPARTMENT":
                return String.format(
                    PRINCIPAL_STRING,
                    "address.department",
                    extraData,
                    "department",
                    "department",
                    data64Characters,
                    ivmConstraintValue
                );
            case "ADDRESS_SUB_DEPARTMENT":
                return String.format(
                    PRINCIPAL_STRING,
                    "address.subDepartment",
                    extraData,
                    "subDepartment",
                    "subDepartment",
                    data64Characters,
                    ivmConstraintValue
                );
            case "ADDRESS_STREET_NAME":
                return String.format(
                    PRINCIPAL_STRING,
                    "address.streetName",
                    extraData,
                    "streetName",
                    "streetName",
                    data64Characters,
                    ivmConstraintValue
                );
            case "ADDRESS_BUILDING_NUMBER":
                return String.format(
                    PRINCIPAL_STRING,
                    "address.buildingNumber",
                    extraData,
                    "buildingNumber",
                    "buildingNumber",
                    data64Characters,
                    ivmConstraintValue
                );
            case "ADDRESS_BUILDING_NAME":
                return String.format(
                    PRINCIPAL_STRING,
                    "address.buildingName",
                    extraData,
                    "buildingName",
                    "buildingName",
                    data64Characters,
                    ivmConstraintValue
                );
            case "ADDRESS_FLOOR":
                return String.format(
                    PRINCIPAL_STRING,
                    "address.floor",
                    extraData,
                    "floor",
                    "floor",
                    data64Characters,
                    ivmConstraintValue
                );
            case "ADDRESS_POSTBOX":
                return String.format(
                    PRINCIPAL_STRING,
                    "address.postBox",
                    extraData,
                    "postBox",
                    "postBox",
                    data64Characters,
                    ivmConstraintValue
                );
            case "ADDRESS_ROOM":
                return String.format(
                    PRINCIPAL_STRING,
                    "address.room",
                    extraData,
                    "room",
                    "room",
                    data64Characters,
                    ivmConstraintValue
                );
            case "ADDRESS_POSTCODE":
                return String.format(
                    PRINCIPAL_STRING,
                    "address.postCode",
                    extraData,
                    "postCode",
                    "postCode",
                    data64Characters,
                    ivmConstraintValue
                );
            case "ADDRESS_TOWN_NAME":
                return String.format(
                    PRINCIPAL_STRING,
                    "address.townName",
                    extraData,
                    "townName",
                    "townName",
                    data64Characters,
                    ivmConstraintValue
                );
            case "ADDRESS_TOWN_LOCATION_NAME":
                return String.format(
                    PRINCIPAL_STRING,
                    "address.townLocationName",
                    extraData,
                    "townLocationName",
                    "townLocationName",
                    data64Characters,
                    ivmConstraintValue
                );
            case "ADDRESS_DISTRICT_NAME":
                return String.format(
                    PRINCIPAL_STRING,
                    "address.districtName",
                    extraData,
                    "districtName",
                    "districtName",
                    data64Characters,
                    ivmConstraintValue
                );
            case "ADDRESS_COUNTRY_SUB_DIVISION":
                return String.format(
                    PRINCIPAL_STRING,
                    "address.countrySubDivision",
                    extraData,
                    "countrySubDivision",
                    "countrySubDivision",
                    data64Characters,
                    ivmConstraintValue
                );
            case "ADDRESS_ADDRESS_LINE":
                return String.format(
                    PRINCIPAL_STRING,
                    "address.addressLine",
                    extraData,
                    "addressLine",
                    "addressLine",
                    data64Characters,
                    ivmConstraintValue
                );
            case "ADDRESS_COUNTRY":
                return String.format(
                    PRINCIPAL_STRING,
                    "address.country",
                    extraData,
                    "country",
                    "country",
                    data64Characters,
                    ivmConstraintValue
                );
            case "NATURAL_PERSON_FIRST_NAME":
                return String.format(
                    PRINCIPAL_STRING,
                    "naturalName.secondaryIdentifier",
                    extraData,
                    "naturalPersonNameType",
                    "naturalPersonFirstName",
                    data64Characters,
                    ivmConstraintValue
                );
            case "NATURAL_PERSON_LAST_NAME":
                return String.format(
                    PRINCIPAL_STRING,
                    "naturalName.primaryIdentifier",
                    extraData,
                    "naturalPersonNameType",
                    "naturalPersonLastName",
                    data64Characters,
                    ivmConstraintValue
                );
            case "BENEFICIARY_PERSON_FIRST_NAME":
                return String.format(
                    PRINCIPAL_STRING,
                    "beneficiaryName.secondaryIdentifier",
                    extraData,
                    "beneficiaryPersonNameType",
                    "beneficiaryPersonFirstName",
                    data64Characters,
                    ivmConstraintValue
                );
            case "BENEFICIARY_PERSON_LAST_NAME":
                return String.format(
                    PRINCIPAL_STRING,
                    "beneficiaryName.primaryIdentifier",
                    extraData,
                    "beneficiaryPersonNameType",
                    "beneficiaryPersonLastName",
                    data64Characters,
                    ivmConstraintValue
                );
            case "BIRTH_DATE":
                return String.format(
                    PRINCIPAL_STRING,
                    "naturalPerson.dateOfBirth",
                    extraData,
                    "dateInPast",
                    "birthdate",
                    data64Characters,
                    ivmConstraintValue
                );
            case "BIRTH_PLACE":
                return String.format(
                    PRINCIPAL_STRING,
                    "naturalPerson.placeOfBirth",
                    extraData,
                    "countryCode",
                    "country",
                    data64Characters,
                    ivmConstraintValue
                );
            case "COUNTRY_OF_RESIDENCE":
                return String.format(
                    PRINCIPAL_STRING,
                    "naturalPerson.countryOfResidence",
                    extraData,
                    "countryCode",
                    "country",
                    data64Characters,
                    ivmConstraintValue
                );
            case "ISSUING_COUNTRY":
                return String.format(
                    PRINCIPAL_STRING,
                    "nationalIdentifier.countryOfIssue",
                    extraData,
                    "nationalIdentifierType",
                    "nationalIdentifier",
                    data64Characters,
                    ivmConstraintValue
                );
            case "NATIONAL_IDENTIFIER_NUMBER":
                return String.format(
                    PRINCIPAL_STRING,
                    "nationalIdentifier.number",
                    extraData,
                    "number",
                    "documentNumber",
                    data64Characters,
                    ivmConstraintValue
                );
            case "NATIONAL_IDENTIFIER":
                return String.format(
                    PRINCIPAL_STRING,
                    "nationalIdentifier.documentType",
                    extraData,
                    "nationalIdentifierType",
                    "documentType",
                    data64Characters,
                    ivmConstraintValue
                );
            case "ACCOUNT_NUMBER":
                return String.format(
                    PRINCIPAL_STRING,
                    "accountNumber",
                    extraData,
                    "accountNumber",
                    "accountNumber",
                    data64Characters,
                    ivmConstraintValue
                );
            case "CUSTOMER_IDENTIFICATION":
                return String.format(
                    PRINCIPAL_STRING,
                    "customerIdentification",
                    extraData,
                    "customerIdentification",
                    "customerIdentification",
                    data64Characters,
                    ivmConstraintValue
                );
            case "REGISTRATION_AUTHORITY":
                return String.format(
                    PRINCIPAL_STRING,
                    "registrationAuthority",
                    extraData,
                    "registrationAuthority",
                    "registrationAuthority",
                    data64Characters,
                    ivmConstraintValue
                );
            default:
                throw Exception("Unknown attestationType");
        }
    }

    private static Boolean validateIvmsConstraint(String attestationType, String ivmsConstraints) {
        if(ivmsConstraints == null) {
            return true;
        }
        switch (attestationType) {
            case "LEGAL_PERSON_PRIMARY_NAME":
            case "LEGAL_PERSON_SECONDARY_NAME":
            case "NATURAL_PERSON_FIRST_NAME":
            case "NATURAL_PERSON_LAST_NAME":
            case "BENEFICIARY_PERSON_FIRST_NAME":
            case "BENEFICIARY_PERSON_LAST_NAME":
                return (
                    ivmsConstraints == "ALIA" ||
                    ivmsConstraints == "BIRT" ||
                    ivmsConstraints == "MAID" ||
                    ivmsConstraints == "LEGL" ||
                    ivmsConstraints == "MISC"
                );
            case "ADDRESS_DEPARTMENT":
            case "ADDRESS_SUB_DEPARTMENT":
            case "ADDRESS_STREET_NAME":
            case "ADDRESS_BUILDING_NUMBER":
            case "ADDRESS_BUILDING_NAME":
            case "ADDRESS_FLOOR":
            case "ADDRESS_POSTBOX":
            case "ADDRESS_ROOM":
            case "ADDRESS_POSTCODE":
            case "ADDRESS_TOWN_NAME":
            case "ADDRESS_TOWN_LOCATION_NAME":
            case "ADDRESS_DISTRICT_NAME":
            case "ADDRESS_COUNTRY_SUB_DIVISION":
            case "ADDRESS_ADDRESS_LINE":
            case "ADDRESS_COUNTRY":
                return (
                    ivmsConstraints == "GEOG" ||
                    ivmsConstraints == "BIZZ" ||
                    ivmsConstraints == "HOME" 
                );
            case "Attestation.NATIONAL_IDENTIFIER":
                return (
                    ivmsConstraints == "CCPT" ||
                    ivmsConstraints == "RAID" ||
                    ivmsConstraints == "DRLC" ||
                    ivmsConstraints == "FIIN" ||
                    ivmsConstraints == "TXID" ||
                    ivmsConstraints == "SOCS" ||
                    ivmsConstraints == "IDCD" ||
                    ivmsConstraints == "LEIX" ||
                    ivmsConstraints == "MISC"
                );
            default:
                return false;
        }
    }
}

