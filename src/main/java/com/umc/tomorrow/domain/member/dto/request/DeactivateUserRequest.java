/**
 * DeactivateUserRequest
 * - patch /api/v1/users/{userId}/deactivate
 * 작성자: 정여진
 * body : {
 *   "status": "DELETED"
 * }
 * 생성일: 2025-07-23
 */
package com.umc.tomorrow.domain.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class DeactivateUserRequest {
    @NotBlank(message = "{user.status.notnull}")
    private String status; // "DELETED"
}
