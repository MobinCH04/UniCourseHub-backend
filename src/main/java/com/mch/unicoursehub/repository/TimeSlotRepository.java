package com.mch.unicoursehub.repository;

import com.mch.unicoursehub.model.entity.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;


@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, UUID> {
}
