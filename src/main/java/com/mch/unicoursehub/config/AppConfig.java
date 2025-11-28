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

@AllArgsConstructor
@Configuration
public class AppConfig {

    private final UserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> registerFilter(JwtAuthenticationFilter jwtTokenAuthenticationFilter) {
        FilterRegistrationBean<JwtAuthenticationFilter> filterRegistrationBean = new FilterRegistrationBean<>(jwtTokenAuthenticationFilter);
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
}
