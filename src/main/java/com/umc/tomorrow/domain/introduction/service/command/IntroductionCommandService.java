package com.umc.tomorrow.domain.introduction.service.command;

import com.umc.tomorrow.domain.introduction.dto.request.IntroductionCreateRequestDTO;
import com.umc.tomorrow.domain.introduction.dto.response.GetIntroductionResponseDTO;
import com.umc.tomorrow.domain.introduction.dto.response.IntroductionResponseDTO;

public interface IntroductionCommandService {

    IntroductionResponseDTO saveIntroduction(Long userId, Long resumeId, IntroductionCreateRequestDTO dto);
    GetIntroductionResponseDTO getIntroduction(Long userId, Long resumeId);
    //IntroductionResponseDTO  updateIntroduction(Long userId, Long resumeId, IntroductionCreateRequestDTO dto);
}
