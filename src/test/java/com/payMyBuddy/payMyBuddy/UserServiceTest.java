package com.payMyBuddy.payMyBuddy;

import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.payMyBuddy.payMyBuddy.exception.UserAlreadyExistsException;
import com.payMyBuddy.payMyBuddy.model.User;
import com.payMyBuddy.payMyBuddy.repository.UserRepository;
import com.payMyBuddy.payMyBuddy.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;


@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @Test
    public void createNewUser() {
        // Given

        // When
        userService.createUser("john.doe@test.com", "Azerty123", "John", "Doe");

        // Then
        verify(userRepository).save(any(User.class));
    }

    @Test
    public void createUser_whenUserAlreadyExist() {
        // Given
        User existingUser = User.builder()
                .email("john.doe@test.com")
                .password("Azerty123")
                .lastName("Doe")
                .firstName("John")
                .build();

        when(userRepository.findByEmail("john.doe@test.com")).thenReturn(Optional.of(existingUser));

        // When & Then
        assertThrows(UserAlreadyExistsException.class, () -> userService.createUser("john.doe@test.com",
                "Azerty123", "John", "Doe"));
    }

    @Test
    public void createUser_whenInvalidEntry() {
        assertThrows(IllegalArgumentException.class, () -> userService.createUser("",
                "", "", ""));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void createUser_whenInvalidEmailFormat() {
        assertThrows(IllegalArgumentException.class, () -> userService.createUser("john.doe@testcom",
                "Azerty123", "John", "Doe"));

        verify(userRepository, never()).save(any(User.class));
    }
}
