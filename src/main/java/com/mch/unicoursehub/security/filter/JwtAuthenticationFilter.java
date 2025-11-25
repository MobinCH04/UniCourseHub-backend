package com.mch.unicoursehub.security;

import com.mch.unicoursehub.ConstVal;
import com.mch.unicoursehub.model.entity.Token;
import com.mch.unicoursehub.repository.TokenRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.util.Optional;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@AllArgsConstructor
public class  JwtAuthenticationFilter extends OncePerRequestFilter {


    private final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final TokenRepository tokenRepository;
    private final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) {

        try {

            final String authHeader = request.getHeader(ConstVal.HEADER_AUTHORIZATION);
            final String jwt;
            String username = null;
            final String uuid;

            if (authHeader == null || !authHeader.startsWith(ConstVal.PREFIX_BEARER)) {
                logger.info(" Token rejected... ");
                filterChain.doFilter(request, response);
                return;
            }

            jwt = authHeader.substring(ConstVal.PREFIX_BEARER.length());
            try {
                username = jwtService.extractUsername(jwt);
            } catch (ExpiredJwtException e) {
                logger.error("token expired -> " + e.getMessage());
                filterChain.doFilter(request, response);
                return;
            }
            uuid = jwtService.extractUUID(jwt);

            if (username != null && uuid != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);


                Optional<Token> token = tokenRepository.findByTokenValue(uuid);
                if (token.isEmpty()){
                    filterChain.doFilter(request, response);
                return;
            }


                Token token1 = token.get();

                boolean isTokenValid = !token1.isRevoked() && !token1.isExpired()
                        && token1.getUser().getUsername().equals(username);

                if (isTokenValid && jwtService.isTokenValid(jwt, userDetails)) {
                    logger.info(" Token verified... ");
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

        } catch (Exception ex) {

            handlerExceptionResolver.resolveException(request, response, null, ex);

        }
    }

}