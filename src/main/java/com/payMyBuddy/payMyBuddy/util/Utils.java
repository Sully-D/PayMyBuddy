package com.payMyBuddy.payMyBuddy.util;

import java.util.regex.Pattern;

public class Utils {

    private static final String EMAIL_REGEX =
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    public static void checkArguments(Object arg, String argName) {
        if (arg == null) {
            throw new IllegalArgumentException(argName + " cannot be null.");
        }
        if (arg instanceof String && ((String) arg).trim().isEmpty()) {
            throw new IllegalArgumentException(argName + " cannot be empty.");
        }
    }

    public static void checkEmailFormat(String email) {
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }
}
