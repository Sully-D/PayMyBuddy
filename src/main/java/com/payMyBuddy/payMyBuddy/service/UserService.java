package com.payMyBuddy.payMyBuddy.service;

import com.payMyBuddy.payMyBuddy.exception.UserAlreadyExistsException;
import com.payMyBuddy.payMyBuddy.model.User;
import com.payMyBuddy.payMyBuddy.repository.UserRepository;
import com.payMyBuddy.payMyBuddy.util.Utils;
import jdk.jshell.execution.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public void createUser(String email, String rawPassword, String lastName, String firstName) {

        logger.info("Start UserService.createUser()");

        Utils.checkArguments(email, "Email");
        Utils.checkArguments(rawPassword, "Password");
        Utils.checkArguments(lastName, "LastName");
        Utils.checkArguments(firstName, "FirstName");

        Utils.checkEmailFormat(email);

        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            throw new UserAlreadyExistsException("A user with this email already exists : " + email);
        }

        User user = User.builder()
                .email(email)
                // Hash password
                .password(bCryptPasswordEncoder.encode(rawPassword))
                .lastName(lastName)
                .firstName(firstName)
                .build();

        logger.info("New user saved");
        userRepository.save(user);
    }
}
