/**
 * MyPostResponseDto
 * 내가 올린 직업 공고 확인하기 위한 DTO
 *
 * 작성자: 정여진
 * 생성일: 2025-07-25
 */
package com.umc.tomorrow.domain.job.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@Schema(description = "내 공고 조회 응답 DTO")
public class MyPostResponseDTO {

    @Schema(description = "공고 ID", example = "301")
    private Long postId;

    @Schema(description = "공고 제목", example = "도서 정리 및 대출 보조")
    private String title;

    @Schema(description = "공고 상태", example = "모집중")
    private String status; // 모집중 / 모집완료

    @Schema(description = "공고 날짜", example = "2025-06-01")
    private LocalDate date;

    @Schema(description = "지역", example = "서울 서초구")
    private String location;

    @Schema(description = "작업 태그 목록", example = "[\"기본 물건 운반\", \"손이나 팔을 자주 사용하는 작업\"]")
    private List<String> tags;
}