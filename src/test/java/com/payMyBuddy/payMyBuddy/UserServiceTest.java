package com.payMyBuddy.payMyBuddy;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import com.payMyBuddy.payMyBuddy.model.User;
import com.payMyBuddy.payMyBuddy.repository.UserRepository;
import com.payMyBuddy.payMyBuddy.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @Test
    public void createNewUser() {
        // Given
        User user1 = User.builder()
                .email("john.doe@test.com")
                .password("Azerty123")
                .lastName("John")
                .firstName("Doe")
                .balance(BigDecimal.valueOf(100.00))
                .build();

        // When
        userService.createUser(user1);

        // Then
        verify(userRepository).save(any(User.class));
    }
}
