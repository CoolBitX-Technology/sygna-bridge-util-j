package com.coolbitx.sygna.util;

/**
 * String utility class.
 */
public final class StringUtil {

    private StringUtil() {
        // Unused.
    }

    /**
     * Determine if a string is {@code null} or {@link String#isEmpty()} returns
     * {@code true}.
     */
    public static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }

    public static String leftPadWithZeroes(String originalString, int length) {
        String paddedString = originalString;
        while (paddedString.length() < length) {
            paddedString = "0" + paddedString;
        }
        return paddedString;
    }
}
