package com.payMyBuddy.payMyBuddy.service;

import com.payMyBuddy.payMyBuddy.model.SenderRecipientConnection;
import com.payMyBuddy.payMyBuddy.model.UserAccount;
import com.payMyBuddy.payMyBuddy.repository.SenderRecipientConnectionRepository;
import com.payMyBuddy.payMyBuddy.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SenderRecipientConnectionService {

    private static final Logger logger = LoggerFactory.getLogger(SenderRecipientConnectionService.class);

    @Autowired
    SenderRecipientConnectionRepository senderRecipientConnectionRepository;

    public void createConnection(UserAccount sender, UserAccount recipient) {

        Utils.validateUserId(sender.getId());
        Utils.validateUserId(recipient.getId());

        SenderRecipientConnection newSenderRecipientConnection = SenderRecipientConnection.builder()
                .sender(sender)
                .recipient(recipient)
                .build();

        senderRecipientConnectionRepository.save(newSenderRecipientConnection);
    }
}
