package com.coolbitx.sygna.bridge;

import static org.junit.Assert.*;

import org.junit.Test;

import com.coolbitx.sygna.util.DateUtils;

public class DateUtilsTest {
	

	@Test
	public void testCheckExpireDateValid() {
	    try {
	    	long expireDate = 1581997090l; //GMT 2020/02/18 03:38:10
			DateUtils.checkExpireDateValid(expireDate);
	        fail("expected exception was not occured.");
	    } catch(Exception e) {
	    	assertEquals(e.getMessage(), "expireDate is too short");
	    }
	    
	    try {
	    	long expireDate = 1581997090000l; //GMT 2020/02/18 03:38:10:000
			DateUtils.checkExpireDateValid(expireDate);
	        fail("expected exception was not occured.");
	    } catch(Exception e) {
	    	assertEquals(e.getMessage(), "expireDate is too short");
	    }
	    
	    try {
	    	long expireDate = 4106604075000l; //GMT 2100/02/18 03:21:15:000
			DateUtils.checkExpireDateValid(expireDate);
	    } catch(Exception e) {
	    	fail("Should not have thrown any exception");
	    }
	    
	    
	}
	

}
