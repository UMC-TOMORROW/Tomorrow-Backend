/**
 * DeactivateUserResponse (회원상태)
 - patch /api/v1/members/{memberId}/deactivate
 * 작성자: 정여진
 * 생성일: 2025-07-23
 */
package com.umc.tomorrow.domain.member.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class DeactivateUserResponse {
    private String status;
    private LocalDateTime deletedAt;
    private LocalDateTime recoverableUntil;
}