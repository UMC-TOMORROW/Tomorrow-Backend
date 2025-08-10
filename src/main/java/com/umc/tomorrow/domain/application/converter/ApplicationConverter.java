/**
 * ApplicationConverter
 * Application Entity <-> DTO 변환 및 매핑
 * 작성자: 정여진
 * 생성일: 2025-07-23
 */
package com.umc.tomorrow.domain.application.converter;

import com.umc.tomorrow.domain.application.dto.request.UpdateApplicationStatusRequestDTO;
import com.umc.tomorrow.domain.application.dto.response.ApplicantListResponseDTO;
import com.umc.tomorrow.domain.application.dto.response.ApplicationStatusListResponseDTO;
import com.umc.tomorrow.domain.application.dto.response.ApplicationDetailsResponseDTO;
import com.umc.tomorrow.domain.application.dto.response.UpdateApplicationStatusResponseDTO;
import com.umc.tomorrow.domain.application.entity.Application;
import com.umc.tomorrow.domain.application.enums.ApplicationStatus;
import com.umc.tomorrow.domain.career.entity.Career;
import com.umc.tomorrow.domain.member.entity.User;
import com.umc.tomorrow.domain.certificate.entity.Certificate;
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
            List<ApplicationDetailsResponseDTO.ExperienceDTO> experienceDTOS = Optional.ofNullable(resume.getExperiences())
                    .orElse(Collections.emptyList())
                    .stream()
                    .map(experience -> ApplicationConverter.toExperienceDTO(experience))
                    .collect(Collectors.toList());

            List<ApplicationDetailsResponseDTO.CareerDTO> careerDTOS = Optional.ofNullable(resume.getCareer())
                    .orElse(Collections.emptyList())
                    .stream()
                    .map(career -> ApplicationConverter.toCareerDTO(career))
                    .collect(Collectors.toList());

            List<ApplicationDetailsResponseDTO.CertificationDTO> certificationDTOS = Optional.ofNullable(resume.getCertificates())
                    .orElse(Collections.emptyList())
                    .stream()
                    .map(certificate -> ApplicationConverter.toCertificationDTO(certificate))
                    .collect(Collectors.toList());

            // Introduction이 null인 경우 빈 문자열로 처리
            String resumeContent = null;
            if (resume.getIntroduction() != null) {
                resumeContent = resume.getIntroduction().getContent();
            }

            resumeInfo = ApplicationDetailsResponseDTO.ResumeInfoDTO.builder()
                    .resumeContent(resumeContent)
                    .experiences(experienceDTOS)
                    .careers(careerDTOS)
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

                .duration(experience.getDuration())
                .description(String.valueOf(experience))
                .description(experience.getDescription())

                .build();
    }

    // Certificate 엔티티를 CertificationDTO로 변환
    private static ApplicationDetailsResponseDTO.CertificationDTO toCertificationDTO(Certificate certificate) {
        return ApplicationDetailsResponseDTO.CertificationDTO.builder()
                .certificationName(certificate.getName())
                .fileUrl(certificate.getFileUrl())
                .filename(certificate.getName())
                .build();
    }

    // Career 엔티티를 CareerDTO로 변환
    private static ApplicationDetailsResponseDTO.CareerDTO toCareerDTO(com.umc.tomorrow.domain.career.entity.Career career) {
        return ApplicationDetailsResponseDTO.CareerDTO.builder()
                .id(career.getId())
                .company(career.getCompany())
                .position(career.getWorkedPeriod().getLabel()) // WorkPeriodType의 라벨 사용
                .duration(career.getWorkedYear() + "년") // workedYear를 문자열로 변환
                .description(career.getDescription())
                .build();
    }

    public static ApplicantListResponseDTO toApplicantListResponseDTO(Application application) {
        return ApplicantListResponseDTO.builder()
                .applicantId(application.getUser().getId())
                .userName(application.getUser().getName())
                .applicationDate(application.getCreatedAt())
                .status(application.getStatus().getLabel())
                .resumeTitle(application.getResume() != null ? application.getResume().toString() : null) // 또는 "없음"
                .build();
    }
}