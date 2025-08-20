package com.umc.tomorrow.global.config;

import com.umc.tomorrow.domain.auth.security.CustomOAuth2User;
import com.umc.tomorrow.domain.member.dto.UserResponseDTO;
import com.umc.tomorrow.domain.member.entity.MemberType;
import com.umc.tomorrow.domain.member.entity.User;
import com.umc.tomorrow.domain.member.enums.Provider;
import com.umc.tomorrow.domain.member.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Profile("dev")
@Component
@RequiredArgsConstructor
public class DevImpersonateFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String actAs = request.getHeader("X-Act-As-User");

        if (actAs != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // ▼▼ effectively-final 로 유지되도록 한 번만 대입 ▼▼
            final String safeName =
                    actAs.length() > 10 ? actAs.substring(0, 10) : actAs;

            final String emailBase = actAs + "@loadtest.local";
            final String safeEmail =
                    emailBase.length() > 30 ? emailBase.substring(0, 30) : emailBase;

            // username 기준 upsert
            User user = Optional.ofNullable(userRepository.findByUsername(actAs))
                    .orElseGet(() -> {
                        User u = new User();
                        u.setUsername(actAs);              // varchar(50)
                        u.setName(safeName);               // varchar(10)
                        u.setEmail(safeEmail);             // varchar(30) (nullable이면 생략 가능)
                        u.setProvider(Provider.GOOGLE);    // ENUM 기본값
                        u.setProviderUserId(actAs);        // NOT NULL
                        u.setMemberType(MemberType.JOB_SEEKER); // 프로젝트 enum 맞으면 사용
                        return userRepository.save(u);
                    });

            UserResponseDTO dto = UserResponseDTO.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .name(user.getName())
                    .email(user.getEmail())
                    .provider(user.getProvider() != null ? user.getProvider().name() : null)
                    .providerUserId(user.getProviderUserId())
                    .build();

            CustomOAuth2User principal = new CustomOAuth2User(dto);
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(principal, null, List.of());
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }
}
