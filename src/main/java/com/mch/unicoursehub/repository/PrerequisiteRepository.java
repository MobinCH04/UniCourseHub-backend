package com.mch.unicoursehub.repository;

import com.mch.unicoursehub.model.entity.Prerequisite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PrerequisiteRepository extends JpaRepository<Prerequisite, UUID> {
}
