package com.umc.tomorrow.domain.introduction.service.query;

import com.umc.tomorrow.domain.introduction.converter.IntroductionConverter;
import com.umc.tomorrow.domain.introduction.dto.response.GetIntroductionResponseDTO;
import com.umc.tomorrow.domain.introduction.entity.Introduction;
import com.umc.tomorrow.domain.introduction.exception.code.IntroductionStatus;
import com.umc.tomorrow.domain.member.entity.User;
import com.umc.tomorrow.domain.member.repository.UserRepository;
import com.umc.tomorrow.domain.resume.entity.Resume;
import com.umc.tomorrow.domain.resume.repository.ResumeRepository;
import com.umc.tomorrow.global.common.exception.RestApiException;
import com.umc.tomorrow.global.common.exception.code.GlobalErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class IntroductionQueryServiceImpl implements IntroductionQueryService {

    private final UserRepository userRepository;
    private final ResumeRepository resumeRepository;
    private final IntroductionConverter introductionConverter;

    /**
     * 이력서 자기소개 조회 메서드
     * @param userId 자기소개를 조회할 사용자
     * @param resumeId 작설한 이력서 id
     * @return converter로 이동
     */
    @Override
    public GetIntroductionResponseDTO getIntroduction(Long userId, Long resumeId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RestApiException(IntroductionStatus.INTRODUCTION_FORBIDDEN));

        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new RestApiException(IntroductionStatus.INTRODUCTION_NOT_FOUND));

        if (!resume.getUser().getId().equals(user.getId())) {
            throw new RestApiException(IntroductionStatus.INTRODUCTION_FORBIDDEN);
        }

        Introduction introduction = resume.getIntroduction();
        if (introduction == null) {
            throw new RestApiException(GlobalErrorStatus._NOT_FOUND);
        }

        return introductionConverter.toGetResponseDTO(introduction);
    }
}
