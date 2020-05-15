/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.coolbitx.sygna.bridge;

import com.coolbitx.sygna.util.Validator;
import org.everit.json.schema.ValidationException;
import org.json.JSONArray;
import org.json.JSONObject;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author kunming.liu
 */
public class ValidateTransactionSchemaTest {

    JSONObject expectedJSONObject = new JSONObject();

    @Before
    public void init() {
        JSONArray originatorAddrs = new JSONArray();
        originatorAddrs.put("16bUGjvunVp7LqygLHrTvHyvbvfeuRCWAh");

        JSONArray beneficiaryAddrs = new JSONArray();
        beneficiaryAddrs.put("3CHgkx946yyueucCMiJhyH2Vg5kBBvfSGH");

        expectedJSONObject.put("originator_vasp_code", "VASPTWTP1");
        expectedJSONObject.put("originator_addrs", originatorAddrs);
        expectedJSONObject.put("beneficiary_vasp_code", "VASPTWTP2");
        expectedJSONObject.put("beneficiary_addrs", beneficiaryAddrs);
        expectedJSONObject.put("transaction_currency", "0x80000000");
        expectedJSONObject.put("amount", 1);
    }

//    public 
    @Test
    public void testTransactionSchema() {

        try {
            Validator.validateTransactionSchema(expectedJSONObject);

            JSONObject cloneJSONObject1 = new JSONObject(expectedJSONObject, JSONObject.getNames(expectedJSONObject));
            JSONObject originatorAddrsExtra = new JSONObject();
            originatorAddrsExtra.put("DT", "001");
            cloneJSONObject1.put("originator_addrs_extra", originatorAddrsExtra);
            Validator.validateTransactionSchema(cloneJSONObject1);

            JSONObject cloneJSONObject2 = new JSONObject(expectedJSONObject, JSONObject.getNames(expectedJSONObject));
            JSONObject beneficiaryAddrsExtra = new JSONObject();
            beneficiaryAddrsExtra.put("DT", "001");
            cloneJSONObject2.put("beneficiary_addrs_extra", beneficiaryAddrsExtra);
            Validator.validateTransactionSchema(cloneJSONObject2);

            JSONObject cloneJSONObject3 = new JSONObject(expectedJSONObject, JSONObject.getNames(expectedJSONObject));
            cloneJSONObject3.put("originator_addrs_extra", originatorAddrsExtra);
            cloneJSONObject3.put("beneficiary_addrs_extra", beneficiaryAddrsExtra);
            Validator.validateTransactionSchema(cloneJSONObject3);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            fail("unexpected exception was occured.");
        }

    }

    @Test
    public void testOriginatorVaspCode() {
        //should throw ValidationException if originator_vasp_code is missing
        JSONObject cloneJSONObject1 = new JSONObject(expectedJSONObject, JSONObject.getNames(expectedJSONObject));
        cloneJSONObject1.remove("originator_vasp_code");
        validateSchema(cloneJSONObject1);

        //should throw ValidationException if originator_vasp_code is not String
        JSONObject cloneJSONObject2 = new JSONObject(expectedJSONObject, JSONObject.getNames(expectedJSONObject));
        cloneJSONObject2.put("originator_vasp_code", 1);
        validateSchema(cloneJSONObject2);

        //should throw ValidationException if originator_vasp_code is empty
        JSONObject cloneJSONObject3 = new JSONObject(expectedJSONObject, JSONObject.getNames(expectedJSONObject));
        cloneJSONObject3.put("originator_vasp_code", "");
        validateSchema(cloneJSONObject3);
    }

    @Test
    public void testOriginatororAddrs() {
        //should throw ValidationException if originator_addrs is missing
        JSONObject cloneJSONObject1 = new JSONObject(expectedJSONObject, JSONObject.getNames(expectedJSONObject));
        cloneJSONObject1.remove("originator_addrs");
        validateSchema(cloneJSONObject1);

        //should throw ValidationException if originator_addrs is not array
        JSONObject cloneJSONObject2 = new JSONObject(expectedJSONObject, JSONObject.getNames(expectedJSONObject));
        cloneJSONObject2.put("originator_addrs", 1);
        validateSchema(cloneJSONObject2);

        //should throw ValidationException if originator_addrs is empty
        JSONArray originatorAddrs = new JSONArray();
        JSONObject cloneJSONObject3 = new JSONObject(expectedJSONObject, JSONObject.getNames(expectedJSONObject));
        cloneJSONObject3.put("originator_addrs", originatorAddrs);
        validateSchema(cloneJSONObject3);

        //should throw ValidationException if originator_addrs element is not String
        originatorAddrs.put(0, 0);
        JSONObject cloneJSONObject4 = new JSONObject(expectedJSONObject, JSONObject.getNames(expectedJSONObject));
        cloneJSONObject4.put("originator_addrs", originatorAddrs);
        validateSchema(cloneJSONObject4);

        //should throw ValidationException if originator_addrs element is empty
        originatorAddrs.remove(0);
        originatorAddrs.put(0, "");
        JSONObject cloneJSONObject5 = new JSONObject(expectedJSONObject, JSONObject.getNames(expectedJSONObject));
        cloneJSONObject5.put("originator_addrs", originatorAddrs);
        validateSchema(cloneJSONObject5);
    }

    @Test
    public void testBeneficiaryVaspCode() {
        //should throw ValidationException if beneficiary_vasp_code is missing
        JSONObject cloneJSONObject1 = new JSONObject(expectedJSONObject, JSONObject.getNames(expectedJSONObject));
        cloneJSONObject1.remove("beneficiary_vasp_code");
        validateSchema(cloneJSONObject1);

        //should throw ValidationException if beneficiary_vasp_code is not String
        JSONObject cloneJSONObject2 = new JSONObject(expectedJSONObject, JSONObject.getNames(expectedJSONObject));
        cloneJSONObject2.put("beneficiary_vasp_code", 1);
        validateSchema(cloneJSONObject2);

        //should throw ValidationException if beneficiary_vasp_code is empty
        JSONObject cloneJSONObject3 = new JSONObject(expectedJSONObject, JSONObject.getNames(expectedJSONObject));
        cloneJSONObject3.put("beneficiary_vasp_code", "");
        validateSchema(cloneJSONObject3);
    }

    @Test
    public void testBeneficiaryAddrs() {
        //should throw ValidationException if beneficiary_addrs is missing
        JSONObject cloneJSONObject1 = new JSONObject(expectedJSONObject, JSONObject.getNames(expectedJSONObject));
        cloneJSONObject1.remove("beneficiary_addrs");
        validateSchema(cloneJSONObject1);

        //should throw ValidationException if beneficiary_addrs is not array
        JSONObject cloneJSONObject2 = new JSONObject(expectedJSONObject, JSONObject.getNames(expectedJSONObject));
        cloneJSONObject2.put("beneficiary_addrs", 1);
        validateSchema(cloneJSONObject2);

        //should throw ValidationException if beneficiary_addrs is empty
        JSONArray originatorAddrs = new JSONArray();
        JSONObject cloneJSONObject3 = new JSONObject(expectedJSONObject, JSONObject.getNames(expectedJSONObject));
        cloneJSONObject3.put("beneficiary_addrs", originatorAddrs);
        validateSchema(cloneJSONObject3);

        //should throw ValidationException if beneficiary_addrs element is not String
        originatorAddrs.put(0, 0);
        JSONObject cloneJSONObject4 = new JSONObject(expectedJSONObject, JSONObject.getNames(expectedJSONObject));
        cloneJSONObject4.put("beneficiary_addrs", originatorAddrs);
        validateSchema(cloneJSONObject4);

        //should throw ValidationException if beneficiary_addrs element is empty
        originatorAddrs.remove(0);
        originatorAddrs.put(0, "");
        JSONObject cloneJSONObject5 = new JSONObject(expectedJSONObject, JSONObject.getNames(expectedJSONObject));
        cloneJSONObject5.put("beneficiary_addrs", originatorAddrs);
        validateSchema(cloneJSONObject5);
    }

    @Test
    public void testTransactionCurrency() {
        //should throw ValidationException if transaction_currency is missing
        JSONObject cloneJSONObject1 = new JSONObject(expectedJSONObject, JSONObject.getNames(expectedJSONObject));
        cloneJSONObject1.remove("transaction_currency");
        validateSchema(cloneJSONObject1);

        //should throw ValidationException if transaction_currency is not String
        JSONObject cloneJSONObject2 = new JSONObject(expectedJSONObject, JSONObject.getNames(expectedJSONObject));
        cloneJSONObject2.put("transaction_currency", 1);
        validateSchema(cloneJSONObject2);

        //should throw ValidationException if transaction_currency is empty
        JSONObject cloneJSONObject3 = new JSONObject(expectedJSONObject, JSONObject.getNames(expectedJSONObject));
        cloneJSONObject3.put("transaction_currency", "");
        validateSchema(cloneJSONObject3);
    }

    @Test
    public void testAmount() {
        //should throw ValidationException if amount is missing
        JSONObject cloneJSONObject1 = new JSONObject(expectedJSONObject, JSONObject.getNames(expectedJSONObject));
        cloneJSONObject1.remove("amount");
        validateSchema(cloneJSONObject1);

        //should throw ValidationException if amount is string
        JSONObject cloneJSONObject2 = new JSONObject(expectedJSONObject, JSONObject.getNames(expectedJSONObject));
        cloneJSONObject2.put("amount", "1");
        validateSchema(cloneJSONObject2);

        //should throw ValidationException if amount is true
        JSONObject cloneJSONObject3 = new JSONObject(expectedJSONObject, JSONObject.getNames(expectedJSONObject));
        cloneJSONObject3.put("amount", true);
        validateSchema(cloneJSONObject3);
    }

    @Test
    public void testOriginatorAddrsExtra() {
        //should throw ValidationException if originator_addrs_extra is not object
        JSONObject cloneJSONObject1 = new JSONObject(expectedJSONObject, JSONObject.getNames(expectedJSONObject));
        cloneJSONObject1.put("originator_addrs_extra", "1");
        validateSchema(cloneJSONObject1);

        //should throw ValidationException if originator_addrs_extra property is empty
        JSONObject originatorAddrsExtra = new JSONObject();
        JSONObject cloneJSONObject2 = new JSONObject(expectedJSONObject, JSONObject.getNames(expectedJSONObject));
        cloneJSONObject2.put("originator_addrs_extra", originatorAddrsExtra);
        validateSchema(cloneJSONObject2);
    }

    @Test
    public void testBeneficiaryAddrsExtra() {
        //should throw ValidationException if beneficiary_addrs_extra is not object
        JSONObject cloneJSONObject1 = new JSONObject(expectedJSONObject, JSONObject.getNames(expectedJSONObject));
        cloneJSONObject1.put("beneficiary_addrs_extra", "1");
        validateSchema(cloneJSONObject1);

        //should throw ValidationException if beneficiary_addrs_extra property is empty
        JSONObject beneficiaryAddrsExtra = new JSONObject();
        JSONObject cloneJSONObject2 = new JSONObject(expectedJSONObject, JSONObject.getNames(expectedJSONObject));
        cloneJSONObject2.put("beneficiary_addrs_extra", beneficiaryAddrsExtra);
        validateSchema(cloneJSONObject2);
    }

    @Test
    public void testAdditionalProperties() {
        //should throw ValidationException if object has additionalProperties
        JSONObject cloneJSONObject1 = new JSONObject(expectedJSONObject, JSONObject.getNames(expectedJSONObject));
        cloneJSONObject1.put("transfer_id", "1");
        validateSchema(cloneJSONObject1);

    }

    private void validateSchema(JSONObject jsonObject) {
        try {
            Validator.validateTransactionSchema(jsonObject);
            fail("expected exception was not occured.");
        } catch (Exception ex) {
            boolean isValidationException = (ex instanceof ValidationException);
            assertEquals(isValidationException, true);
        }
    }
}
