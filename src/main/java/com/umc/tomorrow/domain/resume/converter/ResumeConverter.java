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
import com.umc.tomorrow.domain.resume.entity.Resume;
import com.umc.tomorrow.domain.resume.entity.Certificate;
import com.umc.tomorrow.domain.member.entity.User;
import java.util.stream.Collectors;
import java.util.List;

public class ResumeConverter {
    /**
     * DTO,User를 이력서 entity로 바꿈 (toEntity)
     */
    public static Resume toEntity(ResumeSaveRequestDTO dto, User user) {
        Resume resume = Resume.builder()
            .user(user)
            .build();

        Introduction introduction = Introduction.builder()
                .content(dto.getIntroduction())
                .resume(resume)
                .build();
        resume.setIntroduction(introduction);


        List<Career> career = dto.getCareer().stream()
            .map(careerDto -> Career.builder()
                .company(careerDto.getCompany())
                .description(careerDto.getDescription())
                .workedYear(careerDto.getWorkedYear())
                .workedPeriod(careerDto.getWorkedPeriod())
                .resume(resume)
                .build())
            .collect(Collectors.toList());
        List<Certificate> certificates = dto.getCertificates().stream()
            .map(name -> Certificate.builder().name(name).resume(resume).build())
            .collect(Collectors.toList());
        resume.setCareer(career);
        resume.setCertificates(certificates);
        return resume;
    }

    /**
     * 이력서 Entity 를 Summary DTO로로 변환
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