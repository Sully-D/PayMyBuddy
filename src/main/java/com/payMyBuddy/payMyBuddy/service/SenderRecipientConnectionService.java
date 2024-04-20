package com.payMyBuddy.payMyBuddy.service;

import com.payMyBuddy.payMyBuddy.model.SenderRecipientConnection;
import com.payMyBuddy.payMyBuddy.model.UserAccount;
import com.payMyBuddy.payMyBuddy.repository.SenderRecipientConnectionRepository;
import com.payMyBuddy.payMyBuddy.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service class for managing connections between users.
 */
@Service
public class SenderRecipientConnectionService {

    private static final Logger logger = LoggerFactory.getLogger(SenderRecipientConnectionService.class);

    @Autowired
    SenderRecipientConnectionRepository senderRecipientConnectionRepository;

    /**
     * Creates a connection between two user accounts.
     * Validates the IDs of both users before creating the connection to ensure they are valid.
     *
     * @param sender The user account initiating the connection.
     * @param recipient The user account receiving the connection.
     * @throws IllegalArgumentException if either user ID is invalid (null or less than 1).
     */
    public void createConnection(UserAccount sender, UserAccount recipient) {

        // Validate user IDs before proceeding
        Utils.validateUserId(sender.getId());
        Utils.validateUserId(recipient.getId());

        // Check if the connection already exists to avoid duplicates
        if (!connectionExists(sender, recipient)) {
            // Creating a new connection entity
            SenderRecipientConnection newSenderRecipientConnection = SenderRecipientConnection.builder()
                    .sender(sender)
                    .recipient(recipient)
                    .build();

            // Save the new connection to the repository
            senderRecipientConnectionRepository.save(newSenderRecipientConnection);
        } else {
            throw new IllegalArgumentException("Friend already added.");
        }
    }

    private boolean connectionExists(UserAccount sender, UserAccount recipient) {
        return senderRecipientConnectionRepository.findBySenderAndRecipient(sender, recipient).isPresent();
    }

    public Optional<List<UserAccount>> getConnection(UserAccount userRelation) {
        Long idUser = userRelation.getId();

        return senderRecipientConnectionRepository.findBySenderId(idUser);
    }
}
