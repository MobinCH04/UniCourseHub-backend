package com.mch.unicoursehub.service.impl;

import com.mch.unicoursehub.exceptions.BadRequestException;
import com.mch.unicoursehub.exceptions.NotFoundException;
import com.mch.unicoursehub.model.dto.DropCourseRequest;
import com.mch.unicoursehub.model.dto.EnrollCourseRequest;
import com.mch.unicoursehub.model.dto.StudentEnrollmentResponse;
import com.mch.unicoursehub.model.entity.*;
import com.mch.unicoursehub.model.enums.DayOfWeek;
import com.mch.unicoursehub.model.enums.EnrollmentStatus;
import com.mch.unicoursehub.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class EnrollmentServiceImplTest {

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private CourseOfferingRepository courseOfferingRepository;

    @Mock
    private PrerequisiteRepository prerequisiteRepository;

    @Mock
    private SemesterRepository semesterRepository;

    @InjectMocks
    private EnrollmentServiceImpl service;

    private User student;
    private Course course;
    private Semester semester;
    private CourseOffering offering;
    private TimeSlot timeSlot;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        student = User.builder()
                .uid(UUID.randomUUID())
                .firstName("Alice")
                .lastName("Smith")
                .userNumber("U100")
                .build();

        course = Course.builder()
                .cid(UUID.randomUUID())
                .code("CS101")
                .name("Algorithms")
                .unit(3)
                .build();

        semester = Semester.builder()
                .id(UUID.randomUUID())
                .name("1404-1")
                .minUnits(12)
                .maxUnits(24)
                .startDate(LocalDate.of(2025, 2, 1))
                .endDate(LocalDate.of(2025, 6, 30))
                .build();

        timeSlot = TimeSlot.builder()
                .id(UUID.randomUUID())
                .dayOfWeek(DayOfWeek.MONDAY)
                .startTime(LocalTime.of(8,0))
                .endTime(LocalTime.of(10,0))
                .build();

        offering = CourseOffering.builder()
                .course(course)
                .semester(semester)
                .capacity(30)
                .section(1)
                .examDate(LocalDateTime.of(2025,6,15,9,0))
                .classRoom("101")
                .professor(student) // fake professor for test
                .timeSlots(List.of(timeSlot))
                .build();
    }

    // ----------- enrollStudent -----------

    @Test
    void enrollStudent_shouldSaveEnrollment() {
        EnrollCourseRequest req = new EnrollCourseRequest("CS101", 1);

        when(courseOfferingRepository.findByCourse_CodeAndSection("CS101", 1))
                .thenReturn(Optional.of(offering));
        when(enrollmentRepository.countByCourseOffering(offering)).thenReturn(0L);
        when(enrollmentRepository.existsByStudentAndCourseOffering_SemesterAndCourseOffering_CourseAndStatus(
                student, semester, course, EnrollmentStatus.DROPPED)).thenReturn(false);
        when(enrollmentRepository.existsByStudentAndCourseOffering_SemesterAndCourseOffering_Course(
                student, semester, course)).thenReturn(false);
        when(prerequisiteRepository.findByCourse(course)).thenReturn(List.of());
        when(enrollmentRepository.findByStudentAndStatus(student, EnrollmentStatus.PASSED)).thenReturn(List.of());
        when(enrollmentRepository.findByStudentAndCourseOffering_Semester(student, semester)).thenReturn(List.of());

        service.enrollStudent(student, req);

        verify(enrollmentRepository, times(1)).save(any(Enrollment.class));
    }

    @Test
    void enrollStudent_whenCourseOfferingNotFound_shouldThrow() {
        EnrollCourseRequest req = new EnrollCourseRequest("CS999", 1);

        when(courseOfferingRepository.findByCourse_CodeAndSection("CS999", 1))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.enrollStudent(student, req))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void enrollStudent_whenCapacityFull_shouldThrow() {
        EnrollCourseRequest req = new EnrollCourseRequest("CS101", 1);

        when(courseOfferingRepository.findByCourse_CodeAndSection("CS101", 1))
                .thenReturn(Optional.of(offering));
        when(enrollmentRepository.countByCourseOffering(offering)).thenReturn(30L);

        assertThatThrownBy(() -> service.enrollStudent(student, req))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void enrollStudent_whenDroppedBefore_shouldThrow() {
        EnrollCourseRequest req = new EnrollCourseRequest("CS101", 1);

        when(courseOfferingRepository.findByCourse_CodeAndSection("CS101", 1))
                .thenReturn(Optional.of(offering));
        when(enrollmentRepository.countByCourseOffering(offering)).thenReturn(0L);
        when(enrollmentRepository.existsByStudentAndCourseOffering_SemesterAndCourseOffering_CourseAndStatus(
                student, semester, course, EnrollmentStatus.DROPPED)).thenReturn(true);

        assertThatThrownBy(() -> service.enrollStudent(student, req))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void enrollStudent_whenAlreadyTaken_shouldThrow() {
        EnrollCourseRequest req = new EnrollCourseRequest("CS101", 1);

        when(courseOfferingRepository.findByCourse_CodeAndSection("CS101", 1))
                .thenReturn(Optional.of(offering));
        when(enrollmentRepository.countByCourseOffering(offering)).thenReturn(0L);
        when(enrollmentRepository.existsByStudentAndCourseOffering_SemesterAndCourseOffering_CourseAndStatus(
                student, semester, course, EnrollmentStatus.DROPPED)).thenReturn(false);
        when(enrollmentRepository.existsByStudentAndCourseOffering_SemesterAndCourseOffering_Course(
                student, semester, course)).thenReturn(true);

        assertThatThrownBy(() -> service.enrollStudent(student, req))
                .isInstanceOf(BadRequestException.class);
    }

    // ----------- getStudentEnrollments -----------

    @Test
    void getStudentEnrollments_shouldReturnList() {
        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourseOffering(offering);
        enrollment.setStatus(EnrollmentStatus.SELECTED);

        when(semesterRepository.findByName("1404-1")).thenReturn(Optional.of(semester));
        when(enrollmentRepository.findByStudentAndCourseOffering_Semester(student, semester))
                .thenReturn(List.of(enrollment));

        List<StudentEnrollmentResponse> responses = service.getStudentEnrollments(student, "1404-1");

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).courseCode()).isEqualTo("CS101");
    }

    @Test
    void getStudentEnrollments_whenSemesterNotFound_shouldThrow() {
        when(semesterRepository.findByName("1404-1")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getStudentEnrollments(student, "1404-1"))
                .isInstanceOf(NotFoundException.class);
    }

    // ----------- dropCourse -----------

    @Test
    void dropCourse_shouldSetStatusToDropped() {
        DropCourseRequest req = new DropCourseRequest("CS101", 1, "1404-1");
        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourseOffering(offering);
        enrollment.setStatus(EnrollmentStatus.SELECTED);

        when(enrollmentRepository.findByStudentAndCourseOffering_Course_CodeAndCourseOffering_SectionAndCourseOffering_Semester_Name(
                student, "CS101", 1, "1404-1")).thenReturn(Optional.of(enrollment));

        service.dropCourse(student, req);

        assertThat(enrollment.getStatus()).isEqualTo(EnrollmentStatus.DROPPED);
    }

    @Test
    void dropCourse_whenEnrollmentNotFound_shouldThrow() {
        DropCourseRequest req = new DropCourseRequest("CS101", 1, "1404-1");

        when(enrollmentRepository.findByStudentAndCourseOffering_Course_CodeAndCourseOffering_SectionAndCourseOffering_Semester_Name(
                student, "CS101", 1, "1404-1")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.dropCourse(student, req))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void dropCourse_whenStatusNotSelected_shouldThrow() {
        DropCourseRequest req = new DropCourseRequest("CS101", 1, "1404-1");
        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourseOffering(offering);
        enrollment.setStatus(EnrollmentStatus.PASSED);

        when(enrollmentRepository.findByStudentAndCourseOffering_Course_CodeAndCourseOffering_SectionAndCourseOffering_Semester_Name(
                student, "CS101", 1, "1404-1")).thenReturn(Optional.of(enrollment));

        assertThatThrownBy(() -> service.dropCourse(student, req))
                .isInstanceOf(BadRequestException.class);
    }
}
