package com.mch.unicoursehub.security.filter;

import com.mch.unicoursehub.ConstVal;
import com.mch.unicoursehub.model.entity.Token;
import com.mch.unicoursehub.model.enums.TokenType;
import com.mch.unicoursehub.repository.TokenRepository;
import com.mch.unicoursehub.security.service.JwtService;
import com.mch.unicoursehub.service.impl.TokenServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Filter for authenticating JWT tokens and setting up the Spring Security context.
 * This filter checks the Authorization header for a valid JWT token, verifies its validity,
 * and sets up the authentication in the SecurityContextHolder if the token is valid.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final HandlerExceptionResolver handlerExceptionResolver;
    private final TokenServiceImpl tokenService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        try {

            // اجازه می‌ده مسیرهای auth بدون JWT عبور کنند
            if (request.getServletPath().contains("/api/v1/auth")) {
                filterChain.doFilter(request, response);
                return;
            }

            final String authHeader = request.getHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ") || authHeader.length() <= 7) {
                filterChain.doFilter(request, response);
                return;
            }

            // گرفتن JWT از هدر
            final String jwt = authHeader.substring(7);
            final String userPhone = jwtService.extractUsername(jwt);
            final String uuid = jwtService.extractUUID(jwt);

            if (userPhone != null
                    && SecurityContextHolder.getContext().getAuthentication() == null
                    && uuid != null) {

                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userPhone);

                boolean isTokenValid = false;
                Optional<Token> token = tokenService.checkToken(UUID.fromString(uuid), TokenType.ACCESS_TOKEN);

                if (token.isPresent()) {
                    isTokenValid = !token.get().isRevoked();
                }

                if (jwtService.isTokenValid(jwt, userDetails) && isTokenValid) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            logger.error(e.getMessage());
            handlerExceptionResolver.resolveException(request, response, null, e);
        }
    }
}
