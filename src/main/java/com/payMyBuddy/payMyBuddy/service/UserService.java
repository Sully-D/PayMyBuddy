package com.payMyBuddy.payMyBuddy.service;

import com.payMyBuddy.payMyBuddy.exception.UserAlreadyExistsException;
import com.payMyBuddy.payMyBuddy.exception.UserNotFoundException;
import com.payMyBuddy.payMyBuddy.model.UserAccount;
import com.payMyBuddy.payMyBuddy.repository.UserRepository;
import com.payMyBuddy.payMyBuddy.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class UserService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    /**
     * Constructs a new UserService with necessary dependencies.
     *
     * @param bCryptPasswordEncoder The password encoder used for encoding passwords before saving to the database.
     * @param userRepository The repository for user data access operations.
     */
    @Autowired
    public UserService(BCryptPasswordEncoder bCryptPasswordEncoder, UserRepository userRepository) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userRepository = userRepository;
    }


    /**
     * Creates a new user account in the system with initial checks for data validity,
     * email uniqueness, and secure password storage.
     *
     * @param newUser The {@link UserAccount} object containing the new user's data.
     *                It must not be {@code null}, and all fields must be properly initialized.
     * @throws IllegalArgumentException If any essential attribute of {@code newUser} is {@code null}.
     * @throws UserAlreadyExistsException If a user with the same email already exists in the system.
     */
    @Transactional
    public void createUser(UserAccount newUser) {

        logger.info("Start UserService.createUser()");

        newUser.setBalance(BigDecimal.valueOf(0));
        newUser.setRole("USER");

        // Validates non-null fields of the new user
        Utils.checkArguments(newUser.getEmail(), "Email");
        Utils.checkArguments(newUser.getPassword(), "Password");
        Utils.checkArguments(newUser.getLastName(), "LastName");
        Utils.checkArguments(newUser.getFirstName(), "FirstName");
        Utils.checkArguments(newUser.getBalance(), "Balance");

        // Validates the format of the email and password
        Utils.checkEmailFormat(newUser.getEmail());
        Utils.checkPasswordFormat(newUser.getPassword());

        // Checks if an existing user with the same email already exists
        Optional<UserAccount> existingUser = userRepository.findByEmail(newUser.getEmail());
        if (existingUser.isPresent()) {
            throw new UserAlreadyExistsException("A user with this email already exists : " + newUser.getEmail());
        }

        // Hashes the user's password for secure storage
        String encodedPassword = bCryptPasswordEncoder.encode(newUser.getPassword());
        newUser.setPassword(encodedPassword);

        logger.info("New user saved");

        // Persist the new user in the repository
        userRepository.save(newUser);
    }

    /**
     * Updates the profile details of an existing user.
     * This method specifically updates the user's last name and first name based on the provided user ID.
     *
     * @param id The unique identifier of the user whose profile is to be updated.
     * @param firstName The new last name to set for the user. If it's {@code null}, the last name won't be updated.
     * @param firstName The new first name to set for the user. If it's {@code null}, the first name won't be updated.
     * @throws IllegalArgumentException If the {@code id} is less than or equal to zero, indicating an invalid user ID.
     */
    @Transactional
    public void editProfile(long id, String firstName, String lastName) {
        // Check for a valid user ID
        if (id <= 0) {
            throw new IllegalArgumentException("User ID must be greater than zero.");
        }
        Utils.checkArguments(firstName, "FirstName");
        Utils.checkArguments(lastName, "LastName");

        // Call to repository to update the user's last name and first name based on the user ID
        userRepository.updateUser(id, firstName, lastName);
    }

    /**
     * Retrieves the currently authenticated user account from the security context.
     * This method assumes that the user is authenticated and their email is valid.
     *
     * @return An Optional containing the authenticated user's account, or empty if not found.
     * @throws IllegalStateException if no user is currently authenticated.
     */
    @Transactional
    public Optional<UserAccount> getCurrentUser() {
        // Get the current authentication context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if the user is authenticated
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No user currently authenticated");
        }

        // Retrieve the username from the authentication context
        String username = authentication.getName();

        // Fetch the user account associated with the username (email)
        return userRepository.findByEmail(username);
    }

    /**
     * Adds a specified amount of funds to the user's account with the given ID.
     *
     * @param id The unique ID of the user to whom the funds will be added.
     * @param amount The amount to be added, which must be a positive value.
     * @throws IllegalArgumentException if the user ID is zero or negative, or if the amount is invalid.
     */
    @Transactional
    public void addFund(long id, BigDecimal amount) {
        // Validate that the user ID is positive
        if (id <= 0) {
            throw new IllegalArgumentException("User ID must be greater than zero.");
        }

        // Validate the provided amount
        Utils.valideAmount(amount);

        // Add the funds to the user account through the repository
        userRepository.addFund(id, amount);
    }

    /**
     * Withdraws a specified amount from the currently authenticated user's account.
     * Ensures the user has sufficient balance and that the amount is valid.
     *
     * @param amount The amount to withdraw, which must be a positive value.
     * @param iban The International Bank Account Number for the withdrawal.
     * @throws IllegalArgumentException if the amount is invalid or if the user lacks sufficient balance.
     * @throws UserNotFoundException if the current user cannot be retrieved or is not authenticated.
     */
    @Transactional
    public void withdraw(BigDecimal amount, String iban) {
        // Validate the amount to be withdrawn
        Utils.valideAmount(amount);

        // Retrieve the currently authenticated user
        Optional<UserAccount> user = getCurrentUser();
        if (user.isEmpty()) {
            throw new UserNotFoundException("Invalid user or user not connected");
        }

        // Retrieve the user's account object
        UserAccount currentUser = user.get();

        // Check for sufficient funds
        if (currentUser.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient funds");
        }

        // Deduct the amount from the user's balance
        currentUser.setBalance(currentUser.getBalance().subtract(amount));

        // Save the updated user balance to the repository
        userRepository.save(currentUser);
    }

}
