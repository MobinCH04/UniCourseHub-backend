package com.mch.unicoursehub.security.configuration;

import com.mch.unicoursehub.model.enums.Role;
import com.mch.unicoursehub.security.filter.JwtAuthenticationFilter;
import com.mch.unicoursehub.security.filter.RateLimitFilter;
import com.mch.unicoursehub.security.service.LogOutService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class DashboardSecurityConfiguration {

    private final AuthenticationProvider authenticationProvider;
    private final LogOutService logOutService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final RateLimitFilter rateLimitFilter;

    @Bean
    @Order(3)
    public SecurityFilterChain LoginSecurityFilterChain(HttpSecurity http) throws Exception{

        http.csrf(AbstractHttpConfigurer::disable);

        http.cors(Customizer.withDefaults());

        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()

                        .requestMatchers("/time-slots/**")
                        .hasAnyAuthority(Role.ADMIN.name(), Role.PROFESSOR.name(),Role.STUDENT.name())

                        .requestMatchers(HttpMethod.GET, "/course-offerings")
                        .hasAnyAuthority(Role.STUDENT.name(), Role.PROFESSOR.name(), Role.ADMIN.name())

                        .requestMatchers(HttpMethod.POST, "/course-offerings/**")
                        .hasAuthority(Role.ADMIN.name())

                        .requestMatchers("/course-offerings/**")
                        .hasAuthority(Role.ADMIN.name())

                        .requestMatchers("/enrollments/**")
                        .hasAuthority(Role.STUDENT.name())

                        .requestMatchers("/professor/**")
                        .hasAuthority(Role.PROFESSOR.name())

                        .requestMatchers("/admin/**").hasAuthority(Role.ADMIN.name())
                        .requestMatchers("/users/**").hasAuthority(Role.ADMIN.name())
                        .requestMatchers("/semesters/**").hasAuthority(Role.ADMIN.name())

                        .anyRequest().authenticated()
                )
                .logout(item -> {
                    item.addLogoutHandler(logOutService);
                    item.logoutUrl("/auth/logout");
                    item.logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext());
                })
                .authenticationProvider(authenticationProvider)
                .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(rateLimitFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}