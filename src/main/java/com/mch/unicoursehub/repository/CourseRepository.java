package com.mch.unicoursehub.repository;

import com.mch.unicoursehub.model.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CourseRepository extends JpaRepository<Course, UUID> {

    Optional<Course> findByCode(String code);

    boolean existsByCode(String code);

    List<Course> findByCodeIn(List<String> codes);
}
