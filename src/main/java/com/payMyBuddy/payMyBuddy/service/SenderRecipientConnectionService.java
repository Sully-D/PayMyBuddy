package com.payMyBuddy.payMyBuddy.service;

import com.payMyBuddy.payMyBuddy.model.SenderRecipientConnection;
import com.payMyBuddy.payMyBuddy.model.UserAccount;
import com.payMyBuddy.payMyBuddy.repository.SenderRecipientConnectionRepository;
import com.payMyBuddy.payMyBuddy.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import java.util.ArrayList;
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
     * @throws IllegalArgumentException if friend already added.
     */
    @Transactional(rollbackFor = { IllegalArgumentException.class })
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

    /**
     * Checks if a connection already exists between a sender and a recipient.
     *
     * @param sender The sender's user account.
     * @param recipient The recipient's user account.
     * @return {@code true} if a connection exists, {@code false} otherwise.
     */
    private boolean connectionExists(UserAccount sender, UserAccount recipient) {
        return senderRecipientConnectionRepository.findBySenderAndRecipient(sender, recipient).isPresent();
    }

    /**
     * Retrieves a list of recipients connected to the provided user's account.
     * Validates the user ID before attempting to fetch connections from the repository.
     *
     * @param userRelation The user account whose connections are to be retrieved.
     * @return A list of strings describing the connected recipients' details.
     * @throws IllegalArgumentException if the user ID is invalid.
     */
    public List<String> getConnection(UserAccount userRelation) {
        // Validate that the user ID is not null and positive
        Utils.validateUserId(userRelation.getId());

        // Fetch the list of recipient accounts connected to the specified sender ID
        List<UserAccount> users = senderRecipientConnectionRepository.findRecipientsBySenderId(userRelation.getId());

        // Construct a list of formatted strings representing the connected recipients
        List<String> connections = new ArrayList<>();
        for (UserAccount user : users) {
            String userDetails = "email: " + user.getEmail() + ", " + user.getFirstName() + " " + user.getLastName();
            connections.add(userDetails);
        }

        return connections;
    }

}
