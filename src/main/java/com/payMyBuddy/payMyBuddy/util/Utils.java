package com.payMyBuddy.payMyBuddy.util;

import java.util.regex.Pattern;

public class Utils {

    private static final String EMAIL_REGEX =
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    private static final String PASSWORD_REGEX =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@!?;:#$%^&+=])(?=\\S+$).{8,24}$";

    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(PASSWORD_REGEX);

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

    public static void checkPasswordFormat(String password) {
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            throw new IllegalArgumentException("Invalid password format. The password must be between " +
                    "8 and 24 characters long, include upper and lower case letters, numbers, and special " +
                    "symbols among [@#$%^&+=]. Given: ");
        }
    }
}
