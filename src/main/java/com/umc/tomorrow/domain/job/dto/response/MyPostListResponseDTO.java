/**
 * 내가 등록한 공고 리스트 응답 DTO
 * 작성자: 정여진
 * 생성일: 2025-07-25
 *
 * 설명:
 * - 사용자가 등록한 공고(Post) 목록을 조회할 때 사용됩니다.
 * - 내부에 MyPostResponseDto 리스트를 포함합니다.
 */
package com.umc.tomorrow.domain.job.dto.response;

import com.umc.tomorrow.domain.job.dto.request.MyPostResponseDTO;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MyPostListResponseDTO {
    private List<MyPostResponseDTO> posts;
}