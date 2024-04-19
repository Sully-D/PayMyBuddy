package com.payMyBuddy.payMyBuddy;

import static org.junit.jupiter.api.Assertions.*;

import com.payMyBuddy.payMyBuddy.model.UserAccount;
import com.payMyBuddy.payMyBuddy.repository.SenderRecipientConnectionRepository;
import com.payMyBuddy.payMyBuddy.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Optional;


@SpringBootTest
public class UserRepositoryIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    SenderRecipientConnectionRepository senderRecipientConnectionRepository;

    @BeforeEach
    public void setUp() {
        senderRecipientConnectionRepository.deleteAll();
        senderRecipientConnectionRepository.flush();
        userRepository.deleteAll();
        userRepository.flush();
    }


    @Test
    public void whenFindByEmail_thenReturnUser() {
        // Given
        UserAccount newUser = UserAccount.builder()
                .email("john.doe@test.com")
                .password("Azerty123@")
                .lastName("Doe")
                .firstName("John")
                .balance(BigDecimal.valueOf(0.00))
                .role("USER")
                .build();

        userRepository.save(newUser);

        // When
        Optional<UserAccount> found = userRepository.findByEmail(newUser.getEmail());

        // Then
        assertTrue(found.isPresent());
    }
}
