package com.mch.unicoursehub.service.impl;

import com.mch.unicoursehub.exceptions.BadRequestException;
import com.mch.unicoursehub.exceptions.NotFoundException;
import com.mch.unicoursehub.model.dto.CourseOfferingResponse;
import com.mch.unicoursehub.model.dto.DropEnrollmentRequest;
import com.mch.unicoursehub.model.dto.UserListResponse;
import com.mch.unicoursehub.model.entity.CourseOffering;
import com.mch.unicoursehub.model.entity.Enrollment;
import com.mch.unicoursehub.model.entity.User;
import com.mch.unicoursehub.model.enums.EnrollmentStatus;
import com.mch.unicoursehub.repository.CourseOfferingRepository;
import com.mch.unicoursehub.repository.EnrollmentRepository;
import com.mch.unicoursehub.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
     */
    @Transactional(readOnly = true)
    public List<UserListResponse> getStudentsOfOfferingByKeys(String courseCode, int groupNumber, String semesterName) {
        User professor = userServiceImpl.getUserLoggedInRef();

        // find offering by course code and section
        CourseOffering offering = courseOfferingRepository
                .findByCourse_CodeAndSectionAndSemester_Name(
                        courseCode.trim(),
                        groupNumber,
                        semesterName.trim()
                )
                .orElseThrow(() -> new NotFoundException(courseOfferingNotFound));

        // confirm semester matches
        if (!offering.getSemester().getName().equalsIgnoreCase(semesterName.trim())) {
            throw new NotFoundException(courseOfferingNotFound);
        }

        // ownership check
        if (offering.getProfessor() == null || !offering.getProfessor().getUid().equals(professor.getUid())) {
            throw new NotFoundException(courseOfferingNotFound);
        }

        return offering.getEnrollments().stream()
                .filter(e -> e.getStatus() != EnrollmentStatus.DROPPED)
                .map(Enrollment::getStudent)
                .map(User::convertToUserListResponse)
                .toList();
    }

    /**
     * Remove a student
     */
    @Transactional
    public void removeStudentFromOfferingByKeys(DropEnrollmentRequest req) {
        User professor = userServiceImpl.getUserLoggedInRef();

        CourseOffering offering = courseOfferingRepository
                .findByCourse_CodeAndSectionAndSemester_Name(
                        req.courseCode().trim(),
                        req.groupNumber(),
                        req.semesterName().trim()
                )
                .orElseThrow(() -> new NotFoundException(courseOfferingNotFound));


        if (!offering.getSemester().getName().equalsIgnoreCase(req.semesterName().trim())) {
            throw new NotFoundException(courseOfferingNotFound);
        }

        if (offering.getProfessor() == null || !offering.getProfessor().getUid().equals(professor.getUid())) {
            throw new NotFoundException(courseOfferingNotFound);
        }

        // find student by userNumber
        User student = userRepository.findByUserNumber(req.studentUserNumber().trim())
                .orElseThrow(() -> new NotFoundException(userNotFound));

        Enrollment enrollment = enrollmentRepository
                .findByStudentAndCourseOffering_Course_CodeAndCourseOffering_SectionAndCourseOffering_Semester_Name(
                        student,
                        req.courseCode().trim(),
                        req.groupNumber(),
                        req.semesterName().trim()
                )
                .orElseThrow(() -> new NotFoundException(notFoundEnrollment));

        if (enrollment.getStatus() != EnrollmentStatus.SELECTED) {
            throw new BadRequestException(nonSelectedStatus);
        }

        // drop
        enrollment.setStatus(EnrollmentStatus.DROPPED);
        enrollmentRepository.save(enrollment);
    }
}
