package com.umc.tomorrow.domain.career.service.query;

import com.umc.tomorrow.domain.career.converter.CareerConverter;
import com.umc.tomorrow.domain.career.dto.response.CareerGetResponseDTO;
import com.umc.tomorrow.domain.career.entity.Career;
import com.umc.tomorrow.domain.career.exception.code.CareerStatus;
import com.umc.tomorrow.domain.career.repository.CareerRepository;
import com.umc.tomorrow.domain.member.entity.User;
import com.umc.tomorrow.domain.member.repository.UserRepository;
import com.umc.tomorrow.domain.resume.entity.Resume;
import com.umc.tomorrow.domain.resume.repository.ResumeRepository;
import com.umc.tomorrow.global.common.exception.RestApiException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CareerQueryServiceImpl implements CareerQueryService {

    private final UserRepository userRepository;
    private final CareerRepository careerRepository;
    private final ResumeRepository resumeRepository;

    /**
     * 이력서 경력 조회 메서드
     * @param userId 경력을 조회하는 사용자
     * @param resumeId 조회할 이력서 id
     * @param careerId 조회할 경력 id
     * @return converter로 이동
     */
    @Override
    public CareerGetResponseDTO getCareer(Long userId, Long resumeId, Long careerId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RestApiException(CareerStatus.CAREER_FORBIDDEN));

        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new RestApiException(CareerStatus.CAREER_NOT_FOUND));

        if (!resume.getUser().getId().equals(user.getId())) {
            throw new RestApiException(CareerStatus.CAREER_FORBIDDEN);
        }

        Career career = careerRepository.findById(careerId)
                .orElseThrow(() -> new RestApiException(CareerStatus.CAREER_NOT_FOUND));

        if (!career.getResume().getId().equals(resumeId)) {
            throw new RestApiException(CareerStatus.CAREER_FORBIDDEN);
        }

        return CareerConverter.toGetResponseDTO(career);
    }
}
