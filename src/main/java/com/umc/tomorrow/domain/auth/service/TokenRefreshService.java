package com.umc.tomorrow.domain.auth.service;

import com.umc.tomorrow.domain.auth.dto.TokenRefreshRequest;
import com.umc.tomorrow.domain.auth.dto.TokenRefreshResponse;
import com.umc.tomorrow.domain.auth.jwt.JWTUtil;
import com.umc.tomorrow.domain.member.entity.User;
import com.umc.tomorrow.domain.member.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenRefreshService {

    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;

    public TokenRefreshResponse refreshToken(TokenRefreshRequest request) {
        try {
            // Refresh Token 검증
            if (jwtUtil.isExpired(request.getRefreshToken())) {
                return TokenRefreshResponse.builder()
                        .message("Refresh Token이 만료되었습니다. 다시 로그인해주세요.")
                        .build();
            }

            // Refresh Token에서 사용자 정보 추출
            Long userId = jwtUtil.getId(request.getRefreshToken());
            String username = jwtUtil.getUsernameFromRefreshToken(request.getRefreshToken());

            // 사용자 존재 여부 확인
            User user = userRepository.findById(userId).orElse(null);
            if (user == null || !user.getRefreshToken().equals(request.getRefreshToken())) {
                return TokenRefreshResponse.builder()
                        .message("유효하지 않은 Refresh Token입니다.")
                        .build();
            }

            // 새로운 Access Token 생성
            long accessTokenExpiredMs = 60L * 60 * 60 * 1000; // 60시간
            String newAccessToken = jwtUtil.createJwt(
                    user.getId(),
                    user.getName(),
                    user.getUsername(),
                    (user.getStatus() != null ? user.getStatus().name() : null),
                    accessTokenExpiredMs
            );

            // 새로운 Refresh Token 생성
            long refreshTokenExpiredMs = 60L * 60 * 24 * 14 * 1000; // 2주
            String newRefreshToken = jwtUtil.createRefreshToken(userId, username, refreshTokenExpiredMs);

            // DB에 새로운 Refresh Token 저장
            user.setRefreshToken(newRefreshToken);
            userRepository.save(user);

            return TokenRefreshResponse.builder()
                    .accessToken(newAccessToken)
                    .refreshToken(newRefreshToken)
                    .message("토큰이 성공적으로 갱신되었습니다.")
                    .build();

        } catch (Exception e) {
            return TokenRefreshResponse.builder()
                    .message("토큰 갱신 중 오류가 발생했습니다: " + e.getMessage())
                    .build();
        }
    }
}
