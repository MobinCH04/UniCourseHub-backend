package com.mch.unicoursehub.repository;

import com.mch.unicoursehub.model.entity.Semester;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SemesterRepository extends JpaRepository<Semester, UUID> {

    boolean existsByName(String name);

    Optional<Semester> findByName(String name);
}
