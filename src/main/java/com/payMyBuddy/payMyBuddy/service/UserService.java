package com.payMyBuddy.payMyBuddy.service;

import com.payMyBuddy.payMyBuddy.config.PasswordEncoder;
import com.payMyBuddy.payMyBuddy.model.User;
import com.payMyBuddy.payMyBuddy.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void createUser(User user) {

        private final UserRepository userRepository;

    }
}
