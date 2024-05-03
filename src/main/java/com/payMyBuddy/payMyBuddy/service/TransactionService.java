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

    @Transactional
    public void createTransaction(Transaction transaction) {
        Utils.validateUserId(transaction.getSender().getId());
        Utils.validateUserId(transaction.getRecipient().getId());
        Utils.valideAmount(transaction.getAmount());
        Utils.checkArguments(transaction.getDescription(), "description");

        UserAccount sender = transaction.getSender();
        UserAccount recipient = transaction.getRecipient();

        List<String> friends = senderRecipientConnectionService.getConnection(transaction.getSender());
        if (friends.isEmpty()){
            throw new SenderAndRecipientNotFriend(transaction.getSender().getEmail() + " Not friend with : "
                    + transaction.getRecipient().getEmail());
        }

        BigDecimal fee = transaction.getAmount().multiply(new BigDecimal("0.005"));
        BigDecimal totalAmount = transaction.getAmount().add(fee);

        if (sender.getBalance().compareTo(totalAmount) < 0) {
            throw new IllegalArgumentException("Insufficient funds");
        }

        sender.setBalance(sender.getBalance().subtract(totalAmount));
        recipient.setBalance(recipient.getBalance().add(transaction.getAmount()));

        Transaction makeTransaction = Transaction.builder()
                .sender(transaction.getSender())
                .recipient(transaction.getRecipient())
                .amount(totalAmount)
                .date(transaction.getDate())
                .description(transaction.getDescription())
                .build();

        userRepository.save(sender);
        userRepository.save(recipient);
        transactionRepository.save(makeTransaction);
    }


    public Page<Transaction> getTransaction(UserAccount userAccount, int page, int size) {
        Utils.validateUserId(userAccount.getId());
        Pageable pageable = PageRequest.of(page, size);
        return transactionRepository.findBySenderId(userAccount.getId(), pageable);
    }

}
