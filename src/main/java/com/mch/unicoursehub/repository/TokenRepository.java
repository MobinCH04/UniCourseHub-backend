package com.mch.unicoursehub.repository;

import com.mch.unicoursehub.model.entity.Token;
import com.mch.unicoursehub.model.entity.User;
import com.mch.unicoursehub.model.enums.TokenType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

    /**
     * Repository interface for managing Token entities.
     * Provides methods to interact with the database for Token-related operations.
     */
    @Repository
    public interface TokenRepository extends JpaRepository<Token, UUID> {

//        /**
//         * Finds all valid tokens for a user that are not expired or revoked.
//         *
//         * @param id the UUID of the user
//         * @return a list of valid tokens
//         */
//        @Query(value = """
//            select t from Token t inner join User u\s
//            on t.user.uid = u.uid\s
//            where u.uid = :id and (t.expired = false and t.revoked = false)\s
//            """)
//        List<Token> findAllValidTokenByUser(UUID id);
        /**
         * Finds all tokens associated with a specific user.
         *
         * @param user the User entity
         * @return a list of Tokens associated with the user
         */
        List<Token> findByUser(User user);

        Optional<Token> findByUuidAndType(UUID uuid, TokenType type);

        Optional<Token> findByUuid(String uuid);

        @Modifying
        int deleteByUuidAndType(UUID uuid, TokenType type);

        @Modifying
        int deleteByUuid(String uuid);

        @Modifying
        @Transactional
        @Query("DELETE FROM Token t WHERE t.tid IN :ids")
        void deleteAllByIds(@Param("ids") List<UUID> ids);
    }
