package com.coolbitx.sygna.bridge;

import com.coolbitx.sygna.bridge.enums.PermissionStatus;
import com.coolbitx.sygna.bridge.enums.RejectCode;
import static org.junit.Assert.*;

import org.junit.Test;

import com.coolbitx.sygna.util.Validator;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

public class ValidatorTest {

    @Test
    public void testValidateSignature() {
        String expectedErrorMessage = "signature length should be 128";
        try {
            String signature = null;
            Validator.validateSignature(signature);
            fail("expected exception was not occured.");
        } catch (Exception e) {
            assertEquals(e.getMessage(), expectedErrorMessage);
        }

        try {
            String signature = "";
            Validator.validateSignature(signature);
            fail("expected exception was not occured.");
        } catch (Exception e) {
            assertEquals(e.getMessage(), expectedErrorMessage);
        }

        try {
            String signature = "123";
            Validator.validateSignature(signature);
            fail("expected exception was not occured.");
        } catch (Exception e) {
            assertEquals(e.getMessage(), expectedErrorMessage);
        }

        try {
            String signature = "6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b1";// length=129
            Validator.validateSignature(signature);
            fail("expected exception was not occured.");
        } catch (Exception e) {
            assertEquals(e.getMessage(), expectedErrorMessage);
        }

        try {
            String signature = "6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b";// length=128
            Validator.validateSignature(signature);
        } catch (Exception e) {
            fail("unexpected exception was occured.");
        }
    }

    @Test
    public void testValidateTransferId() {
        String expectedErrorMessage = "transferId length should be 64";
        try {
            String transferId = null;
            Validator.validateTransferId(transferId);
            fail("expected exception was not occured.");
        } catch (Exception e) {
            assertEquals(e.getMessage(), expectedErrorMessage);
        }

        try {
            String transferId = "";
            Validator.validateTransferId(transferId);
            fail("expected exception was not occured.");
        } catch (Exception e) {
            assertEquals(e.getMessage(), expectedErrorMessage);
        }

        try {
            String transferId = "123";
            Validator.validateTransferId(transferId);
            fail("expected exception was not occured.");
        } catch (Exception e) {
            assertEquals(e.getMessage(), expectedErrorMessage);
        }

        try {
            String transferId = "6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b1";// length=65
            Validator.validateTransferId(transferId);
            fail("expected exception was not occured.");
        } catch (Exception e) {
            assertEquals(e.getMessage(), expectedErrorMessage);
        }

        try {
            String transferId = "6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b";// length=64
            Validator.validateTransferId(transferId);
        } catch (Exception e) {
            fail("unexpected exception was occured.");
        }
    }

    @Test
    public void testValidatePermissionStatus() {
        String expectedErrorMessage = String.format("permissionStatus should be equal to one of the allowed values[%s,%s]",
                PermissionStatus.ACCEPTED.getStatus(), PermissionStatus.REJECTED.getStatus());
        try {
            String permissionStatus = null;
            Validator.validatePermissionStatus(permissionStatus);
            fail("expected exception was not occured.");
        } catch (Exception e) {
            assertEquals(e.getMessage(), expectedErrorMessage);
        }

        try {
            String permissionStatus = "";
            Validator.validatePermissionStatus(permissionStatus);
            fail("expected exception was not occured.");
        } catch (Exception e) {
            assertEquals(e.getMessage(), expectedErrorMessage);
        }

        try {
            String permissionStatus = "123";
            Validator.validatePermissionStatus(permissionStatus);
            fail("expected exception was not occured.");
        } catch (Exception e) {
            assertEquals(e.getMessage(), expectedErrorMessage);
        }

        try {
            String permissionStatus = PermissionStatus.ACCEPTED.getStatus();
            Validator.validatePermissionStatus(permissionStatus);
        } catch (Exception e) {
            fail("Should not have thrown any exception");
        }

        try {
            String permissionStatus = PermissionStatus.REJECTED.getStatus();
            Validator.validatePermissionStatus(permissionStatus);
        } catch (Exception e) {
            fail("unexpected exception was occured.");
        }
    }

    @Test
    public void testValidateExpireDate() {
        Calendar fakeNow = Calendar.getInstance();
        fakeNow.setTime(new Date(1582770600000l)); //GMT+08:00 2020/02/27 10:30:00:000
        String expectedErrorMessage = String.format("expire_date should be at least %d seconds away from the current time",
                180);
        try {

            long expireDate = 1582770489l; //GMT+08:00 2020/02/27 10:28:09
            Validator.validateExpireDate(fakeNow, expireDate);
            fail("expected exception was not occured.");
        } catch (Exception e) {
            assertEquals(e.getMessage(), expectedErrorMessage);
        }

        try {
            long expireDate = 1582770748l; //GMT+08:00 2020/02/27 10:32:28
            Validator.validateExpireDate(fakeNow, expireDate);
            fail("expected exception was not occured.");
        } catch (Exception e) {
            assertEquals(e.getMessage(), expectedErrorMessage);
        }

        try {
            long expireDate = 1582770748000l; //GMT+08:00 2020/02/27 10:32:28:000
            Validator.validateExpireDate(fakeNow, expireDate);
            fail("expected exception was not occured.");
        } catch (Exception e) {
            assertEquals(e.getMessage(), expectedErrorMessage);
        }

        try {
            long expireDate = 1582770780l; //GMT+08:00 2020/02/27 10:33:00
            Validator.validateExpireDate(fakeNow, expireDate);
            fail("expected exception was not occured.");
        } catch (Exception e) {
            assertEquals(e.getMessage(), expectedErrorMessage);
        }

        try {
            long expireDate = 1582771094l; //GMT+08:00 2020/02/27 10:38:14
            Validator.validateExpireDate(fakeNow, expireDate);
            fail("expected exception was not occured.");
        } catch (Exception e) {
            assertEquals(e.getMessage(), expectedErrorMessage);
        }

        try {
            long expireDate = -1l;
            Validator.validateExpireDate(fakeNow, expireDate);
            fail("expected exception was not occured.");
        } catch (Exception e) {
            assertEquals(e.getMessage(), expectedErrorMessage);
        }

        try {
            long expireDate = 0l;
            Validator.validateExpireDate(fakeNow, expireDate);
            expireDate = 1582770780000l; //GMT+08:00 2020/02/27 10:33:00:000
            Validator.validateExpireDate(fakeNow, expireDate);
            expireDate = 1582771094000l; //GMT+08:00 2020/02/27 10:38:14:000
            Validator.validateExpireDate(fakeNow, expireDate);
        } catch (Exception e) {
            fail("unexpected exception was occured.");
        }
    }

    @Test
    public void testValidateRejectDate() {
        String expectedErrorMessage = "rejectCode cannot be blank if permissionStatus is REJECTED";
        String expectedErrorMessage1 = "rejectMessage cannot be blank if rejectCode is BVRC999";

        try {
            Validator.validateRejectData(PermissionStatus.REJECTED.getStatus(), RejectCode.NULL, null);
            fail("expected exception was not occured.");
        } catch (Exception e) {
            assertEquals(e.getMessage(), expectedErrorMessage);
        }

        try {
            Validator.validateRejectData(PermissionStatus.REJECTED.getStatus(), RejectCode.NULL, "unsupported_currency");
            fail("expected exception was not occured.");
        } catch (Exception e) {
            assertEquals(e.getMessage(), expectedErrorMessage);
        }

        try {
            Validator.validateRejectData(PermissionStatus.REJECTED.getStatus(), RejectCode.BVRC999, null);
            fail("expected exception was not occured.");
        } catch (Exception e) {
            assertEquals(e.getMessage(), expectedErrorMessage1);
        }

        try {
            Validator.validateRejectData(PermissionStatus.ACCEPTED.getStatus(), RejectCode.NULL, null);
            Validator.validateRejectData(PermissionStatus.REJECTED.getStatus(), RejectCode.BVRC001, null);
            Validator.validateRejectData(PermissionStatus.REJECTED.getStatus(), RejectCode.BVRC001, "");
            Validator.validateRejectData(PermissionStatus.REJECTED.getStatus(), RejectCode.BVRC001, "unsupported_currency");
            Validator.validateRejectData(PermissionStatus.REJECTED.getStatus(), RejectCode.BVRC999, "unsupported_currency");
        } catch (Exception e) {
            fail("unexpected exception was occured.");
        }

    }

    @Test
    public void testValidateTxId() {
        String expectedErrorMessage = "txid length should NOT be shorter than 1";
        try {
            String txid = null;
            Validator.validateTxid(txid);
            fail("expected exception was not occured.");
        } catch (Exception e) {
            assertEquals(e.getMessage(), expectedErrorMessage);
        }

        try {
            String txid = "";
            Validator.validateTxid(txid);
            fail("expected exception was not occured.");
        } catch (Exception e) {
            assertEquals(e.getMessage(), expectedErrorMessage);
        }

        try {
            String txid = "1234";
            Validator.validateTxid(txid);
        } catch (Exception e) {
            fail("Should not have thrown any exception");
        }
    }

    @Test
    public void testValidatePrivateInfo() {
        String expectedErrorMessage = "privateInfo length should NOT be shorter than 1";
        try {
            String privateInfo = null;
            Validator.validatePrivateInfo(privateInfo);
            fail("expected exception was not occured.");
        } catch (Exception e) {
            assertEquals(e.getMessage(), expectedErrorMessage);
        }

        try {
            String privateInfo = "";
            Validator.validatePrivateInfo(privateInfo);
            fail("expected exception was not occured.");
        } catch (Exception e) {
            assertEquals(e.getMessage(), expectedErrorMessage);
        }

        try {
            String privateInfo = "1234";
            Validator.validatePrivateInfo(privateInfo);
        } catch (Exception e) {
            fail("unexpected exception was occured.");
        }
    }

    @Test
    public void testValidateDataDate() {

        try {
            String dataDate = null;
            Validator.validateDataDate(dataDate);
            fail("expected exception was not occured.");
        } catch (Exception e) {
            assertEquals(e.getMessage(), "dataDate length should NOT be shorter than 1");
        }

        try {
            String dataDate = "";
            Validator.validateDataDate(dataDate);
            fail("expected exception was not occured.");
        } catch (Exception e) {
            assertEquals(e.getMessage(), "dataDate length should NOT be shorter than 1");
        }

        try {
            String dataDate = "123";
            Validator.validateDataDate(dataDate);
            fail("expected exception was not occured.");
        } catch (Exception e) {
            boolean isParseException = (e instanceof ParseException);
            assertEquals(isParseException, true);
        }

        try {
            String dataDate = "2019/07/29 06:29:00";
            Validator.validateDataDate(dataDate);
            fail("expected exception was not occured.");
        } catch (Exception e) {
            boolean isParseException = (e instanceof ParseException);
            assertEquals(isParseException, true);
        }

        try {
            String dataDate = "2019-07-29 06:29:00";
            Validator.validateDataDate(dataDate);
            fail("expected exception was not occured.");
        } catch (Exception e) {
            boolean isParseException = (e instanceof ParseException);
            assertEquals(isParseException, true);
        }

        try {
            String dataDate = "2019-07-29T06:29:00.000";
            Validator.validateDataDate(dataDate);
            fail("expected exception was not occured.");
        } catch (Exception e) {
            boolean isParseException = (e instanceof ParseException);
            assertEquals(isParseException, true);
        }

        try {
            String dataDate = "2019-07-29T06:29:00.123Z";
            Validator.validateDataDate(dataDate);
        } catch (Exception e) {
            fail("unexpected exception was occured.");
        }
    }

    @Test
    public void testValidateUrl() {

        try {
            String url = null;
            Validator.validateUrl(url);
            fail("expected exception was not occured.");
        } catch (Exception e) {
            assertEquals(e.getMessage(), "url length should NOT be shorter than 1");
        }

        try {
            String url = "";
            Validator.validateUrl(url);
            fail("expected exception was not occured.");
        } catch (Exception e) {
            assertEquals(e.getMessage(), "url length should NOT be shorter than 1");
        }

        try {
            String url = "123";
            Validator.validateUrl(url);
            fail("expected exception was not occured.");
        } catch (Exception e) {
            boolean isMalformedURLException = (e instanceof MalformedURLException);
            assertEquals(isMalformedURLException, true);
        }

        try {
            String url = "https://api.sygna.io/api/v1.1.0/bridge/";
            Validator.validateUrl(url);
        } catch (Exception e) {
            fail("unexpected exception was occured.");
        }
    }

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

    @Test
    public void testValidateVaspCode() {
        String expectedErrorMessage = "vaspCode length should NOT be shorter than 1";
        try {
            String vaspCode = null;
            Validator.validateVaspCode(vaspCode);
            fail("expected exception was not occured.");
        } catch (Exception e) {
            assertEquals(e.getMessage(), expectedErrorMessage);
        }

        try {
            String vaspCode = "";
            Validator.validateVaspCode(vaspCode);
            fail("expected exception was not occured.");
        } catch (Exception e) {
            assertEquals(e.getMessage(), expectedErrorMessage);
        }

        try {
            String vaspCode = "1234";
            Validator.validateVaspCode(vaspCode);
        } catch (Exception e) {
            fail("Should not have thrown any exception");
        }
    }

}
