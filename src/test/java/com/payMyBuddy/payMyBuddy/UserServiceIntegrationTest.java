package com.payMyBuddy.payMyBuddy;

import com.payMyBuddy.payMyBuddy.model.UserAccount;
import com.payMyBuddy.payMyBuddy.repository.UserRepository;
import com.payMyBuddy.payMyBuddy.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
public class UserServiceIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
        userRepository.flush();
    }

    @Test
    public void whenEditProfile_thenModificationsArePersisted() {
        // Given
        UserAccount userAccount = UserAccount.builder()
                .email("john.doe@test2.com")
                .password("Azerty123@")
                .lastName("Doe")
                .firstName("John")
                .balance(BigDecimal.valueOf(0.00))
                .build();

        userRepository.save(userAccount);

        UserAccount modification = UserAccount.builder()
                .lastName("Doe")
                .firstName("J")
                .build();

        // When
        userService.editProfile(userAccount.getId(), modification.getFirstName(), modification.getLastName());

        // Retrieve the updated user
        Optional<UserAccount> updatedUser = userRepository.findById(userAccount.getId());
        //Optional<UserAccount> updatedUser = userRepository.findByEmail(userAccount.getEmail());
        assertTrue(updatedUser.isPresent());
        assertEquals("J", updatedUser.get().getFirstName());
        assertEquals("Doe", updatedUser.get().getLastName());
    }
}
