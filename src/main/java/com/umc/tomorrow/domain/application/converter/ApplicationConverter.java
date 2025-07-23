/**
 * ApplicationConverter
 * Application Entity <-> DTO 변환 및 매핑
 * 작성자: 정여진
 * 생성일: 2025-07-23
 */
package com.umc.tomorrow.domain.application.converter;

import com.umc.tomorrow.domain.application.dto.request.UpdateApplicationStatusRequestDTO;
import com.umc.tomorrow.domain.application.dto.response.UpdateApplicationStatusResponseDTO;
import com.umc.tomorrow.domain.application.enums.ApplicationStatus;

public class ApplicationConverter {

    /**
     * 요청 DTO → Enum
     */
    public static ApplicationStatus toEnum(UpdateApplicationStatusRequestDTO dto) {
        return ApplicationStatus.from(dto.getStatus());
    }

    /**
     * Enum + ID → 응답 DTO
     */
    public static UpdateApplicationStatusResponseDTO toResponse(Long applicationId, ApplicationStatus status) {
        return UpdateApplicationStatusResponseDTO.builder()
                .applicationId(applicationId)
                .status(status.getLabel())
                .build();
    }
}