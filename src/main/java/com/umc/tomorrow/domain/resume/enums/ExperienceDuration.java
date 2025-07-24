package com.umc.tomorrow.domain.resume.enums;
import lombok.Getter;

@Getter
public enum ExperienceDuration {
    SHORT("단기"),
    UNDER_3M("3개월 이하"),
    UNDER_6M("6개월 이하"),
    M6_TO_1Y("6개월~1년"),
    Y1_TO_2Y("1년~2년"),
    Y2_TO_3Y("2년~3년"),
    OVER_3Y("3년 이상");

    private final String label;

    ExperienceDuration(String label) {
        this.label = label;
    }

    public static ExperienceDuration from(String label) {
        for (ExperienceDuration d : values()) {
            if (d.getLabel().equals(label)) return d;
        }
        throw new IllegalArgumentException("올바르지 않은 경력 기간입니다.");
    }
}
