package com.payMyBuddy.payMyBuddy.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "sender_recipient_connection")
public class SenderRecipientConnection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_sender", referencedColumnName = "id")
    private UserAccount sender;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_recipient", referencedColumnName = "id")
    private UserAccount recipient;

    public SenderRecipientConnection(UserAccount sender, UserAccount recipient) {
    }
}
