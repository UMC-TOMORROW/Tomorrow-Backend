package com.umc.tomorrow.domain.introduction.service;

import com.umc.tomorrow.domain.introduction.dto.request.IntroductionCreateRequestDTO;
import com.umc.tomorrow.domain.introduction.dto.request.IntroductionUpdateRequestDTO;
import com.umc.tomorrow.domain.introduction.dto.response.GetIntroductionResponseDTO;
import com.umc.tomorrow.domain.introduction.dto.response.IntroductionResponseDTO;

public interface IntroductionCommandService {

    IntroductionResponseDTO saveIntroduction(Long userId, Long resumeId, IntroductionCreateRequestDTO dto);
    IntroductionResponseDTO  updateIntroduction(Long userId, Long resumeId, IntroductionUpdateRequestDTO dto);
}
