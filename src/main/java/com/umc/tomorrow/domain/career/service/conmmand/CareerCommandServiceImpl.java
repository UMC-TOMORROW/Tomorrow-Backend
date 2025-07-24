package com.umc.tomorrow.domain.career.service.conmmand;

import com.umc.tomorrow.domain.career.converter.CareerConverter;
import com.umc.tomorrow.domain.career.dto.request.CareerCreateRequestDTO;
import com.umc.tomorrow.domain.career.dto.response.CareerCreateResponseDTO;
import com.umc.tomorrow.domain.career.entity.Career;
import com.umc.tomorrow.domain.career.exception.code.CareerStatus;
import com.umc.tomorrow.domain.career.repository.CareerRepository;
import com.umc.tomorrow.domain.introduction.entity.Introduction;
import com.umc.tomorrow.domain.introduction.exception.code.IntroductionStatus;
import com.umc.tomorrow.domain.member.entity.User;
import com.umc.tomorrow.domain.member.repository.UserRepository;
import com.umc.tomorrow.domain.resume.entity.Resume;
import com.umc.tomorrow.domain.resume.repository.ResumeRepository;
import com.umc.tomorrow.global.common.exception.RestApiException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CareerCommandServiceImpl implements CareerCommandService{

    private final UserRepository userRepository;
    private final CareerRepository careerRepository;
    private final ResumeRepository resumeRepository;
    private final CareerConverter careerConverter;

    /**
     * 이력서 경력 생성 메서드
     * @param userId 경력을 추가하는 사용자
     * @param resumeId 작설할 이력서 id
     * @param dto 경력 추가 요청 DTO
     * @return converter로 이동
     */
    public CareerCreateResponseDTO saveCareer(Long userId, Long resumeId, CareerCreateRequestDTO dto){

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RestApiException(CareerStatus.CAREER_FORBIDDEN));

        // 존재하는 이력서인지 검증
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new RestApiException(CareerStatus.CAREER_NOT_FOUND));

        // 이력서의 소유자가 현재 유저인지 검증
        if (!resume.getUser().getId().equals(user.getId())) {
            throw new RestApiException(CareerStatus.CAREER_FORBIDDEN);
        }

        Career career = Career.builder()
                .company(dto.getCompany())
                .description(dto.getDescription())
                .workedYear(dto.getWorkedYear())
                .workedPeriod(dto.getWorkedPeriod())
                .resume(resume)
                .build();

        resume.getCareer().add(career);
        career.setResume(resume);

        Career saved = careerRepository.save(career);

        return CareerConverter.toResponseDTO(saved);
    }

}
