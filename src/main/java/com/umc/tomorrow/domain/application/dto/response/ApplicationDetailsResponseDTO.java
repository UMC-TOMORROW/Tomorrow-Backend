/**
 * 개별 지원자 이력서 조회 응답 DTO
 *
 * 작성자: 정여진
 * 생성일: 2025-07-24
 */
package com.umc.tomorrow.domain.application.dto.response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationDetailsResponseDTO {
    private Long applicantId;
    private String status; // 현재 지원 상태

    private UserProfileDTO userProfile;
    private ResumeInfoDTO resumeInfo;

    // 사용자 프로필 일부 가져옴
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserProfileDTO {
        private String userName;
        private String email;
        private String phoneNumber;
        private String profileImageUrl;
    }

    // 자기소개 정보 가져옴
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResumeInfoDTO {
        private String resumeContent;
        private String portfolioUrl;
        private List<ExperienceDTO> experiences;
        private List<CareerDTO> careers;
        private List<CertificationDTO> certifications;
    }

    // 경험
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExperienceDTO {
        private Long id;
        private String company;     // 일한 곳
        private String position;    // 했던 일
        private String duration;    // "1년~2년" 등 라벨값
        private String description; // 상세 설명
    }

    // 경력
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CareerDTO {
        private Long id;
        private String company;     // 일한 곳
        private String position;    // 했던 일
        private String duration;    // "1년~2년" 등 라벨값
        private String description; // 상세 설명
    }

    // 자격증
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CertificationDTO {
        private String certificationName;
        private String fileUrl;
        private String filename;
    }
}
