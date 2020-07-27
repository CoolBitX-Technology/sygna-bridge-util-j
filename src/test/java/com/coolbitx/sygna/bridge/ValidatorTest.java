package com.coolbitx.sygna.bridge;

import static org.junit.Assert.*;

import org.junit.Test;

import com.coolbitx.sygna.util.Validator;

public class ValidatorTest {
    
    @Test
    public void testValidatePrivateKey() {
        String expectedErrorMessage = "privateKey length should NOT be shorter than 1";
        try {
            String privateKey = null;
            Validator.validatePrivateKey(privateKey);
            fail("expected exception was not occured.");
        } catch (Exception e) {
            assertEquals(e.getMessage(), expectedErrorMessage);
        }
        
        try {
            String privateKey = "";
            Validator.validatePrivateKey(privateKey);
            fail("expected exception was not occured.");
        } catch (Exception e) {
            assertEquals(e.getMessage(), expectedErrorMessage);
        }
        
        try {
            String privateKey = "1234";
            Validator.validatePrivateKey(privateKey);
        } catch (Exception e) {
            fail("unexpected exception was occured.");
        }
    }
}
