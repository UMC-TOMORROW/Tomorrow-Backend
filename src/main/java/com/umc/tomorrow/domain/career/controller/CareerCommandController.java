package com.umc.tomorrow.domain.career.controller;

import com.umc.tomorrow.domain.auth.security.CustomOAuth2User;
import com.umc.tomorrow.domain.career.dto.request.CareerCreateRequestDTO;
import com.umc.tomorrow.domain.career.dto.request.CareerUpdateRequestDTO;
import com.umc.tomorrow.domain.career.dto.response.CareerCreateResponseDTO;
import com.umc.tomorrow.domain.career.dto.response.CareerGetResponseDTO;
import com.umc.tomorrow.domain.career.service.conmmand.CareerCommandService;
import com.umc.tomorrow.domain.career.service.query.CareerQueryService;
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
    private final CareerQueryService careerQueryService;

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

    /**
     * 이력서 경력 수정(PUT)
     * @param resumeId 이력서 아이디
     * @param careerId 경력 아이디
     * @param user 인증된 사용자
     * @param requestDTO 경력 추가 요청 DTO
     * @return 성공 응답
     */
    @PutMapping("/{careerId}")
    @Operation(summary = "이력서 경력 수정", description = "경력 정보를 수정하는 API입니다.")
    public ResponseEntity<BaseResponse<CareerCreateResponseDTO>> updateCareer(
            @PathVariable Long resumeId,
            @PathVariable Long careerId,
            @AuthenticationPrincipal CustomOAuth2User user,
            @RequestBody @Valid CareerUpdateRequestDTO requestDTO) {

        Long userId = user.getUserDTO().getId();

        CareerCreateResponseDTO response = careerCommandService.updateCareer(userId, resumeId, careerId, requestDTO);

        return ResponseEntity.ok(BaseResponse.onSuccess(response));
    }

    /**
     * 이력서 경력 조회(GET)
     * @param resumeId 이력서 아이디
     * @param careerId 경력 아이디
     * @param user 인증된 사용자
     * @return 성공 응답
     */
    @GetMapping("/{careerId}")
    @Operation(summary = "이력서 경력 조회", description = "경력 정보를 조회하는 API입니다.")
    public ResponseEntity<BaseResponse<CareerGetResponseDTO>> getCareer(
            @PathVariable Long resumeId,
            @PathVariable Long careerId,
            @AuthenticationPrincipal CustomOAuth2User user) {

        Long userId = user.getUserDTO().getId();

        CareerGetResponseDTO response = careerQueryService.getCareer(userId, resumeId, careerId);

        return ResponseEntity.ok(BaseResponse.onSuccess(response));
    }

    /**
     * 이력서 경력 삭제(DELETE)
     * @param resumeId 이력서 아이디
     * @param careerId 경력 아이디
     * @param user 인증된 사용자
     */
    @DeleteMapping("/{careerId}")
    public ResponseEntity<BaseResponse<String>> deleteCareer(
            @PathVariable Long resumeId,
            @PathVariable Long careerId,
            @AuthenticationPrincipal CustomOAuth2User user) {

        Long userId = user.getUserDTO().getId();

        careerCommandService.deleteCareer(userId, resumeId, careerId);

        return ResponseEntity.ok(BaseResponse.onSuccess("경력이 성공적으로 삭제되었습니다."));
    }
}
