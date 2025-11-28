package com.mch.unicoursehub.security.filter;

import com.mch.unicoursehub.security.service.JwtService;
import com.mch.unicoursehub.service.impl.RateLimitServiceImpl;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * A filter that applies rate-limiting logic to API requests for authenticated users.
 */
@Component
@AllArgsConstructor
@Slf4j
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimitServiceImpl rateLimitService;
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        var authentication = SecurityContextHolder.getContext().getAuthentication();

        // Skip rate limiting for actuator and ajax
        String uri = request.getRequestURI();
        if (uri.contains("/actuator") || uri.contains("/ajax")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Only limit authenticated users
        if (authentication != null) {

            String token = request.getHeader(HttpHeaders.AUTHORIZATION);

            // Ensure token exists and starts with "Bearer "
            if (token == null || !token.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            // Extract raw JWT (no JWE)
            String jwt = token.substring(7);

            // Extract UUID (user ID)
            String uuid = jwtService.extractUUID(jwt);
            if (uuid == null) {
                filterChain.doFilter(request, response);
                return;
            }

            Bucket bucket = rateLimitService.resolveBucket(uuid);
            ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

            if (probe.isConsumed()) {
                response.addHeader("X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));
                filterChain.doFilter(request, response);
            } else {
                long waitSeconds = probe.getNanosToWaitForRefill() / 1_000_000_000;
                response.addHeader("Rate-Limit-Retry-After-Seconds", String.valueOf(waitSeconds));

                response.sendError(
                        HttpStatus.TOO_MANY_REQUESTS.value(),
                        "You have exhausted your API Request Quota"
                );
            }

        } else {
            filterChain.doFilter(request, response);
        }
    }
}
