package com.mch.unicoursehub.service.impl;

import com.mch.unicoursehub.exceptions.BadRequestException;
import com.mch.unicoursehub.exceptions.NotFoundException;
import com.mch.unicoursehub.model.dto.CourseOfferingResponse;
import com.mch.unicoursehub.model.dto.UserListResponse;
import com.mch.unicoursehub.model.entity.CourseOffering;
import com.mch.unicoursehub.model.entity.Enrollment;
import com.mch.unicoursehub.model.entity.User;
import com.mch.unicoursehub.model.enums.EnrollmentStatus;
import com.mch.unicoursehub.repository.CourseOfferingRepository;
import com.mch.unicoursehub.repository.EnrollmentRepository;
import com.mch.unicoursehub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.mch.unicoursehub.ConstErrors.*;

@Service
@RequiredArgsConstructor
public class ProfessorServiceImpl {

    private final CourseOfferingRepository courseOfferingRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final UserServiceImpl userServiceImpl; // to identify logged-in user

    /**
     * Returns course offerings assigned to the current professor.
     */
    @Transactional(readOnly = true)
    public List<CourseOfferingResponse> getMyCourseOfferings() {
        User professor = userServiceImpl.getUserLoggedInRef();

        List<CourseOffering> all = courseOfferingRepository.findAll();

        return all.stream()
                .filter(co -> co.getProfessor() != null && co.getProfessor().getUid().equals(professor.getUid()))
                .map(co -> CourseOfferingResponse.builder()
                        .courseCode(co.getCourse().getCode())
                        .courseName(co.getCourse().getName())
                        .professorName(co.getProfessor().fullName())
                        .capacity(co.getCapacity())
                        .examDate(co.getExamDate())
                        .classroomNumber(Integer.parseInt(co.getClassRoom()))
                        .groupNumber(co.getSection())
                        .timeSlotIds(co.getTimeSlots().stream().map(ts -> ts.getId()).toList())
                        .build())
                .toList();
    }

    /**
     * Returns students enrolled in the given course offering.
     * Only enrollments with status != DROPPED are returned.
     */
    @Transactional(readOnly = true)
    public List<UserListResponse> getStudentsOfCourseOffering(UUID courseOfferingId) {
        User professor = userServiceImpl.getUserLoggedInRef();

        CourseOffering offering = courseOfferingRepository.findById(courseOfferingId)
                .orElseThrow(() -> new NotFoundException("Course offering not found"));

        // ownership check
        if (offering.getProfessor() == null || !offering.getProfessor().getUid().equals(professor.getUid())) {
            throw new NotFoundException("Course offering not found");
        }

        return offering.getEnrollments().stream()
                .filter(e -> e.getStatus() != EnrollmentStatus.DROPPED)
                .map(Enrollment::getStudent)
                .map(User::convertToUserListResponse)
                .toList();
    }

    /**
     * Remove (drop) a student from the given course offering.
     * Implementation keeps semantic of student drop: set status = DROPPED.
     */
    @Transactional
    public void removeStudentFromCourseOffering(UUID courseOfferingId, UUID studentId) {
        User professor = userServiceImpl.getUserLoggedInRef();

        CourseOffering offering = courseOfferingRepository.findById(courseOfferingId)
                .orElseThrow(() -> new NotFoundException("Course offering not found"));

        if (offering.getProfessor() == null || !offering.getProfessor().getUid().equals(professor.getUid())) {
            throw new NotFoundException("Course offering not found");
        }

        // ensure student exists (consistent with other services)
        userRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Student not found"));

        // find enrollment inside offering's enrollments
        Enrollment enrollment = offering.getEnrollments().stream()
                .filter(e -> e.getStudent() != null && e.getStudent().getUid().equals(studentId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException(notFoundEnrollment));

        // only allow dropping when in SELECTED status (same rule as student's drop)
        if (enrollment.getStatus() != EnrollmentStatus.SELECTED) {
            throw new BadRequestException(nonSelectedStatus);
        }

        enrollment.setStatus(EnrollmentStatus.DROPPED);
        enrollmentRepository.save(enrollment);
    }
}
