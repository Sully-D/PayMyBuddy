package com.payMyBuddy.payMyBuddy.service;

import com.payMyBuddy.payMyBuddy.exception.SenderAndRecipientNotFriend;
import com.payMyBuddy.payMyBuddy.model.SenderRecipientConnection;
import com.payMyBuddy.payMyBuddy.model.Transaction;
import com.payMyBuddy.payMyBuddy.model.UserAccount;
import com.payMyBuddy.payMyBuddy.repository.TransactionRepository;
import com.payMyBuddy.payMyBuddy.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    SenderRecipientConnectionService senderRecipientConnectionService;

    public void createTransaction(Transaction transaction) {

        Utils.validateUserId(transaction.getSender().getId());
        Utils.validateUserId(transaction.getRecipient().getId());
        Utils.valideAmount(transaction.getAmount());
        Utils.checkArguments(transaction.getDescription(), "description");

        Optional<List<UserAccount>> friend = senderRecipientConnectionService.getConnection(transaction.getSender());
        if (friend.isEmpty()){
            throw new SenderAndRecipientNotFriend(transaction.getSender().getEmail() + " Not friend with : "
                    + transaction.getRecipient().getEmail());
        }

        transactionRepository.save(transaction);
    }
}
