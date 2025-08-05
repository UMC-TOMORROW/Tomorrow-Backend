package com.umc.tomorrow.domain.job.controller.command;

import com.umc.tomorrow.domain.auth.security.CustomOAuth2User;
import com.umc.tomorrow.domain.job.dto.request.BusinessRequestDTO;
import com.umc.tomorrow.domain.job.dto.request.JobRequestDTO;
import com.umc.tomorrow.domain.job.dto.request.PersonalRequestDTO;
import com.umc.tomorrow.domain.job.dto.response.GetRecommendationListResponse;
import com.umc.tomorrow.domain.job.dto.response.JobCreateResponseDTO;
import com.umc.tomorrow.domain.job.dto.response.JobStepResponseDTO;
import com.umc.tomorrow.domain.job.dto.response.GetRecommendationResponse;
import com.umc.tomorrow.domain.job.enums.RegistrantType;
import com.umc.tomorrow.domain.job.service.command.JobCommandService;
import com.umc.tomorrow.domain.member.repository.UserRepository;
import com.umc.tomorrow.global.common.base.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Job", description = "일자리 관련 API")
@RestController
@RequestMapping("/api/v1/jobs")
@RequiredArgsConstructor
@Validated
public class JobCommandController {

    private final JobCommandService jobCommandService;
    private final UserRepository userRepository;

    /**
     * 일자리 정보 세션에 저장(POST)
     * @param user 인증된 사용자
     * @param requestDTO 일자리 데이터 요청 DTO
     * @param session 세션 사용
     * @return 성공 응답
     */
    @Operation(summary = "일자리 등록 폼 작성", description = "검증된 사용자가 일자리 폼을 작성합니다.")
    @PostMapping
    public ResponseEntity<BaseResponse<JobStepResponseDTO>> saveJobStepOne(
            @AuthenticationPrincipal CustomOAuth2User user,
            @Valid @RequestBody JobRequestDTO requestDTO,
            HttpSession session
    ) {
        Long userId = user.getUserDTO().getId();

        JobStepResponseDTO result = jobCommandService.saveInitialJobStep(userId, requestDTO, session);
        return ResponseEntity.ok(BaseResponse.onSuccess(result));
    }


    /**
     * 일자리, 개인 등록 사유 정보 db에 저장(POST)
     * @param user 인증된 사용자
     * @param requestDTO 일자리 데이터 요청 DTO
     * @param session 세션 사용
     * @return 성공 응답
     */
    // 개인 등록 API
    @Operation(summary = "개인 등록 사유", description = "일자리 등록 페이지에서 개인를 선택한 사람은 개인 등록 사유 페이지로 이동한다")
    @PostMapping("/personal_registrations")
    public ResponseEntity<BaseResponse<JobCreateResponseDTO>> savePersonalRegistration(
            @AuthenticationPrincipal CustomOAuth2User user,
            @Valid @RequestBody PersonalRequestDTO requestDTO,
            HttpSession session
    ) {
        Long userId = user.getUserDTO().getId();

        JobCreateResponseDTO result = jobCommandService.savePersonalRegistration(userId, requestDTO, session);
        return ResponseEntity.ok(BaseResponse.onSuccess(result));
    }


    /**
     * 세션이 넘어온 경우 바로 일자리 등록, 아닐 경우 사업자 등록 페이지로 이동(POST)
     * @param user 인증된 사용자
     * @param requestDTO 일자리 데이터 요청 DTO
     * @param session 세션 사용
     * @return 성공 응답
     */
    @Operation(summary = "사업자 등록 페이지 진입", description = "세션에 job이 있으면 일자리 등록까지 진행")
    @PostMapping("/business-verifications/register")
    public ResponseEntity<BaseResponse<JobStepResponseDTO>> registerBusiness(
            @AuthenticationPrincipal CustomOAuth2User user,
            @Valid @RequestBody BusinessRequestDTO requestDTO,
            HttpSession session
    ) {
        Long userId = user.getUserDTO().getId();

        // 세션에 job이 있다면사업자 등록 후 job 생성까지
        JobRequestDTO jobDTO = (JobRequestDTO) session.getAttribute("job_session");

        if (jobDTO != null) {
            JobCreateResponseDTO result = jobCommandService.registerBusinessAndCreateJob(userId, requestDTO, session);
            return ResponseEntity.ok(BaseResponse.onSuccess(
                    JobStepResponseDTO.builder()
                            .step("job_created")
                            .jobId(result.getJobId())
                            .registrantType(RegistrantType.BUSINESS)
                            .build()
            ));
        }

        // 세션에 job이 없다면 /business-verifications/only페이지로 이동(프론트에서 처리)
        jobCommandService.saveBusinessVerification(userId, requestDTO);
        return ResponseEntity.ok(BaseResponse.onSuccess(
                JobStepResponseDTO.builder()
                        .step("business-verifications/only")
                        .registrantType(RegistrantType.BUSINESS)
                        .build()
        ));
    }

    /**
     * 사업자 등록 요청 페이지(POST)
     * @param user 인증된 사용자
     * @param requestDTO 일자리 데이터 요청 DTO
     * @return 성공 응답
     */
    @Operation(summary = "사업자 정보만 등록", description = "유저가 사업자 정보만 등록하고 일자리는 등록하지 않는 경우")
    @PostMapping("/business-verifications/only")
    public ResponseEntity<BaseResponse<Void>> registerBusinessOnly(
            @AuthenticationPrincipal CustomOAuth2User user,
            @Valid @RequestBody BusinessRequestDTO requestDTO
    ) {
        Long userId = user.getUserDTO().getId();
        jobCommandService.saveBusinessVerification(userId, requestDTO);
        return ResponseEntity.ok(BaseResponse.onSuccess(null));

    }
    @GetMapping("recommendations")
    @Operation(summary = "내일 추천 게시글 목록 조회 (무한 스크롤)", description = "내일 추천 게시글 목록을 무한 스크롤 방식으로 조회합니다.")
    @ApiResponse(responseCode = "200", description = "내일 추천 목록 조회 성공")
    public ResponseEntity<BaseResponse<GetRecommendationListResponse>> getTomorrowRecommendations(
            @AuthenticationPrincipal CustomOAuth2User user,
            @Positive @RequestParam(required = false) Long cursor,
            @Positive @RequestParam(defaultValue = "8") int size
    ){
        Long userId = user.getUserDTO().getId();
        GetRecommendationListResponse result = jobCommandService.getTomorrowRecommendations(userId, cursor,size);
        return ResponseEntity.ok(BaseResponse.onSuccess(result));
    }
}
