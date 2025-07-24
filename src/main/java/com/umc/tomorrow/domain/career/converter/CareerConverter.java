package com.umc.tomorrow.domain.career.converter;

import com.umc.tomorrow.domain.career.dto.request.CareerCreateRequestDTO;
import com.umc.tomorrow.domain.career.dto.request.CareerUpdateRequestDTO;
import com.umc.tomorrow.domain.career.dto.response.CareerCreateResponseDTO;
import com.umc.tomorrow.domain.career.dto.response.CareerGetResponseDTO;
import com.umc.tomorrow.domain.career.entity.Career;
import org.springframework.stereotype.Component;

@Component
public class CareerConverter {

    /**
     * Career Entity → CareerCreateResponseDTO 변환
     */
    public static CareerCreateResponseDTO toResponseDTO(Career career) {
        return CareerCreateResponseDTO.builder()
                .careerId(career.getId())
                .build();
    }

    /**
     * CareerCreateRequestDTO → Career Entity
     */
    public static Career toEntity(CareerCreateRequestDTO dto) {
        return Career.builder()
                .company(dto.getCompany())
                .description(dto.getDescription())
                .workedYear(dto.getWorkedYear())
                .workedPeriod(dto.getWorkedPeriod())
                .build();
    }

    public static CareerGetResponseDTO toGetResponseDTO(Career career) {
        return CareerGetResponseDTO.builder()
                .careerId(career.getId())
                .company(career.getCompany())
                .description(career.getDescription())
                .workedYear(career.getWorkedYear())
                .workedPeriod(career.getWorkedPeriod())
                .build();
    }

}
