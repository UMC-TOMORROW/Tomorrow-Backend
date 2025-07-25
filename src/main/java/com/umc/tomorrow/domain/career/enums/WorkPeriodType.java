package com.umc.tomorrow.domain.career.enums;

public enum WorkPeriodType {
    SHORT_TERM("단기"),
    LESS_THAN_3_MONTHS("3개월 이하"),
    LESS_THAN_6_MONTHS("6개월 이하"),
    SIX_TO_TWELVE_MONTHS("6개월~1년 이하"),
    ONE_TO_TWO_YEARS("1~2년"),
    TWO_TO_THREE_YEARS("2~3년"),
    MORE_THAN_THREE_YEARS("3년 이상");

    private final String label;

    WorkPeriodType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
