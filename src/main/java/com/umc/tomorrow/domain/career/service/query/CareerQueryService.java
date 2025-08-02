package com.umc.tomorrow.domain.career.service.query;

import com.umc.tomorrow.domain.career.dto.response.CareerGetResponseDTO;

public interface CareerQueryService {
    CareerGetResponseDTO getCareer(Long userId, Long resumeId, Long careerId);
}
