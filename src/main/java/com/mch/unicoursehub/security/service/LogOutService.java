package com.mch.unicoursehub.security;

import com.mch.unicoursehub.ConstVal;
import com.mch.unicoursehub.service.impl.LoginServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@Service
public class LogOutService implements LogoutHandler {

    private final LoginServiceImpl loginService;
    private final JwtService jwtService;


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