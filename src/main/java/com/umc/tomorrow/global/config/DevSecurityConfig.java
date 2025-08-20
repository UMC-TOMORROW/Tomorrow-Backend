package com.umc.tomorrow.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.RequestHeaderRequestMatcher;

@Profile("dev")
@Configuration
@RequiredArgsConstructor
public class DevSecurityConfig {

    private final DevImpersonateFilter devImpersonateFilter;

    @Bean
    @Order(1) //dev 체인을 먼저 평가
    public SecurityFilterChain devSecurityFilterChain(HttpSecurity http) throws Exception {
        // ★ X-Act-As-User 헤더가 있을 때만 이 체인을 적용
        http.securityMatcher(new RequestHeaderRequestMatcher("X-Act-As-User"));

        http.csrf(csrf -> csrf.disable());
        http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        http.addFilterBefore(devImpersonateFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
