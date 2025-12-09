package com.mch.unicoursehub.repository;

import com.mch.unicoursehub.model.entity.User;
import com.mch.unicoursehub.model.enums.Role;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import org.testcontainers.utility.DockerImageName;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Testcontainers
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootTest
class UserRepositoryTest {

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
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        AssertionsForClassTypes.assertThat(mariadbContainer.isRunning()).isTrue();
    }

    @Test
    void saveAndDeleteUser() {

        User user = User.builder()
                .firstName("محمدمهدی")
                .lastName("خواجه زاده")
                .phoneNumber("09388209270")
                .password("1234")
                .isAccountLocked(false)
                .role(Role.ADMIN)
                .nationalCode("123456")
                .userNumber("123456")
                .build();

        User save = userRepository.saveAndFlush(user);
        assertThat(save.getUid()).isNotNull();
        assertThat(save.getRole()).isNotNull();

        userRepository.deleteById(save.getUid());

        Optional<User> byId = userRepository.findById(save.getUid());

        assertThat(byId.isEmpty()).isEqualTo(true);


    }

    @Test
    void findByUserNumber_shouldReturnUser() {
        User user = User.builder()
                .firstName("Test")
                .lastName("User")
                .phoneNumber("09120000000")
                .password("pass")
                .isAccountLocked(false)
                .role(Role.ADMIN)
                .nationalCode("654321")
                .userNumber("U222")
                .build();

        userRepository.saveAndFlush(user);

        Optional<User> found = userRepository.findByUserNumber("U222");

        assertThat(found).isPresent();
        assertThat(found.get().getUserNumber()).isEqualTo("U222");
    }

}