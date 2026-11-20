package com.devpilotfml.backend.util;

public class Conversions {
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
}
