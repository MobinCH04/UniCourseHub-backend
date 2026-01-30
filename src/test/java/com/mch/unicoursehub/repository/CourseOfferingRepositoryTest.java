package com.mch.unicoursehub.repository;

import com.mch.unicoursehub.model.entity.Course;
import com.mch.unicoursehub.model.entity.CourseOffering;
import com.mch.unicoursehub.model.entity.Semester;
import com.mch.unicoursehub.model.entity.User;
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

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
@Testcontainers
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@Transactional
class CourseOfferingRepositoryTest {

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
    CourseOfferingRepository courseOfferingRepository;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    SemesterRepository semesterRepository;

    @Autowired
    UserRepository userRepository;

    Course course;
    Semester semester;
    User professor;

    @BeforeEach
    void setup() {
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
                        .phoneNumber("09120000000")
                        .password("pass")
                        .nationalCode("1234567890")
                        .role(Role.PROFESSOR)
                        .isAccountLocked(false)
                        .build()
        );
    }

    // ---------- countByCourseAndSemester ----------
    @Test
    void countByCourseAndSemester_shouldReturnCorrectCount() {

        courseOfferingRepository.save(
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

        courseOfferingRepository.save(
                CourseOffering.builder()
                        .course(course)
                        .semester(semester)
                        .professor(professor)
                        .capacity(25)
                        .section(2)
                        .examDate(LocalDateTime.now())
                        .classRoom("102")
                        .build()
        );

        int count = courseOfferingRepository.countByCourseAndSemester(course, semester);

        assertThat(count).isEqualTo(2);
    }

    // ---------- findBySemester ----------
    @Test
    void findBySemester_shouldReturnOfferingsOfSemester() {

        CourseOffering offering = courseOfferingRepository.save(
                CourseOffering.builder()
                        .course(course)
                        .semester(semester)
                        .professor(professor)
                        .capacity(30)
                        .section(1)
                        .examDate(LocalDateTime.now())
                        .classRoom("201")
                        .build()
        );

        List<CourseOffering> result =
                courseOfferingRepository.findBySemester(semester);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(offering.getId());
    }

    // ---------- findByCourse_CodeAndSection ----------
    @Test
    void findByCourseCodeAndSection_shouldReturnCorrectOffering() {

        courseOfferingRepository.save(
                CourseOffering.builder()
                        .course(course)
                        .semester(semester)
                        .professor(professor)
                        .capacity(40)
                        .section(3)
                        .examDate(LocalDateTime.now())
                        .classRoom("301")
                        .build()
        );

        Optional<CourseOffering> found =
                courseOfferingRepository.findByCourse_CodeAndSection("CS101", 3);

        assertThat(found).isPresent();
        assertThat(found.get().getSection()).isEqualTo(3);
    }

    // ---------- existsByCourseAndSemesterAndIdNot ----------
    @Test
    void existsByCourseAndSemesterAndIdNot_shouldDetectDuplicateOffering() {

        CourseOffering offering1 = courseOfferingRepository.save(
                CourseOffering.builder()
                        .course(course)
                        .semester(semester)
                        .professor(professor)
                        .capacity(20)
                        .section(1)
                        .examDate(LocalDateTime.now())
                        .classRoom("401")
                        .build()
        );

        boolean exists =
                courseOfferingRepository.existsByCourseAndSemesterAndIdNot(
                        course,
                        semester,
                        offering1.getId()
                );

        assertThat(exists).isFalse();

        courseOfferingRepository.save(
                CourseOffering.builder()
                        .course(course)
                        .semester(semester)
                        .professor(professor)
                        .capacity(25)
                        .section(2)
                        .examDate(LocalDateTime.now())
                        .classRoom("402")
                        .build()
        );

        boolean duplicateExists =
                courseOfferingRepository.existsByCourseAndSemesterAndIdNot(
                        course,
                        semester,
                        offering1.getId()
                );

        assertThat(duplicateExists).isTrue();
    }
}
