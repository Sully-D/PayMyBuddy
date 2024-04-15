package com.payMyBuddy.payMyBuddy;

import com.payMyBuddy.payMyBuddy.model.UserAccount;
import com.payMyBuddy.payMyBuddy.repository.UserRepository;
import com.payMyBuddy.payMyBuddy.service.UserService;
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

    @Test
    public void createUser_EncryptPassword() {
        // Given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(bCryptPasswordEncoder.encode(anyString())).thenAnswer(invocation -> "hashed" + invocation.getArgument(0));

        UserAccount newUser = UserAccount.builder()
                .email("john.doe@test.com")
                .password("Azerty123@")
                .lastName("Doe")
                .firstName("John")
                .balance(BigDecimal.valueOf(0.00))
                .build();

        // When
        userService.createUser(newUser);

        // Then
        assertNotEquals("Azerty123@", newUser.getPassword(), "Password must be encrypted");
        assertTrue(newUser.getPassword().startsWith("hashed"), "The encrypted password must begin with 'hashed'.");
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
        userService.editProfile(userAccount, modification);

        // Retrieve the updated user
        //Optional<UserAccount> updatedUser = userRepository.findById(userAccount.getId());
        Optional<UserAccount> updatedUser = userRepository.findByEmail(userAccount.getEmail());
        assertTrue(updatedUser.isPresent());
        assertEquals("J", updatedUser.get().getFirstName());
        assertEquals("Doe", updatedUser.get().getLastName());
    }
}
