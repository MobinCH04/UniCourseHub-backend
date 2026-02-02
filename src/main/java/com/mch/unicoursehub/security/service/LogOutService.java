package com.mch.unicoursehub.security.service;

import com.mch.unicoursehub.ConstVal;
import com.mch.unicoursehub.service.impl.LoginServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

/**
 * Service that handles user logout in the application.
 * <p>
 * This class implements {@link LogoutHandler} and is invoked during Spring Security logout process.
 * It extracts the JWT from the request header, validates it, and performs server-side logout
 * by revoking the token via {@link LoginServiceImpl}.
 * </p>
 */
@Service
public class LogOutService implements LogoutHandler {

    private final LoginServiceImpl loginService;
    private final JwtService jwtService;


    /**
     * Constructor for {@link LogOutService}.
     *
     * @param loginService service responsible for handling login and logout operations
     * @param jwtService   service responsible for JWT token operations
     */
    public LogOutService(@Lazy LoginServiceImpl loginService, JwtService jwtService) {
        this.loginService = loginService;
        this.jwtService = jwtService;
    }


    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        String token = request.getHeader(ConstVal.HEADER_AUTHORIZATION);

        if (!token.startsWith(ConstVal.PREFIX_BEARER))
            return;

        String jwt = token.substring(ConstVal.PREFIX_BEARER.length());

        String username = jwtService.extractUsername(jwt);
        String uuid = jwtService.extractUUID(jwt);

        if (username != null && uuid != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            loginService.logout(username);
        }
    }
}