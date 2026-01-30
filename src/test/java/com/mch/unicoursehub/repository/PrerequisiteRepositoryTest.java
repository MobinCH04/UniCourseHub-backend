package com.mch.unicoursehub.repository;

import com.mch.unicoursehub.model.entity.Course;
import com.mch.unicoursehub.model.entity.Prerequisite;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PrerequisiteRepositoryTest {

    @Container
    private static final MariaDBContainer<?> mariadb =
            new MariaDBContainer<>("mariadb:latest")
                    .withDatabaseName("testdb")
                    .withUsername("testuser")
                    .withPassword("testpass");

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mariadb::getJdbcUrl);
        registry.add("spring.datasource.username", mariadb::getUsername);
        registry.add("spring.datasource.password", mariadb::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private PrerequisiteRepository prerequisiteRepository;

    @Autowired
    private CourseRepository courseRepository;

    static UUID cid;
    static Prerequisite savedPrerequisite;
    static Course courseA;      // main course
    static Course prerequisiteCourse; // prerequisite course

    @Test
    @Order(1)
    void testCreateCourses() {
        courseA = courseRepository.saveAndFlush(
                Course.builder()
                        .name("Algorithms")
                        .code("CS101")
                        .build()
        );

        prerequisiteCourse = courseRepository.saveAndFlush(
                Course.builder()
                        .name("Data Structures")
                        .code("CS50")
                        .build()
        );

        assertThat(courseA.getCid()).isNotNull();
        assertThat(prerequisiteCourse.getCid()).isNotNull();
    }

    @Test
    @Order(2)
    void testSavePrerequisite() {
        Prerequisite prerequisite = Prerequisite.builder()
                .course(courseA)
                .prerequisite(prerequisiteCourse)
                .build();

        savedPrerequisite = prerequisiteRepository.saveAndFlush(prerequisite);

        assertThat(savedPrerequisite.getId()).isNotNull();
        assertThat(savedPrerequisite.getCourse().getCid()).isEqualTo(courseA.getCid());
        assertThat(savedPrerequisite.getPrerequisite().getCid()).isEqualTo(prerequisiteCourse.getCid());
    }

    @Test
    @Order(3)
    void testFindByPrerequisiteCid() {
        List<Prerequisite> prereqList =
                prerequisiteRepository.findByPrerequisiteCid(prerequisiteCourse.getCid());

        assertThat(prereqList).hasSize(1);
        assertThat(prereqList.get(0).getCourse().getCid()).isEqualTo(courseA.getCid());
    }

}