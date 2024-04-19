package com.payMyBuddy.payMyBuddy;

import com.payMyBuddy.payMyBuddy.model.SenderRecipientConnection;
import com.payMyBuddy.payMyBuddy.model.UserAccount;
import com.payMyBuddy.payMyBuddy.repository.SenderRecipientConnectionRepository;
import com.payMyBuddy.payMyBuddy.repository.UserRepository;
import com.payMyBuddy.payMyBuddy.service.SenderRecipientConnectionService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class SenderRecipientConnectionServiceTest {

    @Mock
    UserRepository userRepository;
    @Mock
    SenderRecipientConnectionRepository senderRecipientConnectionRepository;

    @InjectMocks
    SenderRecipientConnectionService senderRecipientConnectionService;

    @Test
    public void createConnection_Successfully() {

        // Given
        UserAccount newUserJohn = UserAccount.builder()
                .id(100L)
                .email("john.doe@test.com")
                .password("Azerty123!")
                .lastName("Doe")
                .firstName("John")
                .balance(BigDecimal.valueOf(0.00))
                .role("USER")
                .build();

        UserAccount newUserJane = UserAccount.builder()
                .id(101L)
                .email("jane.doe@test.com")
                .password("Azerty123!")
                .lastName("Doe")
                .firstName("Jane")
                .balance(BigDecimal.valueOf(0.00))
                .role("USER")
                .build();

        when(userRepository.findByEmail("jane.doe@test.com")).thenReturn(Optional.ofNullable(newUserJane));
        when(userRepository.findById(newUserJohn.getId())).thenReturn(Optional.of(newUserJohn));
        when(userRepository.findById(newUserJane.getId())).thenReturn(Optional.of(newUserJane));

        // When
        senderRecipientConnectionService.createConnection(newUserJohn, newUserJane);

        // Then
        verify(senderRecipientConnectionRepository).save(any(SenderRecipientConnection.class));
    }
}
