/**
 * 지원자 상태 enum
 * 작성자: 정여진
 * 생성일: 2025-07-23
 */
package com.umc.tomorrow.domain.application.enums;

public enum ApplicationStatus {
    ACCEPTED("합격"),
    REJECTED("불합격");

    private final String label;

    ApplicationStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static ApplicationStatus from(String label) {
        return switch (label) {
            case "합격" -> ACCEPTED;
            case "불합격" -> REJECTED;
            default -> throw new IllegalArgumentException("지원서 상태는 '합격' 또는 '불합격'이어야 합니다.");
        };
    }
}