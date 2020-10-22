package com.coolbitx.sygna.model;

import com.coolbitx.sygna.model.NetkiMessages;

/**
 * List of types of attestations supported.
 */
public enum Attestation {
  LEGAL_PERSON_PRIMARY_NAME(0),
  LEGAL_PERSON_SECONDARY_NAME(1),
  ADDRESS_DEPARTMENT(2),
  ADDRESS_SUB_DEPARTMENT(3),
  ADDRESS_STREET_NAME(4),
  ADDRESS_BUILDING_NUMBER(5),
  ADDRESS_BUILDING_NAME(6),
  ADDRESS_FLOOR(7),
  ADDRESS_POSTBOX(8),
  ADDRESS_ROOM(9),
  ADDRESS_POSTCODE(10),
  ADDRESS_TOWN_NAME(11),
  ADDRESS_TOWN_LOCATION_NAME(12),
  ADDRESS_DISTRICT_NAME(13),
  ADDRESS_COUNTRY_SUB_DIVISION(14),
  ADDRESS_ADDRESS_LINE(15),
  ADDRESS_COUNTRY(16),
  NATURAL_PERSON_FIRST_NAME(17),
  NATURAL_PERSON_LAST_NAME(18),
  BENEFICIARY_PERSON_FIRST_NAME(19),
  BENEFICIARY_PERSON_LAST_NAME(20),
  BIRTH_DATE(21),
  BIRTH_PLACE(22),
  COUNTRY_OF_RESIDENCE(23),
  ISSUING_COUNTRY(24),
  NATIONAL_IDENTIFIER_NUMBER(25),
  NATIONAL_IDENTIFIER(26),
  ACCOUNT_NUMBER(27),
  CUSTOMER_IDENTIFICATION(28),
  REGISTRATION_AUTHORITY(29);

  public final int attType;
  private Attestation(int id) {
    this.attType = id;
  }

  // public int val() {
  //   return this.attType;
  // }

  private boolean validateConstraint(IvmsConstraints ivmsConstraints) {
    if(ivmsConstraints == null) {
      return true;
    }

    switch (Attestation.values()[this.attType]) {
        case LEGAL_PERSON_PRIMARY_NAME:
        case LEGAL_PERSON_SECONDARY_NAME:
        case NATURAL_PERSON_FIRST_NAME:
        case NATURAL_PERSON_LAST_NAME:
        case BENEFICIARY_PERSON_FIRST_NAME:
        case BENEFICIARY_PERSON_LAST_NAME:
            return (
                ivmsConstraints == IvmsConstraints.ALIA ||
                ivmsConstraints == IvmsConstraints.BIRT ||
                ivmsConstraints == IvmsConstraints.MAID ||
                ivmsConstraints == IvmsConstraints.LEGL ||
                ivmsConstraints == IvmsConstraints.MISC
            );
        case ADDRESS_DEPARTMENT:
        case ADDRESS_SUB_DEPARTMENT:
        case ADDRESS_STREET_NAME:
        case ADDRESS_BUILDING_NUMBER:
        case ADDRESS_BUILDING_NAME:
        case ADDRESS_FLOOR:
        case ADDRESS_POSTBOX:
        case ADDRESS_ROOM:
        case ADDRESS_POSTCODE:
        case ADDRESS_TOWN_NAME:
        case ADDRESS_TOWN_LOCATION_NAME:
        case ADDRESS_DISTRICT_NAME:
        case ADDRESS_COUNTRY_SUB_DIVISION:
        case ADDRESS_ADDRESS_LINE:
        case ADDRESS_COUNTRY:
            return (
                ivmsConstraints == IvmsConstraints.GEOG ||
                ivmsConstraints == IvmsConstraints.BIZZ ||
                ivmsConstraints == IvmsConstraints.HOME 
            );
        case NATIONAL_IDENTIFIER:
            return (
                ivmsConstraints == IvmsConstraints.CCPT ||
                ivmsConstraints == IvmsConstraints.RAID ||
                ivmsConstraints == IvmsConstraints.DRLC ||
                ivmsConstraints == IvmsConstraints.FIIN ||
                ivmsConstraints == IvmsConstraints.TXID ||
                ivmsConstraints == IvmsConstraints.SOCS ||
                ivmsConstraints == IvmsConstraints.IDCD ||
                ivmsConstraints == IvmsConstraints.LEIX ||
                ivmsConstraints == IvmsConstraints.MISC
            );
        default:
            return false;
    }
  }

  public String toPrinciple(String data, IvmsConstraints ivmsConstraints) throws Exception {
    String data64Characters = "";
    String extraData = "";

    if (data.length() > 64) {
        data64Characters = data.substring(0, 64);
        extraData = data.substring(64, data.length());
    } else {
        data64Characters = data;
    }

    if (!validateConstraint(ivmsConstraints)) {
        throw new Exception(
            String.format("IVMS constrain fail", ivmsConstraints)
        );
    }

    final String PRINCIPAL_STRING = "CN=%s, C=%s, L=%s, O=%s, OU=%s, ST=%s";
    switch (Attestation.values()[this.attType]) {
        case LEGAL_PERSON_PRIMARY_NAME: 
            return String.format(
                PRINCIPAL_STRING,
                "legalPersonName.primaryIdentifier",
                extraData,
                "legalPersonNameType",
                "legalPrimaryName",
                data64Characters,
                ivmsConstraints
            );
        case LEGAL_PERSON_SECONDARY_NAME:
            return String.format(
                PRINCIPAL_STRING,
                "legalPersonName.secondaryIdentifier",
                extraData,
                "legalPersonNameType",
                "legalSecondaryName",
                data64Characters,
                ivmsConstraints
            );
        case ADDRESS_DEPARTMENT:
            return String.format(
                PRINCIPAL_STRING,
                "address.department",
                extraData,
                "department",
                "department",
                data64Characters,
                ivmsConstraints
            );
        case ADDRESS_SUB_DEPARTMENT:
            return String.format(
                PRINCIPAL_STRING,
                "address.subDepartment",
                extraData,
                "subDepartment",
                "subDepartment",
                data64Characters,
                ivmsConstraints
            );
        case ADDRESS_STREET_NAME:
            return String.format(
                PRINCIPAL_STRING,
                "address.streetName",
                extraData,
                "streetName",
                "streetName",
                data64Characters,
                ivmsConstraints
            );
        case ADDRESS_BUILDING_NUMBER:
            return String.format(
                PRINCIPAL_STRING,
                "address.buildingNumber",
                extraData,
                "buildingNumber",
                "buildingNumber",
                data64Characters,
                ivmsConstraints
            );
        case ADDRESS_BUILDING_NAME:
            return String.format(
                PRINCIPAL_STRING,
                "address.buildingName",
                extraData,
                "buildingName",
                "buildingName",
                data64Characters,
                ivmsConstraints
            );
        case ADDRESS_FLOOR:
            return String.format(
                PRINCIPAL_STRING,
                "address.floor",
                extraData,
                "floor",
                "floor",
                data64Characters,
                ivmsConstraints
            );
        case ADDRESS_POSTBOX:
            return String.format(
                PRINCIPAL_STRING,
                "address.postBox",
                extraData,
                "postBox",
                "postBox",
                data64Characters,
                ivmsConstraints
            );
        case ADDRESS_ROOM:
            return String.format(
                PRINCIPAL_STRING,
                "address.room",
                extraData,
                "room",
                "room",
                data64Characters,
                ivmsConstraints
            );
        case ADDRESS_POSTCODE:
            return String.format(
                PRINCIPAL_STRING,
                "address.postCode",
                extraData,
                "postCode",
                "postCode",
                data64Characters,
                ivmsConstraints
            );
        case ADDRESS_TOWN_NAME:
            return String.format(
                PRINCIPAL_STRING,
                "address.townName",
                extraData,
                "townName",
                "townName",
                data64Characters,
                ivmsConstraints
            );
        case ADDRESS_TOWN_LOCATION_NAME:
            return String.format(
                PRINCIPAL_STRING,
                "address.townLocationName",
                extraData,
                "townLocationName",
                "townLocationName",
                data64Characters,
                ivmsConstraints
            );
        case ADDRESS_DISTRICT_NAME:
            return String.format(
                PRINCIPAL_STRING,
                "address.districtName",
                extraData,
                "districtName",
                "districtName",
                data64Characters,
                ivmsConstraints
            );
        case ADDRESS_COUNTRY_SUB_DIVISION:
            return String.format(
                PRINCIPAL_STRING,
                "address.countrySubDivision",
                extraData,
                "countrySubDivision",
                "countrySubDivision",
                data64Characters,
                ivmsConstraints
            );
        case ADDRESS_ADDRESS_LINE:
            return String.format(
                PRINCIPAL_STRING,
                "address.addressLine",
                extraData,
                "addressLine",
                "addressLine",
                data64Characters,
                ivmsConstraints
            );
        case ADDRESS_COUNTRY:
            return String.format(
                PRINCIPAL_STRING,
                "address.country",
                extraData,
                "country",
                "country",
                data64Characters,
                ivmsConstraints
            );
        case NATURAL_PERSON_FIRST_NAME:
            return String.format(
                PRINCIPAL_STRING,
                "naturalName.secondaryIdentifier",
                extraData,
                "naturalPersonNameType",
                "naturalPersonFirstName",
                data64Characters,
                ivmsConstraints
            );
        case NATURAL_PERSON_LAST_NAME:
            return String.format(
                PRINCIPAL_STRING,
                "naturalName.primaryIdentifier",
                extraData,
                "naturalPersonNameType",
                "naturalPersonLastName",
                data64Characters,
                ivmsConstraints
            );
        case BENEFICIARY_PERSON_FIRST_NAME:
            return String.format(
                PRINCIPAL_STRING,
                "beneficiaryName.secondaryIdentifier",
                extraData,
                "beneficiaryPersonNameType",
                "beneficiaryPersonFirstName",
                data64Characters,
                ivmsConstraints
            );
        case BENEFICIARY_PERSON_LAST_NAME:
            return String.format(
                PRINCIPAL_STRING,
                "beneficiaryName.primaryIdentifier",
                extraData,
                "beneficiaryPersonNameType",
                "beneficiaryPersonLastName",
                data64Characters,
                ivmsConstraints
            );
        case BIRTH_DATE:
            return String.format(
                PRINCIPAL_STRING,
                "naturalPerson.dateOfBirth",
                extraData,
                "dateInPast",
                "birthdate",
                data64Characters,
                ivmsConstraints
            );
        case BIRTH_PLACE:
            return String.format(
                PRINCIPAL_STRING,
                "naturalPerson.placeOfBirth",
                extraData,
                "countryCode",
                "country",
                data64Characters,
                ivmsConstraints
            );
        case COUNTRY_OF_RESIDENCE:
            return String.format(
                PRINCIPAL_STRING,
                "naturalPerson.countryOfResidence",
                extraData,
                "countryCode",
                "country",
                data64Characters,
                ivmsConstraints
            );
        case ISSUING_COUNTRY:
            return String.format(
                PRINCIPAL_STRING,
                "nationalIdentifier.countryOfIssue",
                extraData,
                "nationalIdentifierType",
                "nationalIdentifier",
                data64Characters,
                ivmsConstraints
            );
        case NATIONAL_IDENTIFIER_NUMBER:
            return String.format(
                PRINCIPAL_STRING,
                "nationalIdentifier.number",
                extraData,
                "number",
                "documentNumber",
                data64Characters,
                ivmsConstraints
            );
        case NATIONAL_IDENTIFIER:
            return String.format(
                PRINCIPAL_STRING,
                "nationalIdentifier.documentType",
                extraData,
                "nationalIdentifierType",
                "documentType",
                data64Characters,
                ivmsConstraints
            );
        case ACCOUNT_NUMBER:
            return String.format(
                PRINCIPAL_STRING,
                "accountNumber",
                extraData,
                "accountNumber",
                "accountNumber",
                data64Characters,
                ivmsConstraints
            );
        case CUSTOMER_IDENTIFICATION:
            return String.format(
                PRINCIPAL_STRING,
                "customerIdentification",
                extraData,
                "customerIdentification",
                "customerIdentification",
                data64Characters,
                ivmsConstraints
            );
        case REGISTRATION_AUTHORITY:
            return String.format(
                PRINCIPAL_STRING,
                "registrationAuthority",
                extraData,
                "registrationAuthority",
                "registrationAuthority",
                data64Characters,
                ivmsConstraints
            );
        default:
            throw new Exception("Unknown attestationType");
    }
  }

  /**
   * Transform Attestation to Messages.AttestationType.
  */
  public NetkiMessages.AttestationType toAttestationType() throws Exception {
    switch (Attestation.values()[this.attType]) {
        case LEGAL_PERSON_PRIMARY_NAME:
          return NetkiMessages.AttestationType.LEGAL_PERSON_PRIMARY_NAME;
        case LEGAL_PERSON_SECONDARY_NAME:
          return NetkiMessages.AttestationType.LEGAL_PERSON_SECONDARY_NAME;
        case ADDRESS_DEPARTMENT:
          return NetkiMessages.AttestationType.ADDRESS_DEPARTMENT;
        case ADDRESS_SUB_DEPARTMENT:
          return NetkiMessages.AttestationType.ADDRESS_SUB_DEPARTMENT;
        case ADDRESS_STREET_NAME:
          return NetkiMessages.AttestationType.ADDRESS_STREET_NAME;
        case ADDRESS_BUILDING_NUMBER:
          return NetkiMessages.AttestationType.ADDRESS_BUILDING_NUMBER;
        case ADDRESS_BUILDING_NAME:
          return NetkiMessages.AttestationType.ADDRESS_BUILDING_NAME;
        case ADDRESS_FLOOR:
          return NetkiMessages.AttestationType.ADDRESS_FLOOR;
        case ADDRESS_POSTBOX:
          return NetkiMessages.AttestationType.ADDRESS_POSTBOX;
        case ADDRESS_ROOM:
          return NetkiMessages.AttestationType.ADDRESS_ROOM;
        case ADDRESS_POSTCODE:
          return NetkiMessages.AttestationType.ADDRESS_POSTCODE;
        case ADDRESS_TOWN_NAME:
          return NetkiMessages.AttestationType.ADDRESS_TOWN_NAME;
        case ADDRESS_TOWN_LOCATION_NAME:
          return NetkiMessages.AttestationType.ADDRESS_TOWN_LOCATION_NAME;
        case ADDRESS_DISTRICT_NAME:
          return NetkiMessages.AttestationType.ADDRESS_DISTRICT_NAME;
        case ADDRESS_COUNTRY_SUB_DIVISION:
          return NetkiMessages.AttestationType.ADDRESS_COUNTRY_SUB_DIVISION;
        case ADDRESS_ADDRESS_LINE:
          return NetkiMessages.AttestationType.ADDRESS_ADDRESS_LINE;
        case ADDRESS_COUNTRY:
          return NetkiMessages.AttestationType.ADDRESS_COUNTRY;
        case NATURAL_PERSON_FIRST_NAME:
          return NetkiMessages.AttestationType.NATURAL_PERSON_FIRST_NAME;
        case NATURAL_PERSON_LAST_NAME:
          return NetkiMessages.AttestationType.NATURAL_PERSON_LAST_NAME;
        case BENEFICIARY_PERSON_FIRST_NAME:
          return NetkiMessages.AttestationType.BENEFICIARY_PERSON_FIRST_NAME;
        case BENEFICIARY_PERSON_LAST_NAME:
          return NetkiMessages.AttestationType.BENEFICIARY_PERSON_LAST_NAME;
        case BIRTH_DATE:
          return NetkiMessages.AttestationType.BIRTH_DATE;
        case BIRTH_PLACE:
          return NetkiMessages.AttestationType.BIRTH_PLACE;
        case COUNTRY_OF_RESIDENCE:
          return NetkiMessages.AttestationType.COUNTRY_OF_RESIDENCE;
        case ISSUING_COUNTRY:
          return NetkiMessages.AttestationType.ISSUING_COUNTRY;
        case NATIONAL_IDENTIFIER_NUMBER:
          return NetkiMessages.AttestationType.NATIONAL_IDENTIFIER_NUMBER;
        case NATIONAL_IDENTIFIER:
          return NetkiMessages.AttestationType.NATIONAL_IDENTIFIER;
        case ACCOUNT_NUMBER: 
          return NetkiMessages.AttestationType.ACCOUNT_NUMBER;
        case CUSTOMER_IDENTIFICATION:
          return NetkiMessages.AttestationType.CUSTOMER_IDENTIFICATION;
        case REGISTRATION_AUTHORITY:
          return NetkiMessages.AttestationType.REGISTRATION_AUTHORITY;
        default:
          throw new Exception("Invalid attestation type");
    }
  }
}
