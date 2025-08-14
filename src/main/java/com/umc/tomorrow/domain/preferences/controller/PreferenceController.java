/**
 * PreferenceController
 * - 희망 조건 저장/수정 API 컨트롤러
 * 
 * 작성자: 정여진
 * 생성일: 2025-07-17
 */
package com.umc.tomorrow.domain.preferences.controller;

import com.umc.tomorrow.domain.preferences.dto.PreferencesDTO;
import com.umc.tomorrow.domain.preferences.service.PreferenceService;
import com.umc.tomorrow.domain.auth.security.CustomOAuth2User;
import com.umc.tomorrow.domain.member.entity.User;
import com.umc.tomorrow.global.common.base.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.Map;


@Tag(name = "preference-controller", description = "희망 조건 관련 API")
@RestController
@RequestMapping("/api/v1/preferences")
public class PreferenceController {
    private final PreferenceService preferenceService;

    @Autowired
    public PreferenceController(PreferenceService preferenceService) {
        this.preferenceService = preferenceService;
    }

    /**
     * 희망 조건 수정 (PATCH)
     * @param user 인증된 사용자
     * @param dto 희망 조건 목록
     * @return 저장 결과
     */
    @Operation(summary = "희망 조건 수정", description = "사용자의 희망 조건을 수정합니다.")
    @PatchMapping
    public ResponseEntity<BaseResponse> updatePreferences(
            @AuthenticationPrincipal CustomOAuth2User user,
            @Valid @RequestBody PreferencesDTO dto) {
        // 실제 DB에 희망 조건 업데이트
        Long userId = user.getUserDTO().getId();
        preferenceService.updatePreferences(userId, dto);
        return ResponseEntity.ok(
            BaseResponse.onSuccess(Map.of("saved", true))
        );
    }

    /**
     * 희망 조건 저장 (POST)
     * @param user 인증된 사용자
     * @param dto 희망 조건 목록
     * @return 저장 결과
     */
    @Operation(summary = "희망 조건 저장", description = "사용자의 희망 조건을 최초로 저장합니다.")
    @PostMapping
    public ResponseEntity<BaseResponse> savePreferences(
            @AuthenticationPrincipal CustomOAuth2User user,
            @RequestBody PreferencesDTO dto) {
        Long userId = user.getUserDTO().getId();
        preferenceService.savePreferences(userId, dto);
        return ResponseEntity.ok(
            BaseResponse.onSuccess(Map.of("saved", true))
        );
    }
} 