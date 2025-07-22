/**
 * CareertalkConverter
 * Careertalk Entity <-> DTO 변환 및 매핑
 * 작성자: 이승주
 * 생성일: 2025-07-20
 */
package com.umc.tomorrow.domain.careertalk.converter;

import com.umc.tomorrow.domain.careertalk.dto.response.CreateCareertalkResponseDto;
import com.umc.tomorrow.domain.careertalk.dto.response.GetCareertalkResponseDto;
import com.umc.tomorrow.domain.careertalk.entity.Careertalk;

public class CareertalkConverter {
    /**
     * Careertalk 엔티티로부터 커리어톡 생성 DTO로 변환
     */
    public static CreateCareertalkResponseDto toCreateCareertalkResponseDto(Careertalk careertalk) {
        return CreateCareertalkResponseDto.builder()
                .id(careertalk.getId())
                .build();
    }

    /**
     * Careertalk 엔티티로부터 커리어톡 조회 DTO로 변환
     */
    public static GetCareertalkResponseDto toGetCareertalkResponseDto(Careertalk careertalk) {
        return GetCareertalkResponseDto.builder()
                .id(careertalk.getId())
                .category(careertalk.getCategory())
                .title(careertalk.getTitle())
                .createdAt(careertalk.getCreatedAt())
                .build();
}
    }
