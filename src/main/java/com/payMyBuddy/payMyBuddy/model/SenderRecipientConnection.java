package com.payMyBuddy.payMyBuddy.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "SenderRecipientConnection")
public class SenderRecipientConnection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "idSender", referencedColumnName = "id")
    private UserAccount sender;

    @ManyToOne(optional = false)
    @JoinColumn(name = "idRecipient", referencedColumnName = "id")
    private UserAccount recipient;
}
