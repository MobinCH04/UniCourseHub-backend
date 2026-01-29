package com.mch.unicoursehub.repository;

import com.mch.unicoursehub.model.entity.CourseOffering;
import com.mch.unicoursehub.model.entity.Course;
import com.mch.unicoursehub.model.entity.Semester;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CourseOfferingRepository extends JpaRepository<CourseOffering, UUID> {

    int countByCourseAndSemester(Course course, Semester semester);

    List<CourseOffering> findBySemester(Semester semester);

    Optional<CourseOffering> findByCourse_CodeAndSection(
            String courseCode,
            int section
    );

    boolean existsByCourseAndSemesterAndIdNot(
            Course course,
            Semester semester,
            UUID id
    );
}