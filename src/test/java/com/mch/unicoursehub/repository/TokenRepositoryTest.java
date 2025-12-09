package com.mch.unicoursehub.repository;

import com.mch.unicoursehub.model.entity.Token;
import com.mch.unicoursehub.model.entity.User;
import com.mch.unicoursehub.model.enums.Role;
import com.mch.unicoursehub.model.enums.TokenType;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@TestMethodOrder(MethodOrderer.DisplayName.class)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootTest
@Transactional
class TokenRepositoryTest {

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
    private TokenRepository tokenRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        AssertionsForClassTypes.assertThat(mariadbContainer.isRunning()).isTrue();
    }

    @BeforeEach
    void cleanDatabase() {
        tokenRepository.deleteAll();
        userRepository.deleteAll();
    }

    private User createUser() {
        User user = User.builder()
                .firstName("Test")
                .lastName("User")
                .phoneNumber("09000000000")
                .password("pass")
                .isAccountLocked(false)
                .role(Role.ADMIN)
                .nationalCode("1111111111")
                .userNumber("U1000")
                .build();
        return userRepository.saveAndFlush(user);
    }

    @Test
    void testFindByUser() {
        User user = createUser();

        Token token = Token.builder()
                .user(user)
                .type(TokenType.ACCESS_TOKEN)
                .uuid(UUID.randomUUID())
                .build();

        tokenRepository.saveAndFlush(token);

        List<Token> tokens = tokenRepository.findByUser(user);
        assertThat(tokens).hasSize(1);
    }

    @Test
    void testFindByUuidAndType() {
        User user = createUser();
        UUID uuid = UUID.randomUUID();

        Token token = Token.builder()
                .uuid(uuid)
                .type(TokenType.ACCESS_TOKEN)
                .user(user)
                .build();

        tokenRepository.saveAndFlush(token);

        Optional<Token> found = tokenRepository.findByUuidAndType(uuid, TokenType.ACCESS_TOKEN);
        assertThat(found).isPresent();
        assertThat(found.get().getType()).isEqualTo(TokenType.ACCESS_TOKEN);
    }

    @Test
    void testFindByUuid() {
        User user = createUser();
        String uuidString = UUID.randomUUID().toString();

        Token token = Token.builder()
                .uuid(UUID.fromString(uuidString))
                .type(TokenType.ACCESS_TOKEN)
                .user(user)
                .build();

        tokenRepository.saveAndFlush(token);

        Optional<Token> found = tokenRepository.findByUuid(UUID.fromString(uuidString));
        assertThat(found).isPresent();
    }

    @Test
    void testDeleteByUuidAndType() {
        User user = createUser();
        UUID uuid = UUID.randomUUID();

        Token token = Token.builder()
                .uuid(uuid)
                .type(TokenType.ACCESS_TOKEN)
                .user(user)
                .build();

        tokenRepository.saveAndFlush(token);

        int deletedCount = tokenRepository.deleteByUuidAndType(uuid, TokenType.ACCESS_TOKEN);
        assertThat(deletedCount).isEqualTo(1);

        assertThat(tokenRepository.findByUuid(uuid)).isEmpty();
    }

    @Test
    void testDeleteByUuid() {
        User user = createUser();
        UUID uuid = UUID.randomUUID();

        Token token = Token.builder()
                .uuid(uuid)
                .type(TokenType.ACCESS_TOKEN)
                .user(user)
                .build();

        tokenRepository.saveAndFlush(token);

        int deleted = tokenRepository.deleteByUuid(uuid);
        assertThat(deleted).isEqualTo(1);

        assertThat(tokenRepository.findByUuid(uuid)).isEmpty();
    }

    @Test
    void testDeleteAllByIds() {
        User user = createUser();

        Token t1 = tokenRepository.saveAndFlush(Token.builder()
                .user(user).type(TokenType.ACCESS_TOKEN)
                .uuid(UUID.randomUUID())
                .build());

        Token t2 = tokenRepository.saveAndFlush(Token.builder()
                .user(user).type(TokenType.ACCESS_TOKEN)
                .uuid(UUID.randomUUID())
                .build());

        int count = tokenRepository.deleteAllByIds(List.of(t1.getTid(), t2.getTid()));

        assertThat(count).isEqualTo(2);
        assertThat(tokenRepository.findById(t1.getTid())).isEmpty();
        assertThat(tokenRepository.findById(t2.getTid())).isEmpty();
    }

}