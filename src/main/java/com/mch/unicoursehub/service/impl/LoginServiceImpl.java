package com.mch.unicoursehub.service.impl;

import com.mch.unicoursehub.exceptions.UnAuthorizedException;
import com.mch.unicoursehub.model.dto.AuthRequestResponse;
import com.mch.unicoursehub.model.dto.UserLogin;
import com.mch.unicoursehub.model.entity.User;
import com.mch.unicoursehub.model.enums.TokenType;
import com.mch.unicoursehub.repository.UserRepository;
import com.mch.unicoursehub.service.LoginService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class LoginServiceImpl implements LoginService {

    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;

    private final TokenServiceImpl tokenService;

    @Override
    public AuthRequestResponse pwdUserLogin(UserLogin userLogin) {

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                userLogin.userNumber(), userLogin.password()
        ));

        User user = userRepository.findByUserNumber(userLogin.userNumber()).orElseThrow();

        return loginSuccess(user);
    }

    public AuthRequestResponse loginSuccess(User user){

        String access = tokenService.newAccessToken(user);
        String refresh = tokenService.newRefreshToken(user);

        return AuthRequestResponse.builder()
                .role(user.getRole().name())
                .name(user.fullName())
                .accessToken(access)
                .refreshToken(refresh)
                .build();
    }

    public void logout(String userNumber){
        User user = userRepository.findByUserNumber(userNumber).orElseThrow();

        tokenService.revokeTokens(user);
    }
}
