package com.pointburst.jsmusic.utils;

/**
 * Created by FARHAN on 12/27/2014.
 */
public class StringUtils {

    /**
     * @param pStr
     *            String object to be tested.
     * @returns true if the given string is null or empty or contains spaces
     *          only.
     */
    public static boolean isNullOrEmpty(final String pStr) {
        return pStr == null || pStr.trim().length() == 0 || pStr.trim().equalsIgnoreCase("null");
    }
}
