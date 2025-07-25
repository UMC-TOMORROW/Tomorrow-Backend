/**
 * ApplicationConverter
 * Application Entity <-> DTO 변환 및 매핑
 * 작성자: 정여진
 * 생성일: 2025-07-23
 */
package com.umc.tomorrow.domain.application.converter;

import com.umc.tomorrow.domain.application.dto.request.UpdateApplicationStatusRequestDTO;
import com.umc.tomorrow.domain.application.dto.response.ApplicationStatusListResponseDTO;
import com.umc.tomorrow.domain.application.dto.response.ApplicationDetailsResponseDTO;
import com.umc.tomorrow.domain.application.dto.response.UpdateApplicationStatusResponseDTO;
import com.umc.tomorrow.domain.application.entity.Application;
import com.umc.tomorrow.domain.application.enums.ApplicationStatus;
import com.umc.tomorrow.domain.career.entity.Career;
import com.umc.tomorrow.domain.member.entity.User;
import com.umc.tomorrow.domain.resume.entity.Certificate;
import com.umc.tomorrow.domain.resume.entity.Experience;
import com.umc.tomorrow.domain.resume.entity.Resume;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ApplicationConverter {

    /**
     * 요청 DTO → Enum
     */
    public static ApplicationStatus toEnum(UpdateApplicationStatusRequestDTO dto) {
        return ApplicationStatus.from(String.valueOf(dto.getStatus()));
    }

    /**
     * Enum + ID → 응답 DTO
     */
    public static UpdateApplicationStatusResponseDTO toResponse(Long applicationId, ApplicationStatus status) {
        return UpdateApplicationStatusResponseDTO.builder()
                .applicationId(applicationId)
                .status(status)
                .build();
    }

    /**
    * Application 엔티티를 지원 현황 리스트 응답 DTO로 변환하는 메서드
    *
    * @param application 변환 대상 Application 엔티티
    * @return ApplicationStatusListResponseDTO - 지원 현황에 필요한 요약 정보
    */
    public static ApplicationStatusListResponseDTO toStatusListDTO(Application application) {
        return ApplicationStatusListResponseDTO.builder()
                .postTitle(application.getJob().getTitle())
                .company(application.getJob().getCompanyName()) // Job에 필드가 있어야 함
                .date(application.getAppliedAt().toLocalDate().toString()) // LocalDateTime → yyyy-MM-dd
                .status(application.getStatus() == null ? ApplicationStatus.valueOf("미정") : application.getStatus())
                .build();
    }

    // 엔티티들을 ApplicantResumeResponseDTO로 변환하는 메서드
    public static ApplicationDetailsResponseDTO toApplicantResumeResponseDTO(
            Application application,
            User user,
            Resume resume
    ) {
        String statusText = application.getStatus() == null
                ? "미정"
                : application.getStatus().getLabel();

        // 1. 사용자 프로필 정보 DTO 생성
        ApplicationDetailsResponseDTO.UserProfileDTO userProfile = ApplicationDetailsResponseDTO.UserProfileDTO.builder()
                .userName(user.getName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .build();

        // 2. 이력서 상세 정보 DTO 생성
        ApplicationDetailsResponseDTO.ResumeInfoDTO resumeInfo = null;
        if (resume != null) {
            List<ApplicationDetailsResponseDTO.ExperienceDTO> experienceDTOS =
                    Optional.ofNullable(resume.getCareer()) //
                            .orElse(Collections.emptyList())
                            .stream()
                            .map(ApplicationConverter::toExperienceDTO)
                            .collect(Collectors.toList());

            List<ApplicationDetailsResponseDTO.CertificationDTO> certificationDTOS = Optional.ofNullable(resume.getCertificates())
                    .orElse(Collections.emptyList())
                    .stream()
                    .map(ApplicationConverter::toCertificationDTO)
                    .collect(Collectors.toList());

            resumeInfo = ApplicationDetailsResponseDTO.ResumeInfoDTO.builder()
                    //.resumeContent(resume.getIntroduction())// Resume 엔티티의 introduction 필드사용
                    .resumeContent(resume.getIntroduction().getContent())
                    .experiences(experienceDTOS)
                    .certifications(certificationDTOS)
                    .build();
        }

        // 3. 최종 응답 DTO 생성 및 반환
        return ApplicationDetailsResponseDTO.builder()
                .applicantId(user.getId())
                .status(statusText)
                .userProfile(userProfile)
                .resumeInfo(resumeInfo)
                .build();
    }

    // Experience 엔티티를 ExperienceDTO로 변환
    private static ApplicationDetailsResponseDTO.ExperienceDTO toExperienceDTO(Experience experience) {
        return ApplicationDetailsResponseDTO.ExperienceDTO.builder()
                .id(experience.getId())
                .company(experience.getPlace())
                .position(experience.getTask())
                .duration(experience.getDuration()) // Enum → Label
                .description(String.valueOf(experience))
                .build();
    }

    // Certificate 엔티티를 CertificationDTO로 변환
    private static ApplicationDetailsResponseDTO.CertificationDTO toCertificationDTO(Certificate certificate) {
        return ApplicationDetailsResponseDTO.CertificationDTO.builder()
                .build();
    }

    private static ApplicationDetailsResponseDTO.ExperienceDTO toExperienceDTO(Career career) {
        return ApplicationDetailsResponseDTO.ExperienceDTO.builder()
                .id(career.getId())
                .company(career.getCompany())
                .duration(career.getWorkedPeriod().getLabel()) // Enum이므로 label 추출 메서드 필요
                .description(career.getDescription())
                .build();
    }
}