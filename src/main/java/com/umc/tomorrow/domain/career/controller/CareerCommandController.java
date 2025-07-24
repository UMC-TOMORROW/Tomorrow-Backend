package com.umc.tomorrow.domain.career.controller;

import com.umc.tomorrow.domain.auth.security.CustomOAuth2User;
import com.umc.tomorrow.domain.career.dto.request.CareerCreateRequestDTO;
import com.umc.tomorrow.domain.career.dto.response.CareerCreateResponseDTO;
import com.umc.tomorrow.domain.career.service.conmmand.CareerCommandService;
import com.umc.tomorrow.global.common.base.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Career", description = "경력 관련 API")
@RestController
@RequestMapping("/api/v1/resumes/{resumeId}/experiences")
@RequiredArgsConstructor
public class CareerCommandController {

    private final CareerCommandService careerCommandService;

    /**
     * 이력서 경력 저장(POST)
     * @param resumeId 이력서 아이디
     * @param user 인증된 사용자
     * @param requestDTO 경력 추가 요청 DTO
     * @return 성공 응답
     */
    @Operation(summary = "이력서 경력 추가", description = "경력을 추가하는 api입니다.")
    @PostMapping
    public ResponseEntity<BaseResponse<CareerCreateResponseDTO>> createCareer(
            @PathVariable Long resumeId,
            @AuthenticationPrincipal CustomOAuth2User user,
            @RequestBody @Valid CareerCreateRequestDTO requestDTO) {

        Long userId = user.getUserDTO().getId();

        CareerCreateResponseDTO response = careerCommandService.saveCareer(userId, resumeId, requestDTO);

        return ResponseEntity.ok(BaseResponse.onSuccess(response));
    }
}
