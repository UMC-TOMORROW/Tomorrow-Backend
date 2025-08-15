package com.umc.tomorrow.domain.job.controller.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.umc.tomorrow.domain.auth.security.CustomOAuth2User;
import com.umc.tomorrow.domain.job.dto.request.BusinessRequestDTO;
import com.umc.tomorrow.domain.job.dto.request.JobRequestDTO;
import com.umc.tomorrow.domain.job.dto.request.PersonalRequestDTO;
import com.umc.tomorrow.domain.job.dto.response.GetRecommendationListResponse;
import com.umc.tomorrow.domain.job.dto.request.PostStatusRequestDTO;
import com.umc.tomorrow.domain.job.dto.response.JobCreateResponseDTO;
import com.umc.tomorrow.domain.job.dto.response.JobStepResponseDTO;
import com.umc.tomorrow.domain.job.enums.RegistrantType;
import com.umc.tomorrow.domain.job.service.command.JobCommandService;
import com.umc.tomorrow.domain.member.repository.UserRepository;
import com.umc.tomorrow.global.common.base.BaseResponse;
import com.umc.tomorrow.global.common.exception.RestApiException;
import com.umc.tomorrow.global.common.exception.code.GlobalErrorStatus;
import com.umc.tomorrow.global.infrastructure.s3.S3Uploader;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Slf4j
@Tag(name = "Job", description = "일자리 관련 API")
@RestController
@RequestMapping("/api/v1/jobs")
@RequiredArgsConstructor
@Validated
public class JobCommandController {

    private final JobCommandService jobCommandService;
    private final UserRepository userRepository;
    private final Validator validator;
    private final S3Uploader s3Uploader;


    /**
     * 일자리 정보 세션에 저장(POST)
     * @param user 인증된 사용자
     * @param session 세션 사용
     * @return 성공 응답
     */
    @Operation(summary = "일자리 등록 폼 작성", description = "검증된 사용자가 일자리 폼을 작성합니다.",security = {@SecurityRequirement(name = "bearer-key")})
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<BaseResponse<JobStepResponseDTO>> saveJobStepOne(
            @AuthenticationPrincipal CustomOAuth2User user,
            @RequestPart("jobRequest") String jobRequestJson,
            @RequestPart(value = "image", required = false) MultipartFile image,
            HttpSession session
    ) {
        Long userId = user.getUserResponseDTO().getId();

        // JSON 파싱
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        JobRequestDTO requestDTO;
        try {
            requestDTO = objectMapper.readValue(jobRequestJson, JobRequestDTO.class);
        } catch (Exception e) {
            throw new RestApiException(GlobalErrorStatus._BAD_REQUEST);
        }

        // 유효성 검증f
        Set<ConstraintViolation<JobRequestDTO>> violations = validator.validate(requestDTO);
        if (!violations.isEmpty()) {
            String errorMessage = violations.iterator().next().getMessage();
            throw new RestApiException(GlobalErrorStatus._VALIDATION_ERROR, errorMessage);
        }


        // 이미지 업로드
        if (image != null && !image.isEmpty()) {
            String imageUrl = s3Uploader.upload(image, "job-images");
            requestDTO.setJobImageUrl(imageUrl);
        }

        // 서비스 호출
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
        Long userId = user.getUserResponseDTO().getId();

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
        Long userId = user.getUserResponseDTO().getId();

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
        Long userId = user.getUserResponseDTO().getId();
        jobCommandService.saveBusinessVerification(userId, requestDTO);
        return ResponseEntity.ok(BaseResponse.onSuccess(null));

    }

    /**
     * 공고 모집완료/모집전 처리
     * @param jobId 변경할 모집글 ID
     * @param requestDTO 상태 변경 요청 DTO
     * @param customOAuth2User 인증된 사용자
     * @return 상태 변경 결과
     */
    @PatchMapping("/{jobId}/status")
    public ResponseEntity<BaseResponse<Void>> updateJobStatus(
            @PathVariable Long jobId,
            @Valid @RequestBody PostStatusRequestDTO requestDTO,
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User) {
        Long userId = customOAuth2User.getUserResponseDTO().getId();
        jobCommandService.updateJobStatus(userId, jobId, requestDTO.getStatus());

        return ResponseEntity.ok(BaseResponse.onSuccess(null));
    }
}
