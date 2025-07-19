/**
 * 이력서 저장 API 컨트롤러
 * - /api/v1/resumes POST
 * 작성자: 정여진
 * 생성일: 2025-07-20
 */
package com.umc.tomorrow.domain.resume.controller;

import com.umc.tomorrow.domain.resume.dto.request.ResumeSaveRequestDTO;
import com.umc.tomorrow.domain.resume.entity.Resume;
import com.umc.tomorrow.domain.resume.service.ResumeSaveService;
import com.umc.tomorrow.global.common.base.BaseResponse;
import com.umc.tomorrow.domain.auth.security.CustomOAuth2User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@Tag(name = "resume-save-controller", description = "이력서 저장 API")
@RestController
@RequestMapping("/api/v1/resumes")
public class ResumeSaveController {
    private final ResumeSaveService resumeSaveService;

    public ResumeSaveController(ResumeSaveService resumeSaveService) {
        this.resumeSaveService = resumeSaveService;
    }

    /**
     * 이력서 저장 (POST)
     * @param user 인증된 사용자
     * @param dto 이력서 저장 요청 DTO
     * @return 저장 결과(이력서 ID)
     */
    @Operation(summary = "이력서 저장", description = "사용자의 이력서를 저장합니다.")
    @PostMapping
    public ResponseEntity<BaseResponse<Map<String, Long>>> saveResume(
            @AuthenticationPrincipal CustomOAuth2User user,
            @RequestBody ResumeSaveRequestDTO dto) {
        Long userId = user.getUserDTO().getId();
        Resume saved = resumeSaveService.saveResume(userId, dto);
        return ResponseEntity.ok(
            BaseResponse.onSuccess(Map.of("resumeId", saved.getId()))
        );
    }
} 