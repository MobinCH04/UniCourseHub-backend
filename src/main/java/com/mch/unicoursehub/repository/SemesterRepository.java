package com.mch.unicoursehub.repository;

import com.mch.unicoursehub.model.entity.Semester;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for {@link Semester} entity.
 *
 * <p>
 * Provides CRUD operations and custom queries for managing semesters in the system.
 * </p>
 */
public interface SemesterRepository extends JpaRepository<Semester, UUID> {

    /**
     * Checks if a semester with the given name already exists.
     *
     * @param name the name of the semester (e.g., "1404-1")
     * @return true if a semester with the given name exists, false otherwise
     */
    boolean existsByName(String name);

    /**
     * Finds a semester by its name.
     *
     * @param name the name of the semester (e.g., "1404-1")
     * @return an {@link Optional} containing the {@link Semester} if found, or empty if not found
     */
    Optional<Semester> findByName(String name);
}
