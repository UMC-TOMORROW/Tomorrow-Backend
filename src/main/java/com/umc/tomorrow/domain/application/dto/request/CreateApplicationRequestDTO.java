/**
 * 일자리 지원 요청 DTO
 * 작성자: 이승주
 * 생성일: 2025-07-24
 */
package com.umc.tomorrow.domain.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "일자리 지원서 생성 요청 DTO")
public class CreateApplicationRequestDTO {

    @NotBlank(message = "application.content.notblank")
    @Size(max = 50,message = "application.content.size")
    private String content;

    @NotNull
    private Long jobId;

    private Long resumeId; //선택 입력
}