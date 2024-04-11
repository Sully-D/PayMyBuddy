package com.payMyBuddy.payMyBuddy;

import static org.junit.jupiter.api.Assertions.*;

import com.payMyBuddy.payMyBuddy.model.UserAccount;
import com.payMyBuddy.payMyBuddy.repository.UserRepository;
import com.payMyBuddy.payMyBuddy.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;
import java.util.Optional;


@DataJpaTest
public class UserRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;


    @Test
    public void whenFindByEmail_thenReturnUser() {
        // Given
        UserAccount newUser = UserAccount.builder()
                .email("john.doe@testcom")
                .password("Azerty123")
                .lastName("Doe")
                .firstName("John")
                .balance(BigDecimal.valueOf(0.00))
                .build();

        entityManager.persist(newUser);
        entityManager.flush();

        // When
        Optional<UserAccount> found = userRepository.findByEmail(newUser.getEmail());

        // Then
        assertTrue(found.isPresent());
        System.out.println(found);
    }

//    @Test
//    public void whenCreateUser_thenPasswordIsEncrypted () {
//        UserAccount user = UserAccount.builder()
//                .email("john.doe@test.com")
//                .password("Azerty123")
//                .lastName("Doe")
//                .firstName("John")
//                .balance(BigDecimal.valueOf(0.00))
//                .build();
//
//        userService.createUser(user);
//
//        Optional<UserAccount> foundUser = userRepository.findByEmail("john.doe@test.com");
//        assertTrue(foundUser.isPresent());
//        assertNotEquals("Azerty123", foundUser.get().getPassword());
//        assertTrue(bCryptPasswordEncoder.matches("Azerty123", foundUser.get().getPassword()));
//    }
}
