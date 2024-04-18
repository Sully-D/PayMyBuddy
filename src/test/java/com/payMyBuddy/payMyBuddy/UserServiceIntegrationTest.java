package com.payMyBuddy.payMyBuddy;

import com.payMyBuddy.payMyBuddy.model.UserAccount;
import com.payMyBuddy.payMyBuddy.repository.UserRepository;
import com.payMyBuddy.payMyBuddy.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;



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
    public void whenUserSaved() {
        // Given
        UserAccount userAccount = UserAccount.builder()
                .email("john.doe@test.com")
                .password("Azerty@123!")
                .lastName("Doe")
                .firstName("John")
                .balance(BigDecimal.valueOf(0.00))
                .role("USER")
                .build();

        // When
        userService.createUser(userAccount);

        // Then
        Optional<UserAccount> userSaved = userRepository.findByEmail(userAccount.getEmail());
        assertTrue(userSaved.isPresent());
    }

    @Test
    public void whenEditProfile_thenModificationsArePersisted() {
        // Given
        UserAccount userAccount = UserAccount.builder()
                .email("jane.doe@test.com")
                .password("Azerty123@")
                .lastName("Doe")
                .firstName("John")
                .balance(BigDecimal.valueOf(0.00))
                .role("USER")
                .build();

        userService.createUser(userAccount);

        UserAccount modification = UserAccount.builder()
                .lastName("Doe")
                .firstName("J")
                .build();

        // When
        userService.editProfile(userAccount.getId(), modification.getFirstName(), modification.getLastName());

        // Then
        Optional<UserAccount> updatedUser = userRepository.findById(userAccount.getId());
        assertTrue(updatedUser.isPresent());
        assertEquals("J", updatedUser.get().getFirstName());
        assertEquals("Doe", updatedUser.get().getLastName());
    }

    @Test
    public void whenEditProfile_thenModificationsInvalidFormat() {
        // Given
        UserAccount userAccount = UserAccount.builder()
                .email("jane.doe@test.com")
                .password("Azerty123@")
                .lastName("Doe")
                .firstName("John")
                .balance(BigDecimal.valueOf(0.00))
                .role("USER")
                .build();

        userService.createUser(userAccount);

        UserAccount modification = UserAccount.builder()
                .lastName("Doe")
                .firstName("")
                .build();

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> userService.editProfile(userAccount.getId(),
                modification.getFirstName(), modification.getLastName()));
    }
}
