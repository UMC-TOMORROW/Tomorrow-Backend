package com.umc.tomorrow.domain.introduction.service.query;

import com.umc.tomorrow.domain.introduction.dto.response.GetIntroductionResponseDTO;

public interface IntroductionQueryService {
    GetIntroductionResponseDTO getIntroduction(Long userId, Long resumeId);
}
