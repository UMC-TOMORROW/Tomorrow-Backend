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
import com.umc.tomorrow.domain.member.entity.User;
import com.umc.tomorrow.domain.certificate.entity.Certificate;
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
    */
    public static ApplicationStatusListResponseDTO toStatusListDTO(Application application) {
        String statusLabel = application.getStatus() != null ? application.getStatus().getLabel() : "불합격";
        return ApplicationStatusListResponseDTO.builder()
                .postTitle(application.getJob().getTitle())
                .company(application.getJob().getCompanyName()) 
                .date(application.getAppliedAt().toLocalDate().toString()) 
                .status(application.getStatus())
                .build();
    }

    // 엔티티들을 ApplicantResumeResponseDTO로 변환
    public static ApplicationDetailsResponseDTO toApplicantResumeResponseDTO(
            Application application,
            User user,
            Resume resume
    ) {
        String statusText = application.getStatus() != null ? application.getStatus().getLabel() : "불합격";

        String content = application.getContent();

        ApplicationDetailsResponseDTO.UserProfileDTO userProfile = ApplicationDetailsResponseDTO.UserProfileDTO.builder()
                .userName(user.getName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .build();

        ApplicationDetailsResponseDTO.ResumeInfoDTO resumeInfo = null;
        if (resume != null) {
            // 경력과 경험을 하나로 합쳐서 처리
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

            String resumeContent = null;
            if (resume.getIntroduction() != null) {
                resumeContent = resume.getIntroduction().getContent();
            }

            resumeInfo = ApplicationDetailsResponseDTO.ResumeInfoDTO.builder()
                    .resumeContent(resumeContent)
                    .careers(careerDTOS)
                    .certifications(certificationDTOS)
                    .build();
        }

        return ApplicationDetailsResponseDTO.builder()
                .applicantId(user.getId())
                .applicationId(application.getId())
                .status(statusText)
                .content(content)
                .userProfile(userProfile)
                .resumeInfo(resumeInfo)
                .build();
    }

    private static ApplicationDetailsResponseDTO.CertificationDTO toCertificationDTO(Certificate certificate) {
        return ApplicationDetailsResponseDTO.CertificationDTO.builder()
                .certificationName(certificate.getFilename())
                .fileUrl(certificate.getFileUrl())
                .build();
    }

    // Career 엔티티를 CareerDTO로 변환
    private static ApplicationDetailsResponseDTO.CareerDTO toCareerDTO(com.umc.tomorrow.domain.career.entity.Career career) {
        return ApplicationDetailsResponseDTO.CareerDTO.builder()
                .id(career.getId())
                .company(career.getCompany())
                .position(career.getWorkedPeriod().getLabel()) 
                .duration(career.getWorkedYear() + "년") 
                .description(career.getDescription())
                .build();
    }

    public static ApplicantListResponseDTO toApplicantListResponseDTO(Application application) {
        String statusLabel = application.getStatus() != null ? application.getStatus().getLabel() : "불합격";
        return ApplicantListResponseDTO.builder()
                .applicantId(application.getUser().getId())
                .applicationId(application.getId())
                .userName(application.getUser().getName())
                .phoneNumber(application.getUser().getPhoneNumber())
                .applicationDate(application.getCreatedAt())
                .status(statusLabel)
                .resumeId(application.getResume() != null ? application.getResume().getId() : null) // 또는 "없음"
                .content(application.getContent())
                .build();
    }
}