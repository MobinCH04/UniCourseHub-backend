package com.mch.unicoursehub.security.configuration;

import com.mch.unicoursehub.model.enums.Role;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration class for managing access to the documentation-related endpoints.
 * This class configures authentication, authorization, and CSRF handling for endpoints related to documentation and API documentation.
 */
@AllArgsConstructor
@Configuration
@EnableWebSecurity
public class DocSecurityConfiguration {

    private final UserDetailsService userDetailService;
    private final PasswordEncoder passwordEncoder;

    public static final String[] DOC_ROUTE = new String[]{"/monitor/**", "/doc/**", "/v2/api-docs",
            "/v2/api-docs/**", "/v3/api-docs", "/v3/api-docs/**", "/scu-api/**", "/scu-api", "/configuration/ui",
            "/swagger-resources/**", "/configuration/security", "/swagger-ui.html", "/swagger-ui/**", "/webjars/**"};

    /**
     * Configures security for the documentation-related endpoints, such as Swagger and API documentation.
     * This method defines which endpoints are publicly accessible, which require specific roles (e.g., ADMIN),
     * and the authentication mechanism used to access the documentation.
     *
     * @param http the {@link HttpSecurity} object used to configure HTTP security
     * @return the configured {@link SecurityFilterChain}
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    @Order(2)
    public SecurityFilterChain docFilterChain(HttpSecurity http) throws Exception {


        http.csrf(AbstractHttpConfigurer::disable);

        http.cors(Customizer.withDefaults());

        http.securityMatcher("/login", "/logout", "/doc/**", "/v2/api-docs",
                        "/v2/api-docs/**", "/v3/api-docs", "/v3/api-docs/**", "/configuration/ui",
                        "/swagger-resources/**", "/configuration/security", "/swagger-ui.html", "/swagger-ui/**", "/webjars/**")
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("/login", "/logout").permitAll()
                                .requestMatchers("/monitor/**", "/doc/**", "/v2/api-docs",
                                        "/v2/api-docs/**", "/v3/api-docs", "/v3/api-docs/**", "/scu-api/**", "/scu-api", "/configuration/ui",
                                        "/swagger-resources/**", "/configuration/security", "/swagger-ui.html", "/swagger-ui/**", "/webjars/**").hasAuthority(Role.ADMIN.name())
                                .anyRequest().authenticated()
                )
                .authenticationProvider(DocAuthenticationProvider())
                .formLogin(login -> login.defaultSuccessUrl("/doc"))
                .logout(logout -> logout.clearAuthentication(true).logoutSuccessUrl("/login"));

        return http.build();
    }

    /**
     * Configures the authentication provider used for user authentication during login.
     * The provider uses a {@link UserDetailsService} to load user details and a {@link PasswordEncoder} for password verification.
     *
     * @return the configured {@link DaoAuthenticationProvider}
     */
    DaoAuthenticationProvider DocAuthenticationProvider() {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider(userDetailService);
        auth.setPasswordEncoder(passwordEncoder);
        return auth;
    }

}