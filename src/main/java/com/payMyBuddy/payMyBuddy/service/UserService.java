package com.payMyBuddy.payMyBuddy.service;

import com.payMyBuddy.payMyBuddy.exception.UserAlreadyExistsException;
import com.payMyBuddy.payMyBuddy.model.UserAccount;
import com.payMyBuddy.payMyBuddy.repository.UserRepository;
import com.payMyBuddy.payMyBuddy.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
     * @param lastName The new last name to set for the user. If it's {@code null}, the last name won't be updated.
     * @param firstName The new first name to set for the user. If it's {@code null}, the first name won't be updated.
     * @throws IllegalArgumentException If the {@code id} is less than or equal to zero, indicating an invalid user ID.
     */
    @Transactional
    public void editProfile(long id, String lastName, String firstName) {
        // Check for a valid user ID
        if (id <= 0) {
            throw new IllegalArgumentException("User ID must be greater than zero.");
        }
        Utils.checkArguments(lastName, "LastName");
        Utils.checkArguments(firstName, "FirstName");

        // Call to repository to update the user's last name and first name based on the user ID
        userRepository.updateUser(id, lastName, firstName);
    }


}
