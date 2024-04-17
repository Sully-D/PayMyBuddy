package com.payMyBuddy.payMyBuddy.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_account")
public class UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "email", nullable = false, unique = true, length = 50)
    @NotNull(message = "email can't null")
    private String email;

    @Column(name = "password", nullable = false, length = 255)
    @NotNull(message = "password can't null")
    private String password;

    @Column(name = "last_name", nullable = false, length = 50)
    @NotNull(message = "lastName can't null")
    private String lastName;

    @Column(name = "first_name", nullable = false, length = 50)
    @NotNull(message = "firstName can't null")
    private String firstName;

    @Column(name = "balance", nullable = false, precision = 10, scale = 2,
            columnDefinition = "DECIMAL(10,2) DEFAULT '0.00'")
    @NotNull(message = "balance can't null")
    private BigDecimal balance;

    @Column(name = "role", nullable = false, length = 50)
    @NotNull(message = "role can't null")
    private String role;
}
