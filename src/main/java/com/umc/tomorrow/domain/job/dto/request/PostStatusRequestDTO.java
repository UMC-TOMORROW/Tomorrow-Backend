/**
 * 공고 상태(모집중,처리완료) 를 위한 dto
 * PATCH /api/v1/posts/{postId}/status
 * 작성자: 정여진
 * 생성일: 2025-08-05
 */
package com.umc.tomorrow.domain.job.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostStatusRequestDTO {
    private String status;
}
