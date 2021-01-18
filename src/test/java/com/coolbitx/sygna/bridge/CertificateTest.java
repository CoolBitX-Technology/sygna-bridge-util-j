package com.coolbitx.sygna.bridge;

import org.junit.Test;

import com.coolbitx.sygna.util.CertificateCreater;
import com.coolbitx.sygna.model.AttestationCertificate;
import com.coolbitx.sygna.model.NetkiMessages;
import com.coolbitx.sygna.model.NetkiMessages.AttestationType;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.math.BigInteger;
import java.security.*;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CertificateTest {
  private static final String BC_PROVIDER = "BC";
  private static final String KEY_ALGORITHM = "RSA";
  private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";

  @Test
  public void testFlow() throws Exception {
    // Add the BouncyCastle Provider
    Security.addProvider(new BouncyCastleProvider());

    // Initialize a new KeyPair generator
    KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM, BC_PROVIDER);
    keyPairGenerator.initialize(2048);

    // Setup start date to yesterday and end date for 1 year validity
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.DATE, -1);
    Date startDate = calendar.getTime();

    calendar.add(Calendar.YEAR, 1);
    Date endDate = calendar.getTime();

    // First step is to create a root certificate
    // First Generate a KeyPair,
    // then a random serial number
    // then generate a certificate using the KeyPair
    KeyPair rootKeyPair = keyPairGenerator.generateKeyPair();
    BigInteger rootSerialNum = new BigInteger(Long.toString(new SecureRandom().nextLong()));

    // Issued By and Issued To same for root certificate
    X500Name rootCertIssuer = new X500Name("CN=root-cert");
    X500Name rootCertSubject = rootCertIssuer;
    CertificateCreater.JCESigner rootCertContentSigner = new CertificateCreater.JCESigner(rootKeyPair.getPrivate(), SIGNATURE_ALGORITHM);
    X509v3CertificateBuilder rootCertBuilder = new JcaX509v3CertificateBuilder(rootCertIssuer, rootSerialNum, startDate, endDate, rootCertSubject, rootKeyPair.getPublic());

    // Add Extensions
    // A BasicConstraint to mark root certificate as CA certificate
    JcaX509ExtensionUtils rootCertExtUtils = new JcaX509ExtensionUtils();
    rootCertBuilder.addExtension(Extension.basicConstraints, true, new BasicConstraints(true));
    rootCertBuilder.addExtension(Extension.subjectKeyIdentifier, false, rootCertExtUtils.createSubjectKeyIdentifier(rootKeyPair.getPublic()));

    // Create a cert holder and export to X509Certificate
    X509CertificateHolder rootCertHolder = rootCertBuilder.build(rootCertContentSigner);
    X509Certificate rootCert = new JcaX509CertificateConverter().setProvider(BC_PROVIDER).getCertificate(rootCertHolder);

    String certPemString = CertificateCreater.objectToPemString(rootCert);
    String privKeyString = CertificateCreater.objectToPemString(rootKeyPair.getPrivate());
    System.out.println(certPemString);
    System.out.println(privKeyString);
    
    // build some attestation
    JsonArray attestations = new JsonArray();

    JsonObject att1 = new JsonObject();
    att1.addProperty("attestation", "LEGAL_PERSON_PRIMARY_NAME");
    att1.addProperty("ivmsConstraints", "BIRT");
    att1.addProperty("data", "Hello");
    attestations.add(att1);

    JsonObject att2 = new JsonObject();
    att2.addProperty("attestation", "LEGAL_PERSON_SECONDARY_NAME");
    att2.addProperty("ivmsConstraints", "BIRT");
    att2.addProperty("data", "World");
    attestations.add(att2);

    List<AttestationCertificate> atts = CertificateCreater.generateCertificate(certPemString, privKeyString, attestations);
    for (AttestationCertificate attestationCertificate : atts) {
      System.out.println(attestationCertificate.attestation);
      System.out.println(attestationCertificate.certificatePem);
      System.out.println(attestationCertificate.privateKeyPem);
    }

    NetkiMessages.Originator originator = CertificateCreater.attestationCertificateToOriginatorData(atts);
    assertTrue(originator.getPrimaryForTransaction());
    assertEquals(originator.getAttestationsCount(),2);
    assertEquals(originator.getAttestations(0).getAttestation(),AttestationType.LEGAL_PERSON_PRIMARY_NAME);
    assertEquals(originator.getAttestations(0).getPkiType(),"x509+sha256");
    assertEquals(originator.getAttestations(0).getPkiData().toStringUtf8(),atts.get(0).certificatePem); 

    NetkiMessages.Beneficiary beneficiary = CertificateCreater.attestationCertificateToBeneficiaryData(atts);
    assertTrue(beneficiary.getPrimaryForTransaction());
    assertEquals(beneficiary.getAttestationsCount(),2);
    assertEquals(beneficiary.getAttestations(0).getAttestation(),AttestationType.LEGAL_PERSON_PRIMARY_NAME);
    assertEquals(beneficiary.getAttestations(0).getPkiType(),"x509+sha256");
    assertEquals(beneficiary.getAttestations(0).getPkiData().toStringUtf8(),atts.get(0).certificatePem); 
  }
}