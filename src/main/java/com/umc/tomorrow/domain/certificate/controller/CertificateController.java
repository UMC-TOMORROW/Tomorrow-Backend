/**
 * 쟈격증 API 컨트롤러
 * -/api/v1/resumes/{resumeId}/certificate
 * 작성자: 이승주
 * 작성일: 2025-07-28
 */
package com.umc.tomorrow.domain.certificate.controller;

import com.umc.tomorrow.domain.auth.security.CustomOAuth2User;
import com.umc.tomorrow.domain.certificate.dto.response.CertificateResponse;
import com.umc.tomorrow.domain.certificate.service.command.CertificateCommandService;
import com.umc.tomorrow.domain.certificate.service.query.CertificateQueryService;
import com.umc.tomorrow.global.common.base.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Certificate", description = "자격증 관련 API")
@RestController
@RequestMapping("/api/v1/resumes")
@RequiredArgsConstructor
public class CertificateController {

    private final CertificateCommandService certificateCommandService;
    private final CertificateQueryService certificateQueryService;

    @PostMapping(value = "{resumeId}/certificates", consumes = "multipart/form-data")
    @Operation(summary = "이력서 자격증 업로드", description = "이력서에 자격증을 업로드합니다.")
    @ApiResponse(responseCode = "201", description = "자격증 업로드 성공")
    public ResponseEntity<BaseResponse<CertificateResponse>> uploadCertificate(
            @AuthenticationPrincipal CustomOAuth2User user,
            @PathVariable Long resumeId,
            @RequestParam("file") MultipartFile file
    ){
        CertificateResponse response = certificateCommandService.uploadCertificate(user.getUserDTO().getId(), resumeId, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponse.onSuccessCreate(response));
    }

    @DeleteMapping("{resumeId}/certificates/{certificateId}")
    @Operation(summary = "이력서 자격증 삭제", description = "이력서에서 자격증을 삭제합니다.")
    @ApiResponse(responseCode = "200", description = "자격증 삭제 성공")
    public ResponseEntity<BaseResponse<CertificateResponse>> deleteCertificate(
            @AuthenticationPrincipal CustomOAuth2User user,
            @PathVariable Long resumeId,
            @PathVariable Long certificateId
    ) {
        CertificateResponse response = certificateCommandService.deleteCertificate(user.getUserDTO().getId(),certificateId);
        return ResponseEntity.ok(BaseResponse.onSuccessDelete(response));
    }

    @GetMapping("{resumeId}/certificates")
    @Operation(summary = "이력서 자격증 목록 조회", description = "이력서에 등록된 자격증 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "자격증 조회 성공")
    public ResponseEntity<BaseResponse<List<CertificateResponse>>> getCertificates(
            @PathVariable Long resumeId
    ){
        List<CertificateResponse> certificates = certificateQueryService.getCertificatesByResumeId(resumeId);
        return ResponseEntity.ok(BaseResponse.onSuccess(certificates));
    }
}
