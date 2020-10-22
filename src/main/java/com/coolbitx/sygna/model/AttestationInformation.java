package com.coolbitx.sygna.model;

/**
 * Represents some data associated to an specific type of attestation.
 */
public class AttestationInformation {
  /**
   * The type of attestation.
   */
  public Attestation attestation;

  /**
   * The type of IVMS constraints
   */
  public IvmsConstraints ivmsConstraint;

  /**
   * Data associated to the attestation.
   */
  public String data;
}
