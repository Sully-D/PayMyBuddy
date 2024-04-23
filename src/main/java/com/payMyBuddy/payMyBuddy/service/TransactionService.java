package com.payMyBuddy.payMyBuddy.service;

import com.payMyBuddy.payMyBuddy.exception.SenderAndRecipientNotFriend;
import com.payMyBuddy.payMyBuddy.model.Transaction;
import com.payMyBuddy.payMyBuddy.model.UserAccount;
import com.payMyBuddy.payMyBuddy.repository.TransactionRepository;
import com.payMyBuddy.payMyBuddy.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    SenderRecipientConnectionService senderRecipientConnectionService;

    @Transactional
    public void createTransaction(Transaction transaction) {

        Utils.validateUserId(transaction.getSender().getId());
        Utils.validateUserId(transaction.getRecipient().getId());
        Utils.valideAmount(transaction.getAmount());
        Utils.checkArguments(transaction.getDescription(), "description");

        List<String> friends = senderRecipientConnectionService.getConnection(transaction.getSender());
        if (friends.isEmpty()){
            throw new SenderAndRecipientNotFriend(transaction.getSender().getEmail() + " Not friend with : "
                    + transaction.getRecipient().getEmail());
        }

        Transaction makeTransaction = Transaction.builder()
                .sender(transaction.getSender())
                .recipient(transaction.getRecipient())
                .amount(transaction.getAmount())
                .date(transaction.getDate())
                .description(transaction.getDescription())
                .build();

        transactionRepository.save(makeTransaction);
    }

    public List<String> getTransaction(UserAccount userAccount) {

        // Validate user IDs before proceeding
        Utils.validateUserId(userAccount.getId());

        Long idUser = userAccount.getId();

        Optional<List<Transaction>> result = transactionRepository.findBySenderId(idUser);

        List<String> transactions = new ArrayList<>();
        result.ifPresent(list -> {
            for (Transaction transaction : list) {
                List<String> lines = new ArrayList<>();

                lines.add("Sender : " + transaction.getSender().getEmail());
                lines.add("Recipient : " + transaction.getRecipient().getEmail());
                lines.add("Amount : " + transaction.getAmount());
                lines.add("Date : " + transaction.getDate());
                lines.add("Description : " + transaction.getDescription());

                String transactionDetails = String.join(", ", lines);

                transactions.add(transactionDetails);
            }
        });

        return transactions;
    }
}
