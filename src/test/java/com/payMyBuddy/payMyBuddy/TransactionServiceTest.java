package com.payMyBuddy.payMyBuddy;

import com.payMyBuddy.payMyBuddy.exception.SenderAndRecipientNotFriend;
import com.payMyBuddy.payMyBuddy.model.Transaction;
import com.payMyBuddy.payMyBuddy.model.UserAccount;
import com.payMyBuddy.payMyBuddy.repository.TransactionRepository;
import com.payMyBuddy.payMyBuddy.service.SenderRecipientConnectionService;
import com.payMyBuddy.payMyBuddy.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class TransactionServiceTest {

    @Mock
    SenderRecipientConnectionService senderRecipientConnectionService;

    @Mock
    TransactionRepository transactionRepository;

    @InjectMocks
    TransactionService transactionService;

    @Test
    public void createTransaction_Successfully() {
        // Given
        LocalDateTime now = LocalDateTime.now();

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

        Transaction transaction = Transaction.builder()
                .sender(newUserJohn)
                .recipient(newUserJane)
                .amount(BigDecimal.valueOf(25.0))
                .description("Test")
                .date(now)
                .build();

        List<String> friends = new ArrayList<>();
        friends.add("email : " + newUserJane.getEmail() + ", " + newUserJane.getFirstName()
                + " " + newUserJane.getLastName());

        when(senderRecipientConnectionService.getConnection(newUserJohn)).thenReturn(friends);
        when(transactionRepository.save(transaction)).thenReturn(transaction);

        // When
        transactionService.createTransaction(transaction);

        // Then
        verify(transactionRepository).save(transaction);
    }

    @Test
    public void createTransaction_WhenNotFriend() {
        // Given
        LocalDateTime now = LocalDateTime.now();

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

        Transaction transaction = Transaction.builder()
                .sender(newUserJohn)
                .recipient(newUserJane)
                .amount(BigDecimal.valueOf(25.0))
                .description("Test")
                .date(now)
                .build();

        List<String> friendsVoid = new ArrayList<>();

        when(senderRecipientConnectionService.getConnection(newUserJohn)).thenReturn(friendsVoid);

        // When & Then
        assertThrows(SenderAndRecipientNotFriend.class, () -> {
            transactionService.createTransaction(transaction);
        });

        // Ensure that the transaction is not saved
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    public void createTransaction_WhenAmountIsNull() {
        // Given
        LocalDateTime now = LocalDateTime.now();

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

        Transaction transaction = Transaction.builder()
                .sender(newUserJohn)
                .recipient(newUserJane)
                .amount(null)
                .description("Test")
                .date(now)
                .build();

        // When & Then
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionService.createTransaction(transaction);
        });

        assertEquals("Amount cannot be null.", exception.getMessage());

        // Ensure that the transaction is not saved
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    public void createTransaction_WhenAmountIsZero() {
        // Given
        LocalDateTime now = LocalDateTime.now();

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

        Transaction transaction = Transaction.builder()
                .sender(newUserJohn)
                .recipient(newUserJane)
                .amount(BigDecimal.valueOf(0))
                .description("Test")
                .date(now)
                .build();

        // When & Then
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionService.createTransaction(transaction);
        });

        assertEquals("Amount must be greater than zero.", exception.getMessage());

        // Ensure that the transaction is not saved
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    public void getTransaction_Successfully(){
        // Given
        LocalDateTime now = LocalDateTime.now();

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

        Transaction transaction = Transaction.builder()
                .sender(newUserJohn)
                .recipient(newUserJane)
                .amount(BigDecimal.valueOf(25))
                .description("Test")
                .date(now)
                .build();

        Transaction transaction2 = Transaction.builder()
                .sender(newUserJohn)
                .recipient(newUserJane)
                .amount(BigDecimal.valueOf(5))
                .description("Test2")
                .date(now)
                .build();

        List<Transaction> transactions = Arrays.asList(transaction, transaction2);

        when(transactionRepository.findBySenderId(newUserJohn.getId())).thenReturn(Optional.of(transactions));

        // When
        List<String> result = transactionService.getTransaction(newUserJohn);

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(transactions.size(), result.size());
        verify(transactionRepository).findBySenderId(newUserJohn.getId());
    }
}
