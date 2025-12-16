package com.mch.unicoursehub.service.impl;

import com.mch.unicoursehub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Service implementation of {@link UserDetailsService} for Spring Security.
 *
 * <p>This service is responsible for loading user details required for
 * authentication and authorization. It retrieves user entities from the
 * {@link UserRepository} by their unique user number.</p>
 *
 * <p>If a user with the specified user number does not exist, a
 * {@link UsernameNotFoundException} is thrown, which is handled by
 * Spring Security during authentication.</p>
 *
 * @see UserRepository
 * @see org.springframework.security.core.userdetails.UserDetailsService
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Loads a user by their unique user number.
     *
     * @param userNumber the unique identifier of the user
     * @return a {@link UserDetails} object representing the authenticated user
     * @throws UsernameNotFoundException if no user is found with the given user number
     */
    @Override
    public UserDetails loadUserByUsername(String userNumber)
            throws UsernameNotFoundException {

        return userRepository.findByUserNumber(userNumber)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
