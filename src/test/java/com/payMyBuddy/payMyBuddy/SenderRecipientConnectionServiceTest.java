package com.payMyBuddy.payMyBuddy;


import com.payMyBuddy.payMyBuddy.model.SenderRecipientConnection;
import com.payMyBuddy.payMyBuddy.model.UserAccount;
import com.payMyBuddy.payMyBuddy.repository.SenderRecipientConnectionRepository;
import com.payMyBuddy.payMyBuddy.repository.UserRepository;
import com.payMyBuddy.payMyBuddy.service.SenderRecipientConnectionService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

    @Test
    public void createConnection_WhenIdNull(){

        // Given
        UserAccount newUserJohn = UserAccount.builder()
                .id(null)
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
        assertThrows(IllegalArgumentException.class, () ->
                senderRecipientConnectionService.createConnection(newUserJohn, newUserJane));

        // Then
        verify(senderRecipientConnectionRepository, never()).save(any(SenderRecipientConnection.class));
    }

    @Test
    public void createConnection_WhenIdNegatif(){

        // Given
        UserAccount newUserJohn = UserAccount.builder()
                .id(-1L)
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
        assertThrows(IllegalArgumentException.class, () ->
                senderRecipientConnectionService.createConnection(newUserJohn, newUserJane));

        // Then
        verify(senderRecipientConnectionRepository, never()).save(any(SenderRecipientConnection.class));
    }

    @Test
    public void createConnection_WhenFriendAlreadyAdded() {

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

        // Simulate that the connection already exists
        when(senderRecipientConnectionRepository.findBySenderAndRecipient(newUserJohn, newUserJane))
                .thenReturn(Optional.of(new SenderRecipientConnection()));

        // When & Then
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            senderRecipientConnectionService.createConnection(newUserJohn, newUserJane);
        });
        assertEquals("Friend already added.", exception.getMessage());

        // Ensure no connection is saved
        verify(senderRecipientConnectionRepository, never()).save(any(SenderRecipientConnection.class));
    }

    @Test
    public void getConnection() {

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

        UserAccount newUserJean = UserAccount.builder()
                .id(102L)
                .email("jean.doe@test.com")
                .password("Azerty123!")
                .lastName("Doe")
                .firstName("Jean")
                .balance(BigDecimal.valueOf(0.00))
                .role("USER")
                .build();

        List<UserAccount> friends = Arrays.asList(newUserJane, newUserJean);

        Optional<List<UserAccount>> johnConnections = Optional.of(friends);

        when(userRepository.findById(100L)).thenReturn(Optional.of(newUserJohn));
        when(senderRecipientConnectionRepository.findBySenderId(100L)).thenReturn(johnConnections);
        when(senderRecipientConnectionRepository.save(any(SenderRecipientConnection.class))).thenReturn(null);

        // When
        senderRecipientConnectionService.createConnection(newUserJohn, newUserJane);
        senderRecipientConnectionService.createConnection(newUserJohn, newUserJean);
        Optional<List<UserAccount>> johnFriends = senderRecipientConnectionService.getConnection(newUserJohn);

        // Then
        assertNotNull(johnFriends);
        assertTrue(johnFriends.isPresent());
        assertEquals(2, johnFriends.get().size());
        assertTrue(johnFriends.get().contains(newUserJane));
        assertTrue(johnFriends.get().contains(newUserJean));
    }
}
