package com.umc.tomorrow.global.config;

import com.umc.tomorrow.domain.member.entity.User;
import com.umc.tomorrow.domain.member.enums.Provider;
import com.umc.tomorrow.domain.member.repository.UserRepository;
import com.umc.tomorrow.domain.auth.security.CustomOAuth2User;
import com.umc.tomorrow.domain.member.dto.UserResponseDTO;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Profile("dev")
@Component
@RequiredArgsConstructor
public class DevImpersonateFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String actAs = request.getHeader("X-Act-As-User");
        if (actAs != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // 1) 유저 upsert
            User user = Optional.ofNullable(userRepository.findByUsername(actAs))
                    .orElseGet(() -> {
                        User u = new User();
                        u.setUsername(actAs);
                        u.setName(actAs);
                        u.setProvider(Provider.GOOGLE);   // 아무거나 고정
                        return userRepository.save(u);
                    });

            // 2) 네 프로젝트의 생성자 시그니처에 맞게 DTO로 래핑
            UserResponseDTO dto = UserResponseDTO.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .name(user.getName())
                    .email(user.getEmail())
                    .provider(user.getProvider().name())     // String이면 .name()
                    .providerUserId(user.getProviderUserId())// 있으면
                    .build();

            CustomOAuth2User principal = new CustomOAuth2User(dto);
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(principal, null, List.of());
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }
}
