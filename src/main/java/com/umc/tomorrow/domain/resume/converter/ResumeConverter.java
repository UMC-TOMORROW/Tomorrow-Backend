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
import com.umc.tomorrow.domain.certificate.entity.Certificate;
import com.umc.tomorrow.domain.member.entity.User;
import java.util.stream.Collectors;
import java.util.List;
import java.util.ArrayList;

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

        // Introduction 엔티티 생성 및 Resume에 연결 (null 체크)
        if (dto.getIntroduction() != null && !dto.getIntroduction().trim().isEmpty()) {
            Introduction introduction = Introduction.builder()
                    .content(dto.getIntroduction())
                    .resume(resume)
                    .build();
            resume.setIntroduction(introduction);
        }

        // CareerSaveRequest DTO 리스트를 Career 엔티티 리스트로 변환 (null 체크)
        List<Career> careers = null;
        if (dto.getCareers() != null && !dto.getCareers().isEmpty()) {
            careers = dto.getCareers().stream()
                    .filter(careerDto -> {
                        // 경력 정보가 완전히 비어있으면 통과 (저장 가능)
                        boolean isCompletelyEmpty = (careerDto.getCompany() == null || careerDto.getCompany().trim().isEmpty()) &&
                                                  (careerDto.getDescription() == null || careerDto.getDescription().trim().isEmpty()) &&
                                                  careerDto.getWorkedYear() == null &&
                                                  careerDto.getWorkedPeriod() == null;
                        
                        if (isCompletelyEmpty) {
                            return true; // 완전히 비어있으면 통과
                        }
                        
                        // 일부만 입력된 경우 필터링 (저장 불가)
                        boolean hasAllFields = careerDto.getCompany() != null && !careerDto.getCompany().trim().isEmpty() &&
                                            careerDto.getDescription() != null && !careerDto.getDescription().trim().isEmpty() &&
                                            careerDto.getWorkedYear() != null &&
                                            careerDto.getWorkedPeriod() != null;
                        
                        return hasAllFields; // 모든 필드가 채워져 있으면 통과
                    })
                    .map(careerDto -> {
                        // 완전히 비어있는 경우 빈 Career 객체 생성
                        if ((careerDto.getCompany() == null || careerDto.getCompany().trim().isEmpty()) &&
                            (careerDto.getDescription() == null || careerDto.getDescription().trim().isEmpty()) &&
                            careerDto.getWorkedYear() == null &&
                            careerDto.getWorkedPeriod() == null) {
                            return Career.builder()
                                    .company("")
                                    .description("")
                                    .workedYear(0)
                                    .workedPeriod(null)
                                    .resume(resume)
                                    .build();
                        }
                        
                        // 모든 필드가 채워진 경우 정상 생성
                        return Career.builder()
                                .company(careerDto.getCompany())
                                .description(careerDto.getDescription())
                                .workedYear(careerDto.getWorkedYear())
                                .workedPeriod(careerDto.getWorkedPeriod())
                                .resume(resume)
                                .build();
                    })
                    .collect(Collectors.toList());
        } else {
            careers = new ArrayList<>();
        }

        // CertificateSaveRequest DTO 리스트를 Certificate 엔티티 리스트로 변환 (null 체크)
        List<Certificate> certificates = null;
        if (dto.getCertificates() != null && !dto.getCertificates().isEmpty()) {
            certificates = dto.getCertificates().stream()
                    .filter(certificateDto -> {
                        // 자격증 정보 중 하나라도 있으면 모든 필드가 있어야 함
                        boolean hasAnyField = certificateDto.getFileUrl() != null && !certificateDto.getFileUrl().trim().isEmpty() ||
                                           certificateDto.getFilename() != null && !certificateDto.getFilename().trim().isEmpty();
                        
                        if (hasAnyField) {
                            // 모든 필드가 채워져 있는지 확인
                            return certificateDto.getFileUrl() != null && !certificateDto.getFileUrl().trim().isEmpty() &&
                                   certificateDto.getFilename() != null && !certificateDto.getFilename().trim().isEmpty();
                        }
                        return false; // 모든 필드가 비어있으면 제외
                    })
                    .map(certificateDto -> Certificate.builder()
                            .name(certificateDto.getFilename()) // filename을 name으로 사용
                            .fileUrl(certificateDto.getFileUrl())
                            .filename(certificateDto.getFilename())
                            .resume(resume) // 연관관계 설정
                            .build())
                    .collect(Collectors.toList());
        } else {
            certificates = new ArrayList<>();
        }

        // Resume 엔티티에 변환된 리스트 할당
        resume.setCareer(careers);
        resume.setCertificates(certificates);

        return resume;
    }

    /**
     * 이력서 Entity 를 Summary DTO로 변환
     */
    public static ResumeSummaryResponseDTO toSummaryDTO(Resume resume) {
        return ResumeSummaryResponseDTO.builder()
                .introduction(resume.getIntroduction() != null && resume.getIntroduction().getContent() != null
                        ? resume.getIntroduction().getContent()
                        : null)
                .career(resume.getCareer() != null && !resume.getCareer().isEmpty() // null 처리
                        ? resume.getCareer().stream()
                        .filter(career -> career != null) // null career 필터링
                        .map(career -> ResumeSummaryResponseDTO.CareerSummary.builder()
                                .companyName(career.getCompany() != null ? career.getCompany() : "")
                                .description(career.getDescription() != null ? career.getDescription() : "")
                                .workedYear(career.getWorkedYear() != null ? career.getWorkedYear() : 0)
                                .workedPeriod(career.getWorkedPeriod() != null ? career.getWorkedPeriod() : null)
                                .build())
                        .collect(Collectors.toList())
                        : List.of())
                .certificates(resume.getCertificates() != null && !resume.getCertificates().isEmpty()
                        ? resume.getCertificates().stream()
                        .filter(cert -> cert != null && cert.getFilename() != null) // null certificate 필터링
                        .map(Certificate::getFilename)
                        .collect(Collectors.toList())
                        : List.of())
                .build();
    }
}