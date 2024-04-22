package com.payMyBuddy.payMyBuddy.service;

import com.payMyBuddy.payMyBuddy.model.Transaction;
import com.payMyBuddy.payMyBuddy.repository.TransactionRepository;
import com.payMyBuddy.payMyBuddy.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

    @Autowired
    TransactionRepository transactionRepository;

    public void createTransaction(Transaction transaction) {

        Utils.validateUserId(transaction.getSender().getId());
        Utils.validateUserId(transaction.getRecipient().getId());
        Utils.valideAmount(transaction.getAmount());
        Utils.checkArguments(transaction.getDescription(), "description");

        transactionRepository.save(transaction);
    }
}
