package com.umc.tomorrow.domain.job.enums;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum JobCategory {
    SERVING("서빙"),
    KITCHEN_HELP("주방 보조"),
    CAFE_BAKERY("카페/베이커리"),
    TUTORING("과외/교육"),
    ERRAND("심부름"),
    PROMOTION("홍보"),
    SENIOR_CARE("노인 돌봄"),
    CHILD_CARE("아이 돌봄"),
    BEAUTY("미용"),
    OFFICE_HELP("사무 보조"),
    ETC("기타");

    private final String description;
}
