package com.mch.unicoursehub.service.impl;

import com.mch.unicoursehub.exceptions.NotFoundException;
import com.mch.unicoursehub.model.dto.AuthRequestResponse;
import com.mch.unicoursehub.model.dto.UserLogin;
import com.mch.unicoursehub.model.entity.User;
import com.mch.unicoursehub.repository.UserRepository;
import com.mch.unicoursehub.service.LoginService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import static com.mch.unicoursehub.ConstErrors.*;
/**
 * Service implementation responsible for user authentication and session management.
 *
 * <p>This service handles password-based login, token generation, and logout
 * operations. It delegates credential validation to Spring Security's
 * {@link AuthenticationManager} and issues access and refresh tokens
 * upon successful authentication.</p>
 *
 * <p>The service integrates with {@link TokenServiceImpl} to generate and
 * revoke tokens, ensuring secure session handling and proper logout behavior.</p>
 *
 * @see LoginService
 * @see AuthenticationManager
 * @see TokenServiceImpl
 */
@AllArgsConstructor
@Service
public class LoginServiceImpl implements LoginService {

    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;

    private final TokenServiceImpl tokenService;

    /**
     * Authenticates a user using user number and password credentials.
     *
     * <p>This method relies on Spring Security to validate the provided
     * credentials. If authentication succeeds, the associated user entity
     * is retrieved and authentication tokens are generated.</p>
     *
     * @param userLogin login request containing user number and password
     * @return an {@link AuthRequestResponse} containing authentication tokens
     *
     * @throws org.springframework.security.core.AuthenticationException
     *         if authentication fails
     */
    @Override
    public AuthRequestResponse pwdUserLogin(UserLogin userLogin) {

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                userLogin.userNumber(), userLogin.password()
        ));

        User user = userRepository.findByUserNumber(userLogin.userNumber()).orElseThrow(() -> new NotFoundException(userNotFound));

        return loginSuccess(user);
    }

    /**
     * Builds a successful authentication response for an authenticated user.
     *
     * <p>This method generates a new access token and refresh token for the user
     * and assembles the response DTO containing user identity and role information.</p>
     *
     * @param user authenticated user entity
     * @return an {@link AuthRequestResponse} containing user details and tokens
     */
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

    /**
     * Logs out a user by revoking all active tokens associated with the account.
     *
     * <p>This operation ensures that previously issued access and refresh tokens
     * are invalidated and can no longer be used for authentication.</p>
     *
     * @param userNumber unique identifier of the user to log out
     */
    public void logout(String userNumber){
        User user = userRepository.findByUserNumber(userNumber).orElseThrow();

        tokenService.revokeTokens(user);
    }
}
