package com.mch.unicoursehub.service.impl;

import com.mch.unicoursehub.exceptions.BadRequestException;
import com.mch.unicoursehub.exceptions.NotFoundException;
import com.mch.unicoursehub.model.dto.CourseOfferingResponse;
import com.mch.unicoursehub.model.dto.DropEnrollmentRequest;
import com.mch.unicoursehub.model.dto.UserListResponse;
import com.mch.unicoursehub.model.entity.*;
import com.mch.unicoursehub.model.enums.EnrollmentStatus;
import com.mch.unicoursehub.repository.CourseOfferingRepository;
import com.mch.unicoursehub.repository.EnrollmentRepository;
import com.mch.unicoursehub.repository.SemesterRepository;
import com.mch.unicoursehub.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfessorServiceImplTest {

    @Mock
    CourseOfferingRepository courseOfferingRepository;

    @Mock
    EnrollmentRepository enrollmentRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    SemesterRepository semesterRepository;

    @Mock
    UserServiceImpl userServiceImpl;

    @InjectMocks
    ProfessorServiceImpl professorService;

    User professor;
    Semester semester;
    Course course;
    CourseOffering offering;

    @BeforeEach
    void setup() {
        professor = User.builder()
                .uid(UUID.randomUUID())
                .firstName("Ali")
                .lastName("Ahmadi")
                .build();

        semester = Semester.builder()
                .name("1403-1")
                .build();

        course = Course.builder()
                .code("CS101")
                .name("Intro")
                .build();

        offering = CourseOffering.builder()
                .course(course)
                .professor(professor)
                .semester(semester)
                .capacity(30)
                .section(1)
                .classRoom("101")
                .examDate(LocalDateTime.now())
                .timeSlots(List.of())
                .build();
    }

    // ===================== getMyCourseOfferings =====================

    @Test
    void getMyCourseOfferings_shouldReturnOnlyProfessorOfferings() {

        // ===== Arrange =====

        String semesterName = "1403-1";

        // professor لاگین‌شده
        when(userServiceImpl.getUserLoggedInRef())
                .thenReturn(professor);

        // semester
        Semester semester = new Semester();
        semester.setName(semesterName);

        when(semesterRepository.findByName(semesterName))
                .thenReturn(Optional.of(semester));

        // course offering متعلق به همین استاد
        when(courseOfferingRepository.findBySemester(semester))
                .thenReturn(List.of(offering));

        // ===== Act =====
        List<CourseOfferingResponse> result =
                professorService.getMyCourseOfferings(semesterName);

        // ===== Assert =====
        assertThat(result).hasSize(1);

        CourseOfferingResponse response = result.get(0);
        assertThat(response.courseCode()).isEqualTo("CS101");
        assertThat(response.professorName())
                .isEqualTo(professor.getFirstName() + " " + professor.getLastName());
    }


    // ===================== getStudentsOfOfferingByKeys =====================

    @Test
    void getStudentsOfOfferingByKeys_shouldReturnStudents() {

        User student = User.builder()
                .lastName("Zarei")
                .build();

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setStatus(EnrollmentStatus.SELECTED);

        offering.setEnrollments(List.of(enrollment));

        when(userServiceImpl.getUserLoggedInRef()).thenReturn(professor);
        when(courseOfferingRepository
                .findByCourse_CodeAndSectionAndSemester_Name("CS101", 1, "1403-1"))
                .thenReturn(Optional.of(offering));

        List<UserListResponse> result =
                professorService.getStudentsOfOfferingByKeys("CS101", 1, "1403-1");

        assertThat(result).hasSize(1);
    }

    @Test
    void getStudentsOfOfferingByKeys_whenNotOwner_shouldThrow() {

        User otherProfessor = User.builder()
                .uid(UUID.randomUUID())
                .build();

        offering.setProfessor(otherProfessor);

        when(userServiceImpl.getUserLoggedInRef()).thenReturn(professor);
        when(courseOfferingRepository
                .findByCourse_CodeAndSectionAndSemester_Name(any(), anyInt(), any()))
                .thenReturn(Optional.of(offering));

        assertThatThrownBy(() ->
                professorService.getStudentsOfOfferingByKeys("CS101", 1, "1403-1"))
                .isInstanceOf(NotFoundException.class);
    }

    // ===================== removeStudentFromOffering =====================

    @Test
    void removeStudentFromOffering_shouldDropEnrollment() {

        User student = User.builder()
                .userNumber("99123")
                .build();

        Enrollment enrollment = new Enrollment();
        enrollment.setStatus(EnrollmentStatus.SELECTED);

        DropEnrollmentRequest req =
                new DropEnrollmentRequest("CS101", 1, "99123");

        when(userServiceImpl.getUserLoggedInRef()).thenReturn(professor);
        when(semesterRepository.findByName("1403-1"))
                .thenReturn(Optional.of(semester));
        when(courseOfferingRepository.findBySemester(semester))
                .thenReturn(List.of(offering));
        when(userRepository.findByUserNumber("99123"))
                .thenReturn(Optional.of(student));
        when(enrollmentRepository.findByStudentAndCourseOffering(student, offering))
                .thenReturn(Optional.of(enrollment));

        professorService.removeStudentFromOffering("1403-1", req);

        assertThat(enrollment.getStatus())
                .isEqualTo(EnrollmentStatus.DROPPED);
        verify(enrollmentRepository).save(enrollment);
    }

    @Test
    void removeStudentFromOffering_whenNotSelected_shouldThrow() {

        Enrollment enrollment = new Enrollment();
        enrollment.setStatus(EnrollmentStatus.PASSED);

        when(userServiceImpl.getUserLoggedInRef()).thenReturn(professor);
        when(semesterRepository.findByName(any()))
                .thenReturn(Optional.of(semester));
        when(courseOfferingRepository.findBySemester(semester))
                .thenReturn(List.of(offering));
        when(userRepository.findByUserNumber(any()))
                .thenReturn(Optional.of(new User()));
        when(enrollmentRepository.findByStudentAndCourseOffering(any(), any()))
                .thenReturn(Optional.of(enrollment));

        DropEnrollmentRequest req =
                new DropEnrollmentRequest("CS101", 1, "99123");

        assertThatThrownBy(() ->
                professorService.removeStudentFromOffering("1403-1", req))
                .isInstanceOf(BadRequestException.class);
    }
}
