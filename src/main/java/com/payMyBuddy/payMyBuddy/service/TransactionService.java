package com.payMyBuddy.payMyBuddy.service;

import com.payMyBuddy.payMyBuddy.exception.SenderAndRecipientNotFriend;
import com.payMyBuddy.payMyBuddy.model.Transaction;
import com.payMyBuddy.payMyBuddy.model.UserAccount;
import com.payMyBuddy.payMyBuddy.repository.TransactionRepository;
import com.payMyBuddy.payMyBuddy.repository.UserRepository;
import com.payMyBuddy.payMyBuddy.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    SenderRecipientConnectionService senderRecipientConnectionService;

    @Autowired
    UserRepository userRepository;

    /**
     * Creates a financial transaction between a sender and a recipient. Ensures that the sender
     * and recipient are friends, checks for sufficient funds, applies a transaction fee, and updates balances.
     *
     * @param transaction The transaction details including sender, recipient, amount, and description.
     * @throws IllegalArgumentException If the sender does not have enough balance, if required fields are null, or if the amount is invalid.
     * @throws SenderAndRecipientNotFriend If the sender and recipient are not connected as friends.
     */
    @Transactional
    public void createTransaction(Transaction transaction) {
        // Validate the sender and recipient user IDs and transaction amount
        Utils.validateUserId(transaction.getSender().getId());
        Utils.validateUserId(transaction.getRecipient().getId());
        Utils.valideAmount(transaction.getAmount());
        Utils.checkArguments(transaction.getDescription(), "description");

        // Retrieve the sender and recipient user accounts
        UserAccount sender = transaction.getSender();
        UserAccount recipient = transaction.getRecipient();

        // Ensure the sender and recipient are friends
        List<String> friends = senderRecipientConnectionService.getConnection(sender);
        if (friends.isEmpty()) {
            throw new SenderAndRecipientNotFriend(sender.getEmail() + " is not friends with: " + recipient.getEmail());
        }

        // Calculate the transaction fee (0.5%) and total amount (fee + transaction amount)
        BigDecimal fee = transaction.getAmount().multiply(new BigDecimal("0.005"));
        BigDecimal totalAmount = transaction.getAmount().add(fee);

        // Check if the sender has sufficient balance for the total transaction amount
        if (sender.getBalance().compareTo(totalAmount) < 0) {
            throw new IllegalArgumentException("Insufficient funds");
        }

        // Deduct the total amount from the sender and add the transaction amount to the recipient
        sender.setBalance(sender.getBalance().subtract(totalAmount));
        recipient.setBalance(recipient.getBalance().add(transaction.getAmount()));

        // Create a new transaction object with all the relevant information
        Transaction makeTransaction = Transaction.builder()
                .sender(sender)
                .recipient(recipient)
                .amount(transaction.getAmount())
                .date(transaction.getDate())
                .description(transaction.getDescription())
                .build();

        // Update the user accounts and save the transaction
        userRepository.save(sender);
        userRepository.save(recipient);
        transactionRepository.save(makeTransaction);
    }

    /**
     * Retrieves a paginated list of transactions made by a specific user account.
     * Ensures that the user ID is valid before attempting to retrieve the transactions.
     *
     * @param userAccount The user account whose transactions need to be retrieved.
     * @param page The page number to retrieve.
     * @param size The number of transactions per page.
     * @return A page of transactions that the specified user account has made.
     * @throws IllegalArgumentException If the user ID is invalid.
     */
    public Page<Transaction> getTransaction(UserAccount userAccount, int page, int size) {
        // Validate the user ID
        Utils.validateUserId(userAccount.getId());

        // Create pagination information
        Pageable pageable = PageRequest.of(page, size);

        // Retrieve the transactions made by the specified user
        return transactionRepository.findBySenderId(userAccount.getId(), pageable);
    }

}
