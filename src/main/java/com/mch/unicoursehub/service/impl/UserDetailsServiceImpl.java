package com.mch.unicoursehub.service.impl;

import com.mch.unicoursehub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userNumber)
            throws UsernameNotFoundException {

        return userRepository.findByUserNumber(userNumber)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
