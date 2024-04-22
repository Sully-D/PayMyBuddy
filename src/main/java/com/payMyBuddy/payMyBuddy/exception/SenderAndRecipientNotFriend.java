package com.payMyBuddy.payMyBuddy.exception;

public class SenderAndRecipientNotFriend extends RuntimeException {
    public SenderAndRecipientNotFriend (String message) {
        super(message);
    }
}
