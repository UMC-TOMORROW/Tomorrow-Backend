/**
 * 공고 상태 Enum (모집중, 모집완료)
 * 작성자: 정여진
 * 생성일: 2025-07-25
 */
package com.umc.tomorrow.domain.job.enums;

public enum PostStatus {
    OPEN,   // 모집중
    CLOSED; // 모집완료

    public static PostStatus from(String status) {
        return switch (status.toLowerCase()) {
            case "open" -> OPEN;
            case "closed" -> CLOSED;
            default -> throw new IllegalArgumentException("Invalid post status: " + status);
        };
    }
}