package com.umc.tomorrow.global.config;

import com.umc.tomorrow.domain.auth.jwt.JWTFilter;
import com.umc.tomorrow.domain.auth.jwt.JWTUtil;
import com.umc.tomorrow.domain.auth.security.CustomSuccessHandler;
import com.umc.tomorrow.domain.auth.service.CustomOAuth2UserService;
import com.umc.tomorrow.domain.member.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomSuccessHandler customSuccessHandler;
    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;

    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService, CustomSuccessHandler customSuccessHandler, JWTUtil jwtUtil, UserRepository userRepository) {

        this.customOAuth2UserService = customOAuth2UserService;
        this.customSuccessHandler = customSuccessHandler;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        //csrf disable
        http
                .cors(corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {

                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {

                        CorsConfiguration configuration = new CorsConfiguration();


                        configuration.setAllowedOrigins(Arrays.asList(
                                "http://localhost:3000",           // 개발용
                                "https://umctomorrow.shop",         // 배포용
                                "https://tomorrow-frontend.vercel.app"
                        ));
                        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                        configuration.setAllowCredentials(true);
                        configuration.setAllowedHeaders(Collections.singletonList("*"));
                        configuration.setMaxAge(3600L);
                        configuration.setExposedHeaders(Arrays.asList("Set-Cookie", "Authorization"));

                        return configuration;
                    }
                }));

        http.csrf(AbstractHttpConfigurer::disable); // 추가

        http
                .formLogin((auth) -> auth.disable());
        http
                .httpBasic((auth) -> auth.disable());

        //JWTFilter 추가
        http
                .addFilterBefore(new JWTFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);

        //JWTFilter 추가
        http
                .addFilterAfter(new JWTFilter(jwtUtil), OAuth2LoginAuthenticationFilter.class);

        //oauth2
        http
                .oauth2Login((oauth2) -> oauth2
                        .userInfoEndpoint((userInfoEndpointConfig) -> userInfoEndpointConfig
                                .userService(customOAuth2UserService))
                        .successHandler(new CustomSuccessHandler(jwtUtil, userRepository))
                );
        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/", "/api/ping").permitAll()  //경로별 인가작업
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/api/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/api/v3/api-docs/**"
                        ).permitAll()
                        .requestMatchers("/login").permitAll()
                        .anyRequest().authenticated());
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }
}
