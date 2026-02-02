package com.mch.unicoursehub.repository;

import com.mch.unicoursehub.model.entity.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository interface for {@link TimeSlot} entity.
 *
 * <p>
 * Provides CRUD operations for managing time slots in the system.
 * Time slots represent specific periods on a day (e.g., SATURDAY 08:00-10:00)
 * that can be assigned to course offerings.
 * </p>
 */
@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, UUID> {
}
