package com.umc.tomorrow.domain.introduction.service.command;

import com.umc.tomorrow.domain.introduction.converter.IntroductionConverter;
import com.umc.tomorrow.domain.introduction.dto.request.IntroductionCreateRequestDTO;
import com.umc.tomorrow.domain.introduction.dto.request.IntroductionUpdateRequestDTO;
import com.umc.tomorrow.domain.introduction.dto.response.GetIntroductionResponseDTO;
import com.umc.tomorrow.domain.introduction.dto.response.IntroductionResponseDTO;
import com.umc.tomorrow.domain.introduction.entity.Introduction;
import com.umc.tomorrow.domain.introduction.exception.code.IntroductionStatus;
import com.umc.tomorrow.domain.introduction.repository.IntroductionRepository;
import com.umc.tomorrow.domain.member.entity.User;
import com.umc.tomorrow.domain.member.repository.UserRepository;
import com.umc.tomorrow.domain.resume.entity.Resume;
import com.umc.tomorrow.domain.resume.repository.ResumeRepository;
import com.umc.tomorrow.global.common.exception.RestApiException;
import com.umc.tomorrow.global.common.exception.code.GlobalErrorStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class IntroductionCommandServiceImpl implements IntroductionCommandService {

    private final IntroductionRepository introductionRepository;
    private final UserRepository userRepository;
    private final ResumeRepository resumeRepository;
    private final IntroductionConverter introductionConverter;

    /**
     * 이력서 자기소개 생성 메서드
     * @param userId 자기소개 추가하는 사용자
     * @param resumeId 작설할 이력서 id
     * @param dto 자기소개 추가 요청 DTO
     * @return converter로 이동
     */
    public IntroductionResponseDTO saveIntroduction(Long userId, Long resumeId, IntroductionCreateRequestDTO dto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RestApiException(IntroductionStatus.INTRODUCTION_FORBIDDEN));

        // 존재하는 이력서인지 검증
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new RestApiException(IntroductionStatus.INTRODUCTION_NOT_FOUND));

        // 이력서의 소유자가 현재 유저인지 검증
        if (!resume.getUser().getId().equals(user.getId())) {
            throw new RestApiException(IntroductionStatus.INTRODUCTION_FORBIDDEN);
        }

        Introduction introduction = Introduction.builder()
                .content(dto.getContent())
                .resume(resume)
                .build();

        resume.setIntroduction(introduction);

        Introduction saved = introductionRepository.save(introduction);

        return introductionConverter.toResponseDTO(saved);

    }

    /**
     * 이력서 자기소개 생성 메서드
     * @param userId 자기소개를 조회할 사용자
     * @param resumeId 작설한 이력서 id
     * @return converter로 이동
     */
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

    /**
     * 이력서 자기소개 생성 메서드
     * @param userId 자기소개를 수정할 사용자
     * @param resumeId 수정할 이력서 id
     * @param dto 자기소개 수정 요청 DTO
     * @return converter로 이동
     */
    @Override
    public IntroductionResponseDTO updateIntroduction(Long userId, Long resumeId, IntroductionUpdateRequestDTO dto) {
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

        introduction.setContent(dto.getContent());

        Introduction saved = introductionRepository.save(introduction);
        return introductionConverter.toResponseDTO(saved);
    }
}
