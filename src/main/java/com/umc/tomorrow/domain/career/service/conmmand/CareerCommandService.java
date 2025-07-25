package com.umc.tomorrow.domain.career.service.conmmand;

import com.umc.tomorrow.domain.career.dto.request.CareerCreateRequestDTO;
import com.umc.tomorrow.domain.career.dto.request.CareerUpdateRequestDTO;
import com.umc.tomorrow.domain.career.dto.response.CareerCreateResponseDTO;
import com.umc.tomorrow.domain.career.dto.response.CareerGetResponseDTO;
import com.umc.tomorrow.domain.introduction.dto.request.IntroductionCreateRequestDTO;
import com.umc.tomorrow.domain.introduction.dto.request.IntroductionUpdateRequestDTO;
import com.umc.tomorrow.domain.introduction.dto.response.GetIntroductionResponseDTO;
import com.umc.tomorrow.domain.introduction.dto.response.IntroductionResponseDTO;

public interface CareerCommandService {

    CareerCreateResponseDTO saveCareer(Long userId, Long resumeId, CareerCreateRequestDTO dto);
    CareerCreateResponseDTO  updateCareer(Long userId, Long resumeId, Long careerId, CareerUpdateRequestDTO dto);
    CareerGetResponseDTO getCareer(Long userId, Long resumeId, Long careerId);
    void deleteCareer(Long userId, Long resumeId, Long careerId);

}
