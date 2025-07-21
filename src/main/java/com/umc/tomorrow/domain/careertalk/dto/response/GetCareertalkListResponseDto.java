/**
 * 커리어톡 게시글 목록 조회 응답 DTO
 * 커리어톡 게시글 목록 조회 API의 응답 구조 정의
 * 작성자: 이승주
 * 생성일: 2025-07-10
 * 수정일: 2025-07-20
 */
package com.umc.tomorrow.domain.careertalk.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.List;

@Getter
@Builder
@Schema(description = "커리어톡 게시글 목록 조회 응답 DTO")
public class GetCareertalkListResponseDto {

    @Schema(description = "게시글 목록")
    private List<GetCareertalkResponseDto> careertalkList;

    @Schema(description = "다음 게시물이 있는지 여부")
    private boolean hasNext;

}
