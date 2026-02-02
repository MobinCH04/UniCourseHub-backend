package com.mch.unicoursehub.service.impl;

import com.mch.unicoursehub.exceptions.BadRequestException;
import com.mch.unicoursehub.exceptions.NotFoundException;
import com.mch.unicoursehub.model.dto.DropCourseRequest;
import com.mch.unicoursehub.model.dto.EnrollCourseRequest;
import com.mch.unicoursehub.model.entity.*;
import com.mch.unicoursehub.model.enums.EnrollmentStatus;
import com.mch.unicoursehub.model.enums.Role;
import com.mch.unicoursehub.repository.CourseOfferingRepository;
import com.mch.unicoursehub.repository.EnrollmentRepository;
import com.mch.unicoursehub.repository.PrerequisiteRepository;
import com.mch.unicoursehub.repository.SemesterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.mch.unicoursehub.ConstErrors.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnrollmentServiceImplTest {

    @InjectMocks
    private EnrollmentServiceImpl enrollmentService;

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private CourseOfferingRepository courseOfferingRepository;

    @Mock
    private PrerequisiteRepository prerequisiteRepository;

    @Mock
    private SemesterRepository semesterRepository;

    private User student;
    private CourseOffering offering;
    private Semester semester;
    private Course course;

    @BeforeEach
    void setup() {
        student = User.builder()
                .uid(UUID.randomUUID())
                .firstName("Ali")
                .lastName("Ahmadi")
                .userNumber("40123456")
                .role(Role.STUDENT)
                .build();

        semester = new Semester();
        semester.setName("1403-1");
        semester.setMaxUnits(20);

        course = new Course();
        course.setCode("AP");
        course.setName("Advanced Programming");
        course.setUnit(3);

        offering = new CourseOffering();
        offering.setCourse(course);
        offering.setSemester(semester);
        offering.setCapacity(30);
        offering.setExamDate(LocalDate.of(2025, 1, 10).atStartOfDay());
        offering.setTimeSlots(List.of());
    }


    // ---------------- enrollStudent ----------------

    @Test
    void enrollStudent_success() {
        EnrollCourseRequest req =
                new EnrollCourseRequest("AP", 1);

        when(courseOfferingRepository
                .findByCourse_CodeAndSectionAndSemester_Name("AP", 1, "1403-1"))
                .thenReturn(Optional.of(offering));

        when(enrollmentRepository.countByCourseOffering(offering))
                .thenReturn(10L);

        when(enrollmentRepository
                .existsByStudentAndCourseOffering_SemesterAndCourseOffering_CourseAndStatus(
                        any(), any(), any(), any()))
                .thenReturn(false);

        when(enrollmentRepository
                .existsByStudentAndCourseOffering_SemesterAndCourseOffering_Course(
                        any(), any(), any()))
                .thenReturn(false);

        when(prerequisiteRepository.findByCourse(course))
                .thenReturn(List.of());

        when(enrollmentRepository
                .findByStudentAndCourseOffering_SemesterAndStatus(
                        student, semester, EnrollmentStatus.SELECTED))
                .thenReturn(List.of());

        enrollmentService.enrollStudent(student, "1403-1", req);

        verify(enrollmentRepository).save(any(Enrollment.class));
    }

    @Test
    void enrollStudent_fullCapacity_shouldThrow() {

        // ===== Student =====
        User student = new User();
        student.setUid(UUID.randomUUID());
        student.setUserNumber("4031234567");

        // ===== Course =====
        Course course = new Course();
        course.setCode("AP");
        course.setUnit(3);

        // ===== Semester =====
        Semester semester = new Semester();
        semester.setName("1403-1");
        semester.setMaxUnits(20);

        // ===== Course Offering =====
        CourseOffering offering = new CourseOffering();
        offering.setCourse(course);
        offering.setSemester(semester);
        offering.setCapacity(30);
        offering.setSection(1);
        offering.setTimeSlots(List.of()); // مهم: null نباشه

        // ===== Request =====
        EnrollCourseRequest req =
                new EnrollCourseRequest("AP", 1);

        // ===== Mocks =====
        when(courseOfferingRepository
                .findByCourse_CodeAndSectionAndSemester_Name("AP", 1, "1403-1"))
                .thenReturn(Optional.of(offering));

        when(enrollmentRepository.countByCourseOffering(offering))
                .thenReturn(30L); // ظرفیت پر

        // ===== Act + Assert =====
        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> enrollmentService.enrollStudent(student, "1403-1", req)
        );

        assertEquals(fullCapacity.getMessage(), ex.getMessage());
    }


    // ---------------- getStudentEnrollments ----------------

    @Test
    void getStudentEnrollments_success() {

        User professor = User.builder()
                .uid(UUID.randomUUID())
                .firstName("Dr")
                .lastName("Smith")
                .role(Role.PROFESSOR)
                .build();

        offering.setProfessor(professor);

        Enrollment enrollment = new Enrollment();
        enrollment.setStatus(EnrollmentStatus.SELECTED);
        enrollment.setCourseOffering(offering);

        when(semesterRepository.findByName("1403-1"))
                .thenReturn(Optional.of(semester));

        when(enrollmentRepository
                .findByStudentAndCourseOffering_Semester(student, semester))
                .thenReturn(List.of(enrollment));

        var result =
                enrollmentService.getStudentEnrollments(student, "1403-1");

        assertEquals(1, result.size());
        assertEquals("AP", result.get(0).courseCode());
        assertEquals("Dr Smith", result.get(0).professorName());
    }


    // ---------------- dropCourse ----------------

    @Test
    void dropCourse_success() {
        DropCourseRequest req =
                new DropCourseRequest("AP", 1, "1403-1");

        Enrollment enrollment = new Enrollment();
        enrollment.setStatus(EnrollmentStatus.SELECTED);

        when(enrollmentRepository
                .findByStudentAndCourseOffering_Course_CodeAndCourseOffering_SectionAndCourseOffering_Semester_Name(
                        student, "AP", 1, "1403-1"))
                .thenReturn(Optional.of(enrollment));

        enrollmentService.dropCourse(student, req);

        assertEquals(EnrollmentStatus.DROPPED, enrollment.getStatus());
    }

    @Test
    void dropCourse_notSelected_shouldThrow() {

        DropCourseRequest req =
                new DropCourseRequest("AP", 1, "1403-1");

        Enrollment enrollment = new Enrollment();
        enrollment.setStatus(EnrollmentStatus.PASSED);

        when(enrollmentRepository
                .findByStudentAndCourseOffering_Course_CodeAndCourseOffering_SectionAndCourseOffering_Semester_Name(
                        student, "AP", 1, "1403-1"))
                .thenReturn(Optional.of(enrollment));

        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> enrollmentService.dropCourse(student, req)
        );

        assertEquals(nonSelectedStatus.getMessage(), ex.getMessage());
    }

}
