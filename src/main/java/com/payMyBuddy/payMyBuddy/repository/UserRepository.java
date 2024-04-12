package com.payMyBuddy.payMyBuddy.repository;

import com.payMyBuddy.payMyBuddy.model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for user account operations.
 * Extends JpaRepository to provide basic CRUD operations.
 */
@Repository
public interface UserRepository extends JpaRepository<UserAccount, Long> {

    /**
     * Finds a user account by email address.
     * @param email the email address to search for.
     * @return an Optional containing the user account if found, or an empty Optional if not.
     */
    Optional<UserAccount> findByEmail(String email);
}
