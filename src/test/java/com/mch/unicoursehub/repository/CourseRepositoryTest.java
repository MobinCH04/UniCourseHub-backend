package com.mch.unicoursehub.repository;

import com.mch.unicoursehub.model.entity.Course;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@TestMethodOrder(MethodOrderer.DisplayName.class)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootTest
@Transactional
class CourseRepositoryTest {

    @Container
    private static final GenericContainer<?> mariadbContainer = new GenericContainer<>(DockerImageName.parse("mariadb:latest"))
            .withExposedPorts(3306)
            .withEnv("MYSQL_DATABASE", "testdb")
            .withEnv("MYSQL_USER", "testuser")
            .withEnv("MYSQL_PASSWORD", "testpass")
            .withEnv("MYSQL_ROOT_PASSWORD", "rootpass");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> String.format("jdbc:mariadb://localhost:%d/testdb", mariadbContainer.getMappedPort(3306)));
        registry.add("spring.datasource.username", () -> "testuser");
        registry.add("spring.datasource.password", () -> "testpass");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private CourseRepository courseRepository;

    static Course savedCourse1;
    static Course savedCourse2;

    @Test
    @Order(1)
    void testSaveCourses() {
        Course c1 = Course.builder()
                .code("CS101")
                .name("Algorithms")
                .unit(3)
                .build();

        Course c2 = Course.builder()
                .code("CS50")
                .name("Data Structures")
                .unit(3)
                .build();

        savedCourse1 = courseRepository.saveAndFlush(c1);
        savedCourse2 = courseRepository.saveAndFlush(c2);

        assertThat(savedCourse1.getCid()).isNotNull();
        assertThat(savedCourse2.getCid()).isNotNull();
    }


    // ---------- 2) findByCode ----------
    @Test
    @Order(2)
    void testFindByCode() {
        // Arrange: ایجاد و ذخیره Course
        Course course = Course.builder()
                .code("CS101")
                .name("Algorithms")
                .unit(3)
                .build();
        courseRepository.saveAndFlush(course); // flush ضروری است

        // Act
        Optional<Course> found = courseRepository.findByCode("CS101");

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Algorithms");
    }



    // ---------- 3) existsByCode ----------
    @Test
    @Order(3)
    void testExistsByCode() {
        // Arrange: ایجاد و ذخیره کورس
        Course course = Course.builder()
                .code("CS50")
                .name("Introduction to CS")
                .build();
        courseRepository.saveAndFlush(course); // حتما flush کن

        // Act
        boolean exists = courseRepository.existsByCode("CS50");

        // Assert
        assertThat(exists).isTrue();
    }



    // ---------- 4) findByCodeIn ----------
    @Test
    @Order(4)
    void testFindByCodeIn() {
        // Arrange: ایجاد و ذخیره کورس‌ها
        Course c1 = Course.builder()
                .code("CS101")
                .name("Intro to CS 101")
                .unit(3)
                .build();

        Course c2 = Course.builder()
                .code("CS50")
                .name("Intro to CS 50")
                .unit(4)
                .build();

        courseRepository.saveAndFlush(c1);
        courseRepository.saveAndFlush(c2);

        // Act
        List<Course> found = courseRepository.findByCodeIn(List.of("CS101", "CS50"));

        // Assert
        assertThat(found).hasSize(2);
        assertThat(found).extracting(Course::getCode)
                .containsExactlyInAnyOrder("CS101", "CS50");
    }



    // ---------- 5) Unique constraint check ----------
    @Test
    @Order(4)
    void testUniqueCodeConstraint() {
        // Arrange: ذخیره رکورد اولیه
        Course original = Course.builder()
                .code("CS101")
                .name("Algorithms")
                .unit(3)
                .build();
        courseRepository.saveAndFlush(original);

        // Act & Assert: رکورد duplicate باید Exception بدهد
        Course duplicate = Course.builder()
                .code("CS101")  // duplicate
                .name("Duplicate Name")
                .unit(2)
                .build();

        assertThatThrownBy(() -> courseRepository.saveAndFlush(duplicate))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

}