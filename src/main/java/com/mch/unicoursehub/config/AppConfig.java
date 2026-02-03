package com.mch.unicoursehub.config;

import com.mch.unicoursehub.security.filter.JwtAuthenticationFilter;
import com.mch.unicoursehub.security.filter.RateLimitFilter;
import lombok.AllArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
/**
 * Application-level configuration class.
 *
 * This class defines and configures essential Spring Security beans such as
 * password encoder, authentication provider, authentication manager,
 * and custom security filters
 */
@AllArgsConstructor
@Configuration
public class AppConfig {
    /**
     * Service used to load user-specific data during authentication.
     */
    private final UserDetailsService userDetailsService;

    /**
     * Provides a password encoder bean.
     *
     * Uses BCrypt hashing algorithm to securely encode user passwords.
     *
     * @return a {@link PasswordEncoder} implementation
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures the authentication provider.
     *
     * <p>
     * Uses {@link DaoAuthenticationProvider} with a custom
     * {@link UserDetailsService} and password encoder.
     * </p>
     *
     * @return an {@link AuthenticationProvider} instance
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Exposes the authentication manager bean.
     *
     * <p>
     * Retrieves the {@link AuthenticationManager} from the Spring
     * {@link AuthenticationConfiguration}.
     * </p>
     *
     * @param config authentication configuration provided by Spring
     * @return the configured {@link AuthenticationManager}
     * @throws Exception if authentication manager cannot be created
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Registers the JWT authentication filter.
     *
     * <p>
     * The filter is registered but disabled to prevent double execution,
     * since it is expected to be managed manually within the Spring Security
     * filter chain.
     * </p>
     *
     * @param jwtTokenAuthenticationFilter JWT authentication filter
     * @return a disabled {@link FilterRegistrationBean} for the JWT filter
     */
    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> registerFilter(JwtAuthenticationFilter jwtTokenAuthenticationFilter) {
        FilterRegistrationBean<JwtAuthenticationFilter> filterRegistrationBean = new FilterRegistrationBean<>(jwtTokenAuthenticationFilter);

        // Disable automatic filter registration
        filterRegistrationBean.setEnabled(false);
        return filterRegistrationBean;
    }

    /**
     * Bean to register the rate-limiting filter.
     *
     * @param rateLimitFilter The rate-limiting filter to be registered.
     * @return A FilterRegistrationBean for the rate-limiting filter.
     */
    @Bean
    public FilterRegistrationBean<RateLimitFilter> registerRateLimitFilter(RateLimitFilter rateLimitFilter) {
        FilterRegistrationBean<RateLimitFilter> filterRegistrationBean = new FilterRegistrationBean<>(rateLimitFilter);
        filterRegistrationBean.setEnabled(false);
        return filterRegistrationBean;
    }

    /**
     * Bean for configuring CORS settings.
     * Allows all origins, headers, and methods for cross-origin requests.
     *
     * @return A CorsConfigurationSource for the application.
     */
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("*");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
