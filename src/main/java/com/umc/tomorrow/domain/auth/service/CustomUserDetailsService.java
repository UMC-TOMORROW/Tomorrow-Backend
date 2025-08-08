package com.umc.tomorrow.domain.auth.service;

import com.umc.tomorrow.domain.auth.security.CustomUserDetails;
import com.umc.tomorrow.domain.member.entity.User;
import com.umc.tomorrow.domain.member.enums.Provider;
import com.umc.tomorrow.domain.member.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // username = "provider providerId" 형식
        String[] parts = username.split(" ");
        if (parts.length != 2) {
            throw new UsernameNotFoundException("Invalid username format");
        }

        String providerStr = parts[0];
        String providerUserId = parts[1];

        Provider provider;
        try {
            provider = Provider.valueOf(providerStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new UsernameNotFoundException("Unsupported provider: " + providerStr);
        }

        User user = userRepository.findByProviderAndProviderUserId(provider, providerUserId);


        return new CustomUserDetails(user);
    }
}
