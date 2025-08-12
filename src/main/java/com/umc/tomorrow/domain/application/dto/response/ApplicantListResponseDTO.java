/**
 * 관리자(또는 등록자)가 특정 공고(postId)에 지원한 '지원자 목록'을 조회하기 위해
 *
 * 작성자: 정여진
 * 생성일: 2025-07-26
 */
package com.umc.tomorrow.domain.application.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ApplicantListResponseDTO {
    private Long applicantId;
    private Long resumeId;
    private String userName;
    private String phoneNumber;
    private LocalDateTime applicationDate;
    private String status;
    private String content;
}

