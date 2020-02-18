package com.coolbitx.sygna.util;

import java.util.Calendar;

/**
 * Date utility class.
 */
public final class DateUtils {
	private static final int EXPIRE_DATE_MIN_OFFSET = 60;
	private DateUtils() {
		// Unused.
	}
	
	/**
     * Check if expireDate is valid
     */
	public static void checkExpireDateValid(long expireDate) throws Exception{
		Calendar calender = Calendar.getInstance();
		calender.add(Calendar.SECOND, EXPIRE_DATE_MIN_OFFSET);
		System.out.println(calender.getTimeInMillis());
		if(expireDate < calender.getTimeInMillis()) {
			throw new Exception("expireDate is too short");
		}
    }
	
}
