package com.payMyBuddy.payMyBuddy.repository;

import com.payMyBuddy.payMyBuddy.model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Repository interface for user account operations.
 * Extends JpaRepository to provide basic CRUD operations.
 */
//@Repository
public interface UserRepository extends JpaRepository<UserAccount, Long> {

    /**
     * Finds a user account by email address.
     * @param email the email address to search for.
     * @return an Optional containing the user account if found, or an empty Optional if not.
     */
    Optional<UserAccount> findByEmail(String email);

    /**
     * Updates the firstName and lastName of an existing UserAccount in the database.
     * This operation requires an active transaction and modifies the database,
     * hence it is annotated with @Transactional and @Modifying.
     *
     * @param id The ID of the UserAccount to update.
     * @param firstName The new first name to be set.
     * @param lastName The new last name to be set.
     */
    @Modifying
    @Transactional
    @Query("UPDATE UserAccount u SET u.firstName = :firstName, u.lastName = :lastName WHERE u.id = :id")
    void updateUser(@Param("id") long id, @Param("firstName") String firstName, @Param("lastName") String lastName);

}
