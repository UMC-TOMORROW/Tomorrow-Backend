/**
 * ResumeConverter
 * - Resume Entity <-> DTO 변환 및 매핑
 * 작성자: 정여진
 * 생성일: 2025-07-20
 */
package com.umc.tomorrow.domain.resume.converter;

import com.umc.tomorrow.domain.career.entity.Career;
import com.umc.tomorrow.domain.introduction.entity.Introduction;
import com.umc.tomorrow.domain.resume.dto.request.ResumeSaveRequestDTO;
import com.umc.tomorrow.domain.resume.dto.response.ResumeSummaryResponseDTO;
import com.umc.tomorrow.domain.resume.entity.Experience;
import com.umc.tomorrow.domain.resume.entity.Resume;
import com.umc.tomorrow.domain.resume.certificate.entity.Certificate;
import com.umc.tomorrow.domain.member.entity.User;
import java.util.stream.Collectors;
import java.util.List;

public class ResumeConverter {
    /**
     * DTO, User를 이력서 Entity로 변환 (toEntity)
     * - ResumeSaveRequestDTO의 변경에 맞춰 toEntity 로직을 수정합니다.
     */
    public static Resume toEntity(ResumeSaveRequestDTO dto, User user) {
        // Resume 엔티티 생성
        Resume resume = Resume.builder()
                .user(user)
                .build();

        // Introduction 엔티티 생성 및 Resume에 연결
        Introduction introduction = Introduction.builder()
                .content(dto.getIntroduction())
                .resume(resume)
                .build();
        resume.setIntroduction(introduction);

        // CareerSaveRequest DTO 리스트를 Career 엔티티 리스트로 변환
        List<Career> careers = dto.getCareers().stream()
                .map(careerDto -> Career.builder()
                        .company(careerDto.getCompany())
                        .description(careerDto.getDescription())
                        .workedYear(careerDto.getWorkedYear())
                        .workedPeriod(careerDto.getWorkedPeriod())
                        .resume(resume) // 연관관계 설정
                        .build())
                .collect(Collectors.toList());

        // ExperienceSaveRequest DTO 리스트를 Experience 엔티티 리스트로 변환
        // ResumeSaveRequestDTO에 Experience DTO 필드가 있다고 가정
        List<Experience> experiences = dto.getExperiences().stream()
                .map(experienceDto -> Experience.builder()
                        .place(experienceDto.getPlace())
                        .task(experienceDto.getTask())
                        .duration(experienceDto.getDuration())
                        .year(experienceDto.getYear())
                        .description(experienceDto.getDescription())
                        .resume(resume)
                        .build())
                .collect(Collectors.toList());

        // CertificateSaveRequest DTO 리스트를 Certificate 엔티티 리스트로 변환
        List<Certificate> certificates = dto.getCertificates().stream()
                .map(certificateDto -> Certificate.builder()
                        .name(certificateDto.getName())
                        .fileUrl(certificateDto.getFileUrl()) // 파일 URL 필드도 사용
                        .resume(resume) // 연관관계 설정
                        .build())
                .collect(Collectors.toList());

        // Resume 엔티티에 변환된 리스트 할당
        resume.setCareer(careers);
        resume.setExperiences(experiences);
        resume.setCertificates(certificates);

        return resume;
    }

    /**
     * 이력서 Entity 를 Summary DTO로 변환
     */
    public static ResumeSummaryResponseDTO toSummaryDTO(Resume resume) {
        return ResumeSummaryResponseDTO.builder()
                .introduction(resume.getIntroduction() != null
                        ? resume.getIntroduction().getContent()
                        : null)
                .career(resume.getCareer().stream()
                        .map(career -> ResumeSummaryResponseDTO.CareerSummary.builder()
                                .companyName(career.getCompany())
                                .description(career.getDescription())
                                .workedYear(career.getWorkedYear())
                                .workedPeriod(career.getWorkedPeriod())
                                .build())
                        .collect(Collectors.toList()))
                .certificates(resume.getCertificates().stream()
                        .map(Certificate::getName)
                        .collect(Collectors.toList()))
                .build();
    }
}