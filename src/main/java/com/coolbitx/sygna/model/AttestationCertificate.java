package com.coolbitx.sygna.model;

public class AttestationCertificate {
  public final String attestation;
  public final String certificatePem;
  public final String privateKeyPem;  

  public AttestationCertificate(
    String attestation,
    String certificatePem,
    String privateKeyPem
  ) {
    this.attestation = attestation;
    this.certificatePem = certificatePem;
    this.privateKeyPem = privateKeyPem;
  }
}
