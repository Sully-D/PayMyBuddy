package com.payMyBuddy.payMyBuddy;

import com.payMyBuddy.payMyBuddy.exception.SenderAndRecipientNotFriend;
import com.payMyBuddy.payMyBuddy.model.Transaction;
import com.payMyBuddy.payMyBuddy.model.UserAccount;
import com.payMyBuddy.payMyBuddy.repository.TransactionRepository;
import com.payMyBuddy.payMyBuddy.repository.UserRepository;
import com.payMyBuddy.payMyBuddy.service.SenderRecipientConnectionService;
import com.payMyBuddy.payMyBuddy.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
public class TransactionServiceTest {

    @Mock
    SenderRecipientConnectionService senderRecipientConnectionService;

    @Mock
    TransactionRepository transactionRepository;

    @Mock
    UserRepository userRepository;

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
                .balance(BigDecimal.valueOf(100.00))
                .role("USER")
                .build();

        UserAccount newUserJane = UserAccount.builder()
                .id(101L)
                .email("jane.doe@test.com")
                .password("Azerty123!")
                .lastName("Doe")
                .firstName("Jane")
                .balance(BigDecimal.valueOf(50.00))
                .role("USER")
                .build();

        Transaction transaction = Transaction.builder()
                .sender(newUserJohn)
                .recipient(newUserJane)
                .amount(BigDecimal.valueOf(20))
                .description("Test")
                .date(now)
                .build();

        when(senderRecipientConnectionService.getConnection(newUserJohn)).thenReturn(Collections.singletonList("jane.doe@example.com"));
        when(userRepository.save(any(UserAccount.class))).then(returnsFirstArg());
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        // When
        transactionService.createTransaction(transaction);

        // Then
        assertEquals("79.900", String.valueOf(newUserJohn.getBalance()));
        assertEquals(BigDecimal.valueOf(70.0), newUserJane.getBalance());
        verify(transactionRepository).save(any(Transaction.class));
        verify(userRepository, times(2)).save(any(UserAccount.class));
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
    public void createTransaction_WhenFundsIsInsiffufficient() {
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
                .balance(BigDecimal.valueOf(50.00))
                .role("USER")
                .build();

        Transaction transaction = Transaction.builder()
                .sender(newUserJohn)
                .recipient(newUserJane)
                .amount(BigDecimal.valueOf(20))
                .description("Test")
                .date(now)
                .build();

        when(senderRecipientConnectionService.getConnection(newUserJohn)).thenReturn(Collections.singletonList("jane.doe@example.com"));
        when(userRepository.save(any(UserAccount.class))).then(returnsFirstArg());
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        // When & Then
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionService.createTransaction(transaction);
        });

        // Then
        assertEquals("Insufficient funds", exception.getMessage());
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    public void getTransaction_Successfully(){
        // Given
        LocalDateTime now = LocalDateTime.now();
        Pageable pageable = PageRequest.of(0, 4);

        UserAccount newUserJohn = UserAccount.builder()
                .id(100L)
                .email("john.doe@test.com")
                .password("Azerty123!")
                .lastName("Doe")
                .firstName("John")
                .balance(BigDecimal.valueOf(0.00))
                .role("USER")
                .build();

        Page<Transaction> expectedPage = mock(Page.class);

        when(transactionRepository.findBySenderId(newUserJohn.getId(), pageable)).thenReturn(expectedPage);

        // When
        Page<Transaction> result = transactionService.getTransaction(newUserJohn, 0, 4);

        // Then
        assertNotNull(result);
        assertEquals(expectedPage, result);
        verify(transactionRepository).findBySenderId(newUserJohn.getId(), pageable);
    }
}
