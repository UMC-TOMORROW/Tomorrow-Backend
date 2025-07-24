/**
 * 커리어톡 생성 서비스
 * - 커리어톡 저장 비즈니스 로직
 * 작성자: 이승주
 * 생성일: 2020-07-10
 * 수정일: 2025-07-20
 */
package com.umc.tomorrow.domain.careertalk.service.command;

import com.umc.tomorrow.domain.careertalk.converter.CareertalkConverter;
import com.umc.tomorrow.domain.careertalk.dto.request.CreateCareertalkRequestDto;
import com.umc.tomorrow.domain.careertalk.dto.request.UpdateCareertalkRequestDto;
import com.umc.tomorrow.domain.careertalk.dto.response.CareertalkResponseDto;
import com.umc.tomorrow.domain.careertalk.entity.Careertalk;
import com.umc.tomorrow.domain.careertalk.exception.CareertalkException;
import com.umc.tomorrow.domain.careertalk.exception.code.CareertalkErrorStatus;
import com.umc.tomorrow.domain.careertalk.repository.CareertalkRepository;

import com.umc.tomorrow.domain.member.entity.User;

import com.umc.tomorrow.domain.member.repository.UserRepository;
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
public class CareertalkCommandServiceImpl implements CareertalkCommandService {

    private final CareertalkRepository careertalkRepository;
    private final UserRepository userRepository;

    /**
     * 커리어톡 게시글 생성 메서드
     * @param userId 게시글 생성 사용자 Id
     * @param requestDto 커리어톡 게시글 생성 요청 DTO
     * @return 커리어톡 게시글 응답 DTO
     */
    @Override
    public CareertalkResponseDto createCareertalk(Long userId, CreateCareertalkRequestDto requestDto){

        User user = userRepository.findById(userId)
                .orElseThrow(()  -> new RestApiException(GlobalErrorStatus._NOT_FOUND));

        Careertalk careertalk = Careertalk.builder()
                .category(requestDto.getCategory())
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .user(user)
                .build();

        careertalkRepository.save(careertalk);
        return CareertalkConverter.toCareertalkResponseDto(careertalk);
    }

    /**
     * 커리어톡 게시글 수정 메서드
     * @param userId 게시글 작성자 Id
     * @param careertalkId 해당하는 커리어톡 게시글 Id
     * @param requestDto 커리어톡 게시글 수정 요청 DTO
     * @return 커리어톡 게시글 응답 DTO
     */
    @Override
    public CareertalkResponseDto updateCareertalk(Long userId, Long careertalkId, UpdateCareertalkRequestDto requestDto){

        User user = userRepository.findById(userId)
                .orElseThrow(()  -> new RestApiException(GlobalErrorStatus._NOT_FOUND));

        Careertalk careertalk = careertalkRepository.findById(careertalkId)
                .orElseThrow(() -> new CareertalkException(CareertalkErrorStatus.CAREERTALK_NOT_FOUND));

        if (!careertalk.getUser().getId().equals(userId)){ //권한 유효성 검사
            throw new CareertalkException(CareertalkErrorStatus.CAREERTALK_FORBIDDEN);
        }

        careertalk.update(requestDto.getTitle(), requestDto.getContent(), requestDto.getCategory());
        return CareertalkConverter.toCareertalkResponseDto(careertalk);
    }

    /**
     * 커리어톡 게시글 삭제 메서드
     * @param userId 게시글 작성자 Id
     * @param careertalkId 해당하는 커리어톡 게시글 id
     * @return 커리어톡 게시글 응답 DTO
     */
    @Override
    public CareertalkResponseDto deleteCareertalk(Long userId, Long careertalkId){
        User user = userRepository.findById(userId)
                .orElseThrow(()  -> new RestApiException(GlobalErrorStatus._NOT_FOUND));

        Careertalk careertalk = careertalkRepository.findById(careertalkId)
                .orElseThrow(() -> new CareertalkException(CareertalkErrorStatus.CAREERTALK_NOT_FOUND));

        if (!careertalk.getUser().getId().equals(userId)){ //권한 유효성 검사
            throw new CareertalkException(CareertalkErrorStatus.CAREERTALK_FORBIDDEN);
        }

        CareertalkResponseDto responseDto = CareertalkConverter.toCareertalkResponseDto(careertalk); // 삭제 전 DTO 생성
        careertalkRepository.delete(careertalk);

        return responseDto;
    }
}
