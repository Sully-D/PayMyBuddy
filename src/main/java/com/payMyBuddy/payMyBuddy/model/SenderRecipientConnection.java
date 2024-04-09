package com.payMyBuddy.payMyBuddy.model;

import jakarta.persistence.*;

@Entity
@Table(name = "SenderRecipientConnection")
public class SenderRecipientConnection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "idSender", referencedColumnName = "id")
    private User sender;

    @ManyToOne(optional = false)
    @JoinColumn(name = "idRecipient", referencedColumnName = "id")
    private User recipient;
}
