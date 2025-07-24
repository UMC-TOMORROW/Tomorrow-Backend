/**
 * 커리어톡 게시글 생성 응답 DTO
 * 커리어톡 게시글 생성 API의 응답 구조 정의
 * 작성자: 이승주
 * 생성일: 2025-07-10
 * 수정일: 2025-07-24
 */
package com.umc.tomorrow.domain.careertalk.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "커리어톡 게시글 응답 DTO") //생성,수정,삭제 요청 시 응답 Dto
public class CareertalkResponseDto {

    @Schema(description = "커리어톡 게시글 ID", example = "1")
    private Long id;

}
