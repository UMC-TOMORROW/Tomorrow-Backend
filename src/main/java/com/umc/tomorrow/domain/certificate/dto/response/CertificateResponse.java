/**
 * 자격증 관련 응답 DTO
 * 작성자: 이승주
 * 생성일: 2025-07-28
 */
package com.umc.tomorrow.domain.certificate.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "자격증 관련 응답 DTO")
public class CertificateResponse {

    @Schema(description = "자격증id", example = "1")
    private Long id;

    @Schema(description = "자격증 파일 주소", example = "https://chatgpt.com/c/6886e83b-7298-8321-a5d0-26ed42114e4a")
    private String fileUrl;

}
