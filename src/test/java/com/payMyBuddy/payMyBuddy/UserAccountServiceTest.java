package com.payMyBuddy.payMyBuddy;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.payMyBuddy.payMyBuddy.exception.UserAlreadyExistsException;
import com.payMyBuddy.payMyBuddy.model.UserAccount;
import com.payMyBuddy.payMyBuddy.repository.UserRepository;
import com.payMyBuddy.payMyBuddy.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.util.Optional;


@SpringBootTest
public class UserAccountServiceTest {

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    // CREATE USER TESTS
    @Test
    public void createNewUser() {
        // Given
        UserAccount newUserAccount = UserAccount.builder()
                .email("john.doe@test.com")
                .password("Azerty123@")
                .lastName("Doe")
                .firstName("John")
                .balance(BigDecimal.valueOf(0.00))
                .build();

        // When
        userService.createUser(newUserAccount);

        // Then
        verify(userRepository).save(any(UserAccount.class));
    }

    @Test
    public void createUser_whenUserAlreadyExist() {
        // Given
        UserAccount existingUserAccount = UserAccount.builder()
                .email("john.doe@test.com")
                .password("Azerty123@")
                .lastName("Doe")
                .firstName("John")
                .balance(BigDecimal.valueOf(0.00))
                .build();

        when(userRepository.findByEmail("john.doe@test.com")).thenReturn(Optional.of(existingUserAccount));

        UserAccount newUserAccount = UserAccount.builder()
                .email("john.doe@test.com")
                .password("Azerty123@")
                .lastName("Doe")
                .firstName("John")
                .balance(BigDecimal.valueOf(0.00))
                .build();

        // When & Then
        assertThrows(UserAlreadyExistsException.class, () -> userService.createUser(newUserAccount));
    }

    @Test
    public void createUser_whenEmptyEntry() {
        UserAccount newUserAccount = UserAccount.builder()
                .email("john.doe@test.com")
                .password("Azerty123@")
                .lastName("")
                .firstName("")
                .balance(BigDecimal.valueOf(0.00))
                .build();

        assertThrows(IllegalArgumentException.class, () -> userService.createUser(newUserAccount));

        verify(userRepository, never()).save(any(UserAccount.class));
    }

    @Test
    public void createUser_whenNullEntry() {
        UserAccount newUserAccount = UserAccount.builder()
                .email("john.doe@test.com")
                .password("Azerty123@")
                .lastName(null)
                .firstName("John")
                .balance(BigDecimal.valueOf(0.00))
                .build();
        assertThrows(IllegalArgumentException.class, () -> userService.createUser(newUserAccount));

        verify(userRepository, never()).save(any(UserAccount.class));
    }

    @Test
    public void createUser_whenInvalidEmailFormat() {
        UserAccount newUserAccount = UserAccount.builder()
                .email("john.doe@testcom")
                .password("Azerty123@")
                .lastName("Doe")
                .firstName("John")
                .balance(BigDecimal.valueOf(0.00))
                .build();

        assertThrows(IllegalArgumentException.class, () -> userService.createUser(newUserAccount));

        verify(userRepository, never()).save(any(UserAccount.class));
    }

    @Test
    public void createUser_whenInvalidPasswordFormat() {
        UserAccount newUserAccount = UserAccount.builder()
                .email("john.doe@test.com")
                .password("Azerty")
                .lastName("Doe")
                .firstName("John")
                .balance(BigDecimal.valueOf(0.00))
                .build();

        assertThrows(IllegalArgumentException.class, () -> userService.createUser(newUserAccount));

        verify(userRepository, never()).save(any(UserAccount.class));
    }

    // EDIT PROFILE TEST
    @Test
    public void testEditProfileUpdatesUserSuccessfully() {
        // Given
        long userId = 1L;
        String newFirstName = "Doe";
        String newLastName = "Jane";

        // When
        userService.editProfile(userId, newLastName, newFirstName);

        // Then
        verify(userRepository).updateUser(userId, newLastName, newFirstName);
    }

}