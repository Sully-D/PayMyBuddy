package com.payMyBuddy.payMyBuddy;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.payMyBuddy.payMyBuddy.config.CustomUserDetailsService;
import com.payMyBuddy.payMyBuddy.model.UserAccount;
import com.payMyBuddy.payMyBuddy.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

@SpringBootTest
@ActiveProfiles("test")
public class UserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    @Test
    public void loadUserByUsername_userFound_returnsUserDetails() {
        // Given
        String email = "john.doe@test.com";
        UserAccount userAccount = UserAccount.builder()
                .email(email)
                .password("Azerty123!")
                .role("USER")
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(userAccount));

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        // Then
        assertNotNull(userDetails);
        assertEquals(email, userDetails.getUsername());
        assertEquals("Azerty123!", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }

    @Test
    public void loadUserByUsername_userNotFound_throwsUsernameNotFoundException() {
        // Given
        String email = "john.doe@test.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername(email);
        });
    }

    @Test
    public void loadUserByUsername_userFound_returnsCorrectAuthorities() {
        // Given
        String email = "admin@test.com";
        UserAccount userAccount = UserAccount.builder()
                .email(email)
                .password("Azerty123!")
                .role("ADMIN")
                .build();
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(userAccount));

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        // Then
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }
}
