package com.devpilotfml.backend.util;

public class Conversions {
    public static Integer toInteger(Object value) {
        // Null
        if (value == null) {
            return null;
        }

        // Number is parent wrapper of Int, Long etc
        if (value instanceof Number number) {
            return number.intValue();
        }

        return Integer.parseInt(String.valueOf(value));
    }

    public static Long toLong(Object value) {
        // Null
        if (value == null) {
            return null;
        }
        
        // Number is parent wrapper of Int, Long etc
        if (value instanceof Number number) {
            return number.longValue();
        }

        return Long.parseLong(String.valueOf(value));
    }

    public static String toString(Object value) {
        return value == null ? null : String.valueOf(value);
    }
}
