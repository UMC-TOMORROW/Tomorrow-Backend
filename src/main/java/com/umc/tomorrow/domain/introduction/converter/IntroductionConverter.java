package com.umc.tomorrow.domain.introduction.converter;

import com.umc.tomorrow.domain.introduction.dto.request.IntroductionCreateRequestDTO;
import com.umc.tomorrow.domain.introduction.dto.response.GetIntroductionResponseDTO;
import com.umc.tomorrow.domain.introduction.dto.response.IntroductionResponseDTO;
import com.umc.tomorrow.domain.introduction.entity.Introduction;
import com.umc.tomorrow.domain.resume.entity.Resume;
import org.springframework.stereotype.Component;

@Component
public class IntroductionConverter {



    //자기소개 추가, 수정용
    public IntroductionResponseDTO toResponseDTO(Introduction introduction) {
        return IntroductionResponseDTO.builder()
                .introductionId(introduction.getId())
                .build();
    }

    //자기소개 조회용
    public GetIntroductionResponseDTO toGetResponseDTO(Introduction introduction) {
        return GetIntroductionResponseDTO.builder()
                .content(introduction.getContent())
                .build();
    }


}
