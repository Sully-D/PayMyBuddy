package com.payMyBuddy.payMyBuddy.repository;

import com.payMyBuddy.payMyBuddy.model.SenderRecipientConnection;
import com.payMyBuddy.payMyBuddy.model.UserAccount;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SenderRecipientConnectionRepository extends JpaRepository<SenderRecipientConnection, Long> {
    Optional<SenderRecipientConnection> findBySender(UserAccount user);

    Optional<SenderRecipientConnection> findBySenderAndRecipient(UserAccount sender, UserAccount recipient);

    Optional<List<UserAccount>> findBySenderId(long l);

    @Query("SELECT s.recipient FROM SenderRecipientConnection s WHERE s.sender.id = :senderId")
    List<UserAccount> findRecipientsBySenderId(@Param("senderId") Long senderId);

}
