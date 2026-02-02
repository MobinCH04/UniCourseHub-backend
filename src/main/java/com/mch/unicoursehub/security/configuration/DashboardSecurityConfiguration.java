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

/**
 * Security configuration for the dashboard and REST API endpoints.
 *
 * <p>
 * This class defines the Spring Security filter chain with:
 * <ul>
 *     <li>JWT authentication</li>
 *     <li>Rate limiting</li>
 *     <li>Role-based access control for ADMIN, PROFESSOR, and STUDENT roles</li>
 *     <li>Stateless session management</li>
 *     <li>Custom logout handling</li>
 * </ul>
 * </p>
 */
@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class DashboardSecurityConfiguration {

    private final AuthenticationProvider authenticationProvider;
    private final LogOutService logOutService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final RateLimitFilter rateLimitFilter;

    /**
     * Configures the security filter chain for the application.
     *
     * <p>
     * - Disables CSRF protection (for REST API).
     * - Enables CORS with default configuration.
     * - Sets role-based access rules for endpoints.
     * - Configures logout handler and clears security context.
     * - Adds JWT authentication and rate-limit filters.
     * - Uses stateless session management.
     * </p>
     *
     * @param http the {@link HttpSecurity} object to configure
     * @return a built {@link SecurityFilterChain}
     * @throws Exception in case of any security configuration error
     */
    @Bean
    @Order(3)
    public SecurityFilterChain LoginSecurityFilterChain(HttpSecurity http) throws Exception{

        // Disable CSRF for REST API
        http.csrf(AbstractHttpConfigurer::disable);

        // Enable CORS with default configuration
        http.cors(Customizer.withDefaults());

        // Configure endpoint access rules and authentication
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

                        .requestMatchers(HttpMethod.GET, "/semesters")
                        .hasAnyAuthority(Role.PROFESSOR.name(), Role.STUDENT.name(), Role.ADMIN.name())

                        .requestMatchers("/semesters/**")
                        .hasAuthority(Role.ADMIN.name())

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