/**
 * UserStatus (회원상태)
 * 작성자: 정여진
 * 생성일: 2025-07-23
 */
package com.umc.tomorrow.domain.member.enums;

public enum UserStatus {
    ACTIVE,
    DELETED, // 탈퇴 상태 (14일 내 복구 가능)
    INACTIVE // 휴면 상태 또는 비활성화
}
