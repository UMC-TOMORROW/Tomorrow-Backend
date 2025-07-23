package com.umc.tomorrow.domain.introduction.controller;

import com.umc.tomorrow.domain.auth.security.CustomOAuth2User;
import com.umc.tomorrow.domain.introduction.dto.request.IntroductionCreateRequestDTO;
import com.umc.tomorrow.domain.introduction.dto.request.IntroductionUpdateRequestDTO;
import com.umc.tomorrow.domain.introduction.dto.response.GetIntroductionResponseDTO;
import com.umc.tomorrow.domain.introduction.dto.response.IntroductionResponseDTO;
import com.umc.tomorrow.domain.introduction.service.command.IntroductionCommandService;
import com.umc.tomorrow.domain.job.dto.response.JobStepResponseDTO;
import com.umc.tomorrow.domain.job.service.command.JobCommandService;
import com.umc.tomorrow.domain.member.entity.User;
import com.umc.tomorrow.global.common.base.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Introduction", description = "자기소개 관련 API")
@RestController
@RequestMapping("/api/v1/resumes/{resumeId}/introductions")
@RequiredArgsConstructor
public class IntroductionCommandController {

    private final IntroductionCommandService introductionCommandService;

    /**
     * 이력서 자기소개 저장(POST)
     * @param resumeId 이력서 아이디
     * @param user 인증된 사용자
     * @param requestDTO 자기소개 추가 요청 DTO
     * @return 성공 응답
     */
    @Operation(summary = "이력서 자기소개 추가", description = "자기소개를 추가하는 api입니다.")
    @PostMapping
    public ResponseEntity<BaseResponse<IntroductionResponseDTO>> createIntroduction(
            @PathVariable Long resumeId,
            @AuthenticationPrincipal CustomOAuth2User user,
            @RequestBody @Valid IntroductionCreateRequestDTO requestDTO) {

        Long userId = user.getUserDTO().getId();

        IntroductionResponseDTO response = introductionCommandService.saveIntroduction(userId, resumeId, requestDTO);

        return ResponseEntity.ok(BaseResponse.onSuccess(response));
    }

    /**
     * 이력서 자기소개 조회(GET)
     * @param resumeId 이력서 아이디
     * @param user 인증된 사용자
     * @return 성공 응답
     */
    @Operation(summary = "이력서 자기소개 조회", description = "이력서에 등록된 자기소개를 조회하는 api입니다.")
    @GetMapping
    public ResponseEntity<BaseResponse<GetIntroductionResponseDTO>> getIntroduction(
            @PathVariable Long resumeId,
            @AuthenticationPrincipal CustomOAuth2User user) {

        Long userId = user.getUserDTO().getId();

        GetIntroductionResponseDTO response = introductionCommandService.getIntroduction(userId, resumeId);

        return ResponseEntity.ok(BaseResponse.onSuccess(response));
    }

    /**
     * 이력서 자기소개 수정(PUT)
     * @param resumeId 이력서 아이디
     * @param user 인증된 사용자
     * @param requestDTO 자기소개 수정 요청 DTO
     * @return 성공 응답
     */
    @Operation(summary = "이력서 자기소개 수정", description = "자기소개를 수정하는 api입니다.")
    @PutMapping
    public ResponseEntity<BaseResponse<IntroductionResponseDTO>> updateIntroduction(
            @PathVariable Long resumeId,
            @AuthenticationPrincipal CustomOAuth2User user,
            @RequestBody @Valid IntroductionUpdateRequestDTO requestDTO) {

        Long userId = user.getUserDTO().getId();

        IntroductionResponseDTO response = introductionCommandService.updateIntroduction(userId, resumeId, requestDTO);

        return ResponseEntity.ok(BaseResponse.onSuccess(response));
    }


}
