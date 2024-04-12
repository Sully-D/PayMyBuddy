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

    public void createUser(UserAccount newUser) {

        logger.info("Start UserService.createUser()");

        Utils.checkArguments(newUser.getEmail(), "Email");
        Utils.checkArguments(newUser.getPassword(), "Password");
        Utils.checkArguments(newUser.getLastName(), "LastName");
        Utils.checkArguments(newUser.getFirstName(), "FirstName");
        Utils.checkArguments(newUser.getBalance(), "Balance");

        Utils.checkEmailFormat(newUser.getEmail());
        Utils.checkPasswordFormat(newUser.getPassword());

        Optional<UserAccount> existingUser = userRepository.findByEmail(newUser.getEmail());
        if (existingUser.isPresent()) {
            throw new UserAlreadyExistsException("A user with this email already exists : " + newUser.getEmail());
        }

        // Hash password
        newUser.setPassword(bCryptPasswordEncoder.encode(newUser.getPassword()));
        UserAccount saveUser = UserAccount.builder()
                .email(newUser.getEmail())
                .password(bCryptPasswordEncoder.encode(newUser.getPassword()))
                .lastName(newUser.getLastName())
                .firstName(newUser.getFirstName())
                .balance(BigDecimal.valueOf(0.00))
                .build();

        logger.info("New user saved");
        userRepository.save(saveUser);
    }
}
