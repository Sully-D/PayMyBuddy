package com.payMyBuddy.payMyBuddy;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.payMyBuddy.payMyBuddy.exception.UserAlreadyExistsException;
import com.payMyBuddy.payMyBuddy.model.UserAccount;
import com.payMyBuddy.payMyBuddy.repository.UserRepository;
import com.payMyBuddy.payMyBuddy.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Optional;


@SpringBootTest
@ActiveProfiles("test")
public class UserAccountServiceTest {

    @MockBean
    private UserRepository userRepository;

    @Mock
    BCryptPasswordEncoder bCryptPasswordEncoder;

//    @Mock
//    Authentication authentication;
//
//    @Mock
//    SecurityContext securityContext;

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
                .role("USER")
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
                .role("USER")
                .build();

        when(userRepository.findByEmail("john.doe@test.com")).thenReturn(Optional.of(existingUserAccount));

        UserAccount newUserAccount = UserAccount.builder()
                .email("john.doe@test.com")
                .password("Azerty123@")
                .lastName("Doe")
                .firstName("John")
                .balance(BigDecimal.valueOf(0.00))
                .role("USER")
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
                .role("USER")
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
                .role("USER")
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
                .role("USER")
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
                .role("USER")
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
        userService.editProfile(userId, newFirstName, newLastName);

        // Then
        verify(userRepository).updateUser(userId, newFirstName, newLastName);
    }

    @Test
    public void testEditProfileInvalidFormat() {
        // Given
        long userId = 1L;
        String newFirstName = "Doe";
        String newLastName = "";

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> userService.editProfile(userId,
                newFirstName, newLastName));
        verify(userRepository, never()).updateUser(userId, newFirstName, newLastName);
    }

    @Test
    public void testEditProfileInvalidId() {
        // Given
        long userId = -1L;
        String newFirstName = "Doe";
        String newLastName = "";

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> userService.editProfile(userId,
                newFirstName, newLastName));
        verify(userRepository, never()).updateUser(userId, newFirstName, newLastName);
    }

    @Test
    public void getCurrentUser_WhenAuthenticated_ReturnsUser() {
        try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
            // Setup the SecurityContext and Authentication objects
            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);

            // Configure mocks
            when(SecurityContextHolder.getContext()).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.isAuthenticated()).thenReturn(true);
            when(authentication.getName()).thenReturn("user@example.com");

            UserAccount expectedUser = new UserAccount();
            when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(expectedUser));

            // Execute the method under test
            Optional<UserAccount> result = userService.getCurrentUser();

            // Assertions
            assertTrue(result.isPresent());
            assertEquals(expectedUser, result.get());
        } // The static mock is automatically released here
    }

    @Test
    public void getCurrentUser_WhenNotAuthenticated_ThrowsException() {
        try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
            SecurityContext securityContext = mock(SecurityContext.class);

            when(SecurityContextHolder.getContext()).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(null);

            // Execute and assert the expected exception
            assertThrows(IllegalStateException.class, () -> userService.getCurrentUser());
        }
    }
}