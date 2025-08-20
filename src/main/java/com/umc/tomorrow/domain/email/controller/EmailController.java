/**
 * 이메일 컨트롤러
 * 작성자: 이승주
 * 작성일: 2025-07-28
 */
package com.umc.tomorrow.domain.email.controller;

import com.umc.tomorrow.domain.auth.security.CustomOAuth2User;
import com.umc.tomorrow.domain.email.dto.request.EmailRequestDTO;
import com.umc.tomorrow.domain.email.dto.response.EmailResponseDTO;
import com.umc.tomorrow.domain.email.service.EmailService;
import com.umc.tomorrow.global.common.base.BaseResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "EMAIL", description = "이메일 관련 API")
@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/send")
    public ResponseEntity<BaseResponse> send(
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User,
            @Valid @RequestBody EmailRequestDTO dto) {

        Long userId = customOAuth2User.getUserResponseDTO().getId();

        // 비동기 실행 (반환값 받지 않음)
        emailService.sendEmail(userId, dto);

        // 202 Accepted로 즉시 응답
        return ResponseEntity.accepted()
                .body(BaseResponse.onSuccess(Map.of("accepted", true)));
    }

}
