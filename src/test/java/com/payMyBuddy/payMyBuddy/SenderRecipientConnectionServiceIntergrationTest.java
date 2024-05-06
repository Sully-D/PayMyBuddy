package com.payMyBuddy.payMyBuddy;

import com.payMyBuddy.payMyBuddy.model.SenderRecipientConnection;
import com.payMyBuddy.payMyBuddy.model.UserAccount;
import com.payMyBuddy.payMyBuddy.repository.SenderRecipientConnectionRepository;
import com.payMyBuddy.payMyBuddy.repository.UserRepository;
import com.payMyBuddy.payMyBuddy.service.SenderRecipientConnectionService;
import com.payMyBuddy.payMyBuddy.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
public class SenderRecipientConnectionServiceIntergrationTest {

    @Autowired
    SenderRecipientConnectionService senderRecipientConnectionService;

    @Autowired
    SenderRecipientConnectionRepository senderRecipientConnectionRepository;

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    public void setUp() {
//        userRepository.deleteAll();
//        userRepository.flush();
//        senderRecipientConnectionRepository.deleteAll();
//        senderRecipientConnectionRepository.flush();

//        UserAccount userJohn = UserAccount.builder()
//                .id(100L)
//                .email("john.doe@test.com")
//                .password("Azerty123!")
//                .lastName("Doe")
//                .firstName("John")
//                .balance(BigDecimal.valueOf(0.00))
//                .role("USER")
//                .build();
//
//        UserAccount userJean = UserAccount.builder()
//                .id(101L)
//                .email("jean.doe@test.com")
//                .password("Azerty123!")
//                .lastName("Doe")
//                .firstName("Jean")
//                .balance(BigDecimal.valueOf(0.00))
//                .role("USER")
//                .build();
//
//        userService.createUser(userJohn);
//        userService.createUser(userJean);
    }

    @Test
    public void createConnection(){
        UserAccount userJohn = UserAccount.builder()
                .email("johnnn.doe@test.com")
                .password("Azerty123!")
                .lastName("Doe")
                .firstName("John")
                .balance(BigDecimal.valueOf(0.00))
                .role("USER")
                .build();

        UserAccount userJane = UserAccount.builder()
                .email("janene.doe@test.com")
                .password("Azerty123!")
                .lastName("Doe")
                .firstName("Jane")
                .balance(BigDecimal.valueOf(0.00))
                .role("USER")
                .build();

        userRepository.save(userJohn);
        userRepository.save(userJane);

        senderRecipientConnectionService.createConnection(userJohn, userJane);

        Optional<SenderRecipientConnection> senderRecipientConnectionSaved =
                senderRecipientConnectionRepository.findBySender(userJohn);
        assertTrue(senderRecipientConnectionSaved.isPresent());
    }
}
