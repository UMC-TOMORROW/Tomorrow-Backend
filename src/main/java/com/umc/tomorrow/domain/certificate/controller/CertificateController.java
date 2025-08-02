/**
 * 쟈격증 API 컨트롤러
 * -/api/v1/resumes/{resumeId}/certificate
 * 작성자: 이승주
 * 작성일: 2025-07-28
 */
package com.umc.tomorrow.domain.certificate.controller;

import com.umc.tomorrow.domain.certificate.dto.response.CertificateResponse;
import com.umc.tomorrow.domain.certificate.service.command.CertificateCommandService;
import com.umc.tomorrow.global.common.base.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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

    @PostMapping(value = "{resumeId}/certificates", consumes = "multipart/form-data")
    @Operation(summary = "이력서 자격증 업로드", description = "이력서에 자격증을 업로드합니다.")
    @ApiResponse(responseCode = "201", description = "자격증 업로드 성공")
    public ResponseEntity<BaseResponse<CertificateResponse>> uploadCertificate(
            @PathVariable Long resumeId,
            @RequestParam("file") MultipartFile file
    ){
        CertificateResponse response = certificateCommandService.uploadCertificate(resumeId, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponse.onSuccessCreate(response));
    }

    @DeleteMapping("{resumeId}/certificates/{certificateId}")
    @Operation(summary = "이력서 자격증 삭제", description = "이력서에서 자격증을 삭제합니다.")
    @ApiResponse(responseCode = "200", description = "자격증 삭제 성공")
    public ResponseEntity<BaseResponse<CertificateResponse>> deleteCertificate(
            @PathVariable Long resumeId,
            @PathVariable Long certificateId
    ) {
        CertificateResponse response = certificateCommandService.deleteCertificate(certificateId);
        return ResponseEntity.ok(BaseResponse.onSuccessDelete(response));
    }
}
