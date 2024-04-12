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

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
public class UserServiceIntegrationTest {

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    @Autowired
    private UserService userService;

    @Test
    public void createUser_EncryptPassword() {
        // Given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(bCryptPasswordEncoder.encode(anyString())).thenAnswer(invocation -> "hashed" + invocation.getArgument(0));

        UserAccount newUser = UserAccount.builder()
                .email("john.doe@test.com")
                .password("Azerty123")
                .lastName("Doe")
                .firstName("John")
                .balance(BigDecimal.valueOf(0.00))
                .build();

        // When
        userService.createUser(newUser);

        // Then
        assertNotEquals("Azerty123", newUser.getPassword(), "Password must be encrypted");
        assertTrue(newUser.getPassword().startsWith("hashed"), "The encrypted password must begin with 'hashed'.");
    }
}
