/**
 * ApplicationConverter
 * Application Entity <-> DTO 변환 및 매핑
 * 작성자: 정여진
 * 생성일: 2025-07-23
 */
package com.umc.tomorrow.domain.application.converter;

import com.umc.tomorrow.domain.application.dto.request.UpdateApplicationStatusRequestDTO;
import com.umc.tomorrow.domain.application.dto.response.ApplicationStatusListResponseDTO;
import com.umc.tomorrow.domain.application.dto.response.UpdateApplicationStatusResponseDTO;
import com.umc.tomorrow.domain.application.entity.Application;
import com.umc.tomorrow.domain.application.enums.ApplicationStatus;

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
}