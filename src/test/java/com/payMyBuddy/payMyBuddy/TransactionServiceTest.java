package com.payMyBuddy.payMyBuddy;

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

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class TransactionServiceTest {

    @Mock
    UserRepository userRepository;

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

        Optional<List<UserAccount>> friends = Optional.of(Arrays.asList(newUserJane));

        when(senderRecipientConnectionService.getConnection(newUserJohn)).thenReturn(friends);
        when(transactionRepository.save(transaction)).thenReturn(transaction);

        // When
        transactionService.createTransaction(transaction);

        // Then
        verify(transactionRepository).save(transaction);
    }
}
