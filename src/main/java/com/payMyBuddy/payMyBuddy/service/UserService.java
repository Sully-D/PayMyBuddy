package com.payMyBuddy.payMyBuddy.service;

import com.payMyBuddy.payMyBuddy.exception.UserAlreadyExistsException;
import com.payMyBuddy.payMyBuddy.model.UserAccount;
import com.payMyBuddy.payMyBuddy.repository.UserRepository;
import com.payMyBuddy.payMyBuddy.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

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

    public void createUser(UserAccount userAccount) {

        logger.info("Start UserService.createUser()");

        Utils.checkArguments(userAccount.getEmail(), "Email");
        Utils.checkArguments(userAccount.getPassword(), "Password");
        Utils.checkArguments(userAccount.getLastName(), "LastName");
        Utils.checkArguments(userAccount.getFirstName(), "FirstName");
        Utils.checkArguments(userAccount.getBalance(), "Balance");

        Utils.checkEmailFormat(userAccount.getEmail());

        Optional<UserAccount> existingUser = userRepository.findByEmail(userAccount.getEmail());
        if (existingUser.isPresent()) {
            throw new UserAlreadyExistsException("A user with this email already exists : " + userAccount.getEmail());
        }

        // Hash password
        userAccount.setPassword(bCryptPasswordEncoder.encode(userAccount.getPassword()));

        logger.info("New user saved");
        userRepository.save(userAccount);
    }
}
