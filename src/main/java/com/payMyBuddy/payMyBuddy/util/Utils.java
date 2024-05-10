package com.payMyBuddy.payMyBuddy.util;

import java.math.BigDecimal;
import java.util.regex.Pattern;

/**
 * Utility class providing methods to validate email, password, and general argument conditions.
 * This class helps enforce data integrity and prevent invalid data from being processed or stored.
 */
public class Utils {

    // Regular expression for validating email format.
    private static final String EMAIL_REGEX =
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    // Regular expression for validating password strength and format.
    private static final String PASSWORD_REGEX =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@!?;:#$%^&+=])(?=\\S+$).{8,24}$";

    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(PASSWORD_REGEX);

    /**
     * Validates that an argument is not null and not empty (if it is a string).
     *
     * @param arg The argument to check.
     * @param argName The name of the argument, used for generating exception messages.
     * @throws IllegalArgumentException if the argument is {@code null} or empty (for strings).
     */
    public static void checkArguments(Object arg, String argName) {
        if (arg == null) {
            throw new IllegalArgumentException(argName + " cannot be null.");
        }
        if (arg instanceof String && ((String) arg).trim().isEmpty()) {
            throw new IllegalArgumentException(argName + " cannot be empty.");
        }
    }

    /**
     * Validates the format of an email address against a regular expression.
     *
     * @param email The email address to validate.
     * @throws IllegalArgumentException if the email does not match the expected format.
     */
    public static void checkEmailFormat(String email) {
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }

    /**
     * Validates the format of a password against a regular expression.
     *
     * @param password The password to validate.
     * @throws IllegalArgumentException if the password does not meet complexity requirements.
     */
    public static void checkPasswordFormat(String password) {
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            throw new IllegalArgumentException("Invalid password format. The password must be between " +
                    "8 and 24 characters long, include upper and lower case letters, numbers, and special " +
                    "symbols among [@#$%^&+=]. Given: ");
        }
    }

    /**
     * Validates that a user ID is non-null and positive.
     *
     * @param id The user ID to validate.
     * @throws IllegalArgumentException if the user ID is null or less than or equal to zero.
     */
    public static void validateUserId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("User ID cannot be null.");
        }
        if (id < 0) {
            throw new IllegalArgumentException("User ID must be greater than zero.");
        }
    }

    /**
     * Validates that a given amount is not null and greater than zero.
     * This method is used to ensure that monetary values are positive before processing.
     *
     * @param amount The BigDecimal value representing the amount to be validated.
     * @throws IllegalArgumentException if the amount is {@code null} or not greater than zero.
     */
    public static void valideAmount(BigDecimal amount) {
        // Check if the amount is null
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null.");
        }

        // Check if the amount is positive
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }
    }
}
