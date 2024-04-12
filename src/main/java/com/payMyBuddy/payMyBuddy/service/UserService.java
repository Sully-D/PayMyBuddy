package com.payMyBuddy.payMyBuddy.service;

import com.payMyBuddy.payMyBuddy.exception.UserAlreadyExistsException;
import com.payMyBuddy.payMyBuddy.model.UserAccount;
import com.payMyBuddy.payMyBuddy.repository.UserRepository;
import com.payMyBuddy.payMyBuddy.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class UserService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

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

        // Creates a new user account with zero initial balance
        UserAccount saveUser = UserAccount.builder()
                .email(newUser.getEmail())
                .password(encodedPassword)
                .lastName(newUser.getLastName())
                .firstName(newUser.getFirstName())
                .balance(BigDecimal.valueOf(0.00))
                .build();

        logger.info("New user saved");

        // Persist the new user in the repository
        userRepository.save(saveUser);
    }
}
