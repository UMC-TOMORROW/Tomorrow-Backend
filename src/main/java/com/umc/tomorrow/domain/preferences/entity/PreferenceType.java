/**
 * 희망 조건 ENUM
 * - 각 값은 사용자가 선택할 수 있는 일자리 희망 조건을 의미합니다.
 * - description 필드로 한글 설명 제공
 * 작성자: 정여진
 * 생성일: 2025-07-17
 */
package com.umc.tomorrow.domain.preferences.entity;

public enum PreferenceType {
    SIT("앉아서 근무 중심"),
    STAND("서서 근무 중심"),
    DELIVERY("물건 운반"),
    PHYSICAL("신체 활동 중심"),
    HUMAN("사람 응대 중심");

    private final String description;

    PreferenceType(String description) {
        this.description = description;
    }

    /**
     * 한글 설명 반환
     */
    public String getDescription() {
        return description;
    }
} 