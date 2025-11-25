package com.mch.unicoursehub.security.configuration;

import com.mch.unicoursehub.security.LogOutService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class DashboardSecurityConfiguration {

    private final AuthenticationProvider authenticationProvider;
    private final LogOutService logOutService;

    @Bean
    @Order(3)
    public SecurityFilterChain LoginSecurityFilterChain(HttpSecurity http) throws Exception{

        http.csrf(AbstractHttpConfigurer::disable);

        http.cors(Customizer.withDefaults());

        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/auth/**").permitAll()
                        .anyRequest().authenticated()
                )
                .logout(item -> {
                    item.addLogoutHandler(logOutService);
                    item.logoutUrl("/auth/logout");
                    item.logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext());
                })
                .authenticationProvider(authenticationProvider)
                .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}