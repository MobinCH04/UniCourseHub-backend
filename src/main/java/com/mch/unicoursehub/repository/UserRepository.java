package com.mch.unicoursehub.repository;

import com.mch.unicoursehub.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByUserNumber(String userNumber);

    /**
     * Finds a User by their national ID.
     *
     * @param nationalCode the national ID of the user
     * @return an Optional containing the User if found, or an empty Optional if no user is found
     */
    Optional<User> findUserByNationalCode(String nationalCode);

    /**
     * Finds a User by their user number, returning a limited set of fields (uid, user number).
     *
     * @param userNumber the user number of the user
     * @return an Optional containing the User if found, or an empty Optional if no user is found
     */
    @Query("SELECT new com.mch.unicoursehub.model.entity.User(u.uid, u.userNumber) " +
            "FROM User u " +
            "WHERE u.userNumber = :userNumber")
    Optional<User> findByUsernameRef(@Param("userNumber") String userNumber);
}
