package com.umc.tomorrow.domain.career.service.query;

import com.umc.tomorrow.domain.career.dto.response.CareerGetResponseDTO;
import java.util.List;

public interface CareerQueryService {
    CareerGetResponseDTO getCareer(Long userId, Long resumeId, Long careerId);
    List<CareerGetResponseDTO> getCareerList(Long userId, Long resumeId);
}
