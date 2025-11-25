package com.mch.unicoursehub.repository;

import com.mch.unicoursehub.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByUserNumber(String userNumber);
}
