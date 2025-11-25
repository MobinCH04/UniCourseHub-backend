package com.mch.unicoursehub.config;

import com.mch.unicoursehub.model.enums.Role;
import com.mch.unicoursehub.repository.UserRepository;
import com.mch.unicoursehub.model.entity.User;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
@RequiredArgsConstructor
@Configuration
public class InitData {

    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(InitData.class);

    private final PasswordEncoder passwordEncoder;

    @Bean
    @Profile("dev")
    @Order
    public CommandLineRunner loadData(UserRepository userRepository) {

        return args -> {
            if (userRepository.count() == 0) {
                User Mobin = User.builder()
                        .firstName("مبین")
                        .lastName("چشم براه")
                        .phoneNumber("09168393006")
                        .password(passwordEncoder.encode("#Unicoursehub2025"))
                        .role(Role.ADMIN)
                        .nationalCode("1744201390")
                        .userNumber("40173152")
                        .isAccountLocked(false)
                        .build();

                User Babak = User.builder()
                        .firstName("بابک")
                        .lastName("اسکندری")
                        .phoneNumber("09108645158")
                        .password(passwordEncoder.encode("#Unicoursehub2025"))
                        .role(Role.ADMIN)
                        .nationalCode("1478523695")
                        .userNumber("40173140")
                        .isAccountLocked(false)
                        .build();

                User Sajjad = User.builder()
                        .firstName("سجاد")
                        .lastName("ورمزیار")
                        .phoneNumber("09187679775")
                        .password(passwordEncoder.encode("#Unicoursehub2025"))
                        .role(Role.ADMIN)
                        .nationalCode("1597532486")
                        .userNumber("40173102")
                        .isAccountLocked(false)
                        .build();

                List<User> users = new ArrayList<>();

                users.addAll(List.of(Mobin, Babak, Sajjad));

                userRepository.saveAll(users);
            }
        };
    }
}
