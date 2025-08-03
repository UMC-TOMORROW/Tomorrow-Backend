package com.umc.tomorrow.domain.career.service.conmmand;

import com.umc.tomorrow.domain.career.converter.CareerConverter;
import com.umc.tomorrow.domain.career.dto.request.CareerCreateRequestDTO;
import com.umc.tomorrow.domain.career.dto.request.CareerUpdateRequestDTO;
import com.umc.tomorrow.domain.career.dto.response.CareerCreateResponseDTO;
import com.umc.tomorrow.domain.career.dto.response.CareerGetResponseDTO;
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

    /**
     * 이력서 수정 생성 메서드
     * @param userId 경력을 수정하는 사용자
     * @param resumeId 수정할 이력서 id
     * @param careerId 수정할 경력 id
     * @param dto 경력 수정 요청 DTO
     * @return converter로 이동
     */
    public CareerCreateResponseDTO updateCareer(Long userId, Long resumeId, Long careerId, CareerUpdateRequestDTO dto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RestApiException(CareerStatus.CAREER_FORBIDDEN));

        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new RestApiException(CareerStatus.CAREER_NOT_FOUND));

        if (!resume.getUser().getId().equals(user.getId())) {
            throw new RestApiException(CareerStatus.CAREER_FORBIDDEN);
        }

        Career career = careerRepository.findById(careerId)
                .orElseThrow(() -> new RestApiException(CareerStatus.CAREER_DELETE_NOT_FOUND));

        // resumeId와 career가 일치하는지
        if (!career.getResume().getId().equals(resumeId)) {
            throw new RestApiException(CareerStatus.CAREER_FORBIDDEN);
        }

        career.update(
                dto.getCompany(),
                dto.getDescription(),
                dto.getWorkedYear(),
                dto.getWorkedPeriod()
        );

        return CareerConverter.toResponseDTO(career);
    }

    /**
     * 이력서 경력 삭제 메서드
     * @param userId 경력을 삭제하는 사용자
     * @param resumeId 삭제할 이력서 id
     * @param careerId 삭제할 경력 id
     */
    @Override
    public void deleteCareer(Long userId, Long resumeId, Long careerId) {

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

        careerRepository.delete(career);
    }



}
