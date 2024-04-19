package com.payMyBuddy.payMyBuddy.repository;

import com.payMyBuddy.payMyBuddy.model.SenderRecipientConnection;
import com.payMyBuddy.payMyBuddy.model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SenderRecipientConnectionRepository extends JpaRepository<SenderRecipientConnection, Long> {
    Optional<SenderRecipientConnection> findBySender(UserAccount user);
}
