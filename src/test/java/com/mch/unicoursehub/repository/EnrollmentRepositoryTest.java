package com.mch.unicoursehub.repository;

import com.mch.unicoursehub.model.entity.*;
import com.mch.unicoursehub.model.enums.EnrollmentStatus;
import com.mch.unicoursehub.model.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@Transactional
class EnrollmentRepositoryTest {

    @Container
    static GenericContainer<?> mariadb =
            new GenericContainer<>(DockerImageName.parse("mariadb:latest"))
                    .withExposedPorts(3306)
                    .withEnv("MYSQL_DATABASE", "testdb")
                    .withEnv("MYSQL_USER", "testuser")
                    .withEnv("MYSQL_PASSWORD", "testpass")
                    .withEnv("MYSQL_ROOT_PASSWORD", "rootpass");

    @DynamicPropertySource
    static void configureDatasource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url",
                () -> "jdbc:mariadb://localhost:" + mariadb.getMappedPort(3306) + "/testdb");
        registry.add("spring.datasource.username", () -> "testuser");
        registry.add("spring.datasource.password", () -> "testpass");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    EnrollmentRepository enrollmentRepository;
    @Autowired CourseOfferingRepository courseOfferingRepository;
    @Autowired CourseRepository courseRepository;
    @Autowired SemesterRepository semesterRepository;
    @Autowired UserRepository userRepository;

    Course course;
    Semester semester;
    User student;
    User professor;
    CourseOffering offering;

    @BeforeEach
    void setup() {

        enrollmentRepository.deleteAll();
        courseOfferingRepository.deleteAll();
        courseRepository.deleteAll();
        semesterRepository.deleteAll();
        userRepository.deleteAll();

        course = courseRepository.save(
                Course.builder()
                        .code("CS101")
                        .name("Algorithms")
                        .unit(3)
                        .build()
        );

        semester = semesterRepository.save(
                Semester.builder()
                        .name("1404-1")
                        .startDate(LocalDate.of(2025, 2, 1))
                        .endDate(LocalDate.of(2025, 6, 30))
                        .minUnits(12)
                        .maxUnits(24)
                        .build()
        );

        professor = userRepository.save(
                User.builder()
                        .firstName("Ali")
                        .lastName("Ahmadi")
                        .userNumber("P100")
                        .phoneNumber("09120000001")
                        .password("pass")
                        .nationalCode("1111111111")
                        .role(Role.PROFESSOR)
                        .isAccountLocked(false)
                        .build()
        );

        student = userRepository.save(
                User.builder()
                        .firstName("Sara")
                        .lastName("Karimi")
                        .userNumber("S200")
                        .phoneNumber("09120000002")
                        .password("pass")
                        .nationalCode("2222222222")
                        .role(Role.STUDENT)
                        .isAccountLocked(false)
                        .build()
        );

        offering = courseOfferingRepository.save(
                CourseOffering.builder()
                        .course(course)
                        .semester(semester)
                        .professor(professor)
                        .capacity(30)
                        .section(1)
                        .examDate(LocalDateTime.now())
                        .classRoom("101")
                        .build()
        );
    }

    // ---------- countByCourseOffering ----------
    @Test
    void countByCourseOffering_shouldReturnCorrectCount() {

        enrollmentRepository.save(
                Enrollment.builder()
                        .student(student)
                        .courseOffering(offering)
                        .status(EnrollmentStatus.SELECTED)
                        .build()
        );

        enrollmentRepository.save(
                Enrollment.builder()
                        .student(student)
                        .courseOffering(offering)
                        .status(EnrollmentStatus.PASSED)
                        .build()
        );

        long count = enrollmentRepository.countByCourseOffering(offering);

        assertThat(count).isEqualTo(2);
    }

    // ---------- existsByStudentAndSemesterAndCourse ----------
    @Test
    void existsByStudentSemesterAndCourse_shouldReturnTrue() {

        enrollmentRepository.save(
                Enrollment.builder()
                        .student(student)
                        .courseOffering(offering)
                        .status(EnrollmentStatus.PASSED)
                        .build()
        );

        boolean exists =
                enrollmentRepository.existsByStudentAndCourseOffering_SemesterAndCourseOffering_Course(
                        student, semester, course
                );

        assertThat(exists).isTrue();
    }

    // ---------- findByStudentAndSemester ----------
    @Test
    void findByStudentAndSemester_shouldReturnEnrollments() {

        enrollmentRepository.save(
                Enrollment.builder()
                        .student(student)
                        .courseOffering(offering)
                        .status(EnrollmentStatus.PASSED)
                        .build()
        );

        List<Enrollment> result =
                enrollmentRepository.findByStudentAndCourseOffering_Semester(student, semester);

        assertThat(result).hasSize(1);
    }

    // ---------- findByStudentAndStatus ----------
    @Test
    void findByStudentAndStatus_shouldFilterByStatus() {

        enrollmentRepository.save(
                Enrollment.builder()
                        .student(student)
                        .courseOffering(offering)
                        .status(EnrollmentStatus.SELECTED)
                        .build()
        );

        enrollmentRepository.save(
                Enrollment.builder()
                        .student(student)
                        .courseOffering(offering)
                        .status(EnrollmentStatus.PASSED)
                        .build()
        );

        List<Enrollment> approved =
                enrollmentRepository.findByStudentAndStatus(student, EnrollmentStatus.PASSED);

        assertThat(approved).hasSize(1);
    }

    // ---------- existsByStudentCourseAndStatus ----------
    @Test
    void existsByStudentCourseAndStatus_shouldReturnTrue() {

        enrollmentRepository.save(
                Enrollment.builder()
                        .student(student)
                        .courseOffering(offering)
                        .status(EnrollmentStatus.PASSED)
                        .build()
        );

        boolean exists =
                enrollmentRepository.existsByStudentAndCourseOffering_CourseAndStatus(
                        student, course, EnrollmentStatus.PASSED
                );

        assertThat(exists).isTrue();
    }

    // ---------- findByStudentCourseCodeSectionSemester ----------
    @Test
    void findByStudentCourseCodeSectionSemester_shouldReturnEnrollment() {

        enrollmentRepository.save(
                Enrollment.builder()
                        .student(student)
                        .courseOffering(offering)
                        .status(EnrollmentStatus.SELECTED)
                        .build()
        );

        Optional<Enrollment> found =
                enrollmentRepository
                        .findByStudentAndCourseOffering_Course_CodeAndCourseOffering_SectionAndCourseOffering_Semester_Name(
                                student, "CS101", 1, "1404-1"
                        );

        assertThat(found).isPresent();
    }

    // ---------- existsByStudentSemesterCourseAndStatus ----------
    @Test
    void existsByStudentSemesterCourseAndStatus_shouldReturnTrue() {

        enrollmentRepository.save(
                Enrollment.builder()
                        .student(student)
                        .courseOffering(offering)
                        .status(EnrollmentStatus.SELECTED)
                        .build()
        );

        boolean exists =
                enrollmentRepository
                        .existsByStudentAndCourseOffering_SemesterAndCourseOffering_CourseAndStatus(
                                student, semester, course, EnrollmentStatus.SELECTED
                        );

        assertThat(exists).isTrue();
    }
}
