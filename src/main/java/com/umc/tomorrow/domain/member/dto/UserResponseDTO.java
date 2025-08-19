/**
 * 회원 정보 응답 DTO
 * - User Entity와 1:1 매핑되는 응답 객체
 * - Validation 없음 (응답용이므로)
 *
 * 작성자: 정여진
 * 생성일: 2025-07-11
 * 수정일: 2025-08-15
 */
package com.umc.tomorrow.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.umc.tomorrow.domain.member.enums.Gender;
import com.umc.tomorrow.global.common.base.BaseEntity;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDTO extends BaseEntity {
    private Long id; // 회원 고유 ID
    private String role;
    private String username; // 사용자명(로그인 ID 또는 소셜 ID)
    private String email; // 이메일
    private String name; // 이름
    private Gender gender; // 성별
    private String phoneNumber; // 휴대폰 번호
    private String address; // 주소
    private String status; // 상태 (ACTIVE, INACTIVE)
    private String inactiveAt; // 비활성화 일시
    private Boolean isOnboarded; // 온보딩 여부
    private String provider; // 소셜 제공자 (KAKAO, NAVER, GOOGLE)
    private String providerUserId; // 소셜 제공자 ID
    private Long resumeId; // 이력서 ID
}
