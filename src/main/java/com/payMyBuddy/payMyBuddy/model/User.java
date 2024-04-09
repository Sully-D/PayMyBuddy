package com.payMyBuddy.payMyBuddy.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@Entity
@Table(name = "User")
public class User {

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

    @Column(name = "lastName", nullable = false, length = 50)
    @NotNull(message = "lastName can't null")
    private String lastName;

    @Column(name = "firstName", nullable = false, length = 50)
    @NotNull(message = "firstName can't null")
    private String firstName;

    @Column(name = "balance", nullable = false, precision = 10, scale = 2)
    @NotNull(message = "balance can't null")
    //@DecimalMin("0.00")
    private BigDecimal balance;
}
