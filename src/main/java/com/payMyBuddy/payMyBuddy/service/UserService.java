package com.payMyBuddy.payMyBuddy.service;

import com.payMyBuddy.payMyBuddy.model.User;
import com.payMyBuddy.payMyBuddy.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    UserRepository userRepository;

    public void createUser(String email, String rawPassword, String lastName, String firstName) {

        User user = User.builder()
                .email(email)
                // Hash password
                .password(bCryptPasswordEncoder.encode(rawPassword))
                .lastName(lastName)
                .firstName(firstName)
                .build();

        userRepository.save(user);
    }
}
