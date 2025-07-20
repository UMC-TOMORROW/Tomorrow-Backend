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
import com.umc.tomorrow.domain.careertalk.dto.response.CreateCareertalkResponseDto;
import com.umc.tomorrow.domain.careertalk.entity.Careertalk;
import com.umc.tomorrow.domain.careertalk.repository.CareertalkRepository;

import com.umc.tomorrow.domain.member.entity.User;

import com.umc.tomorrow.domain.member.repository.UserRepository;
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
     * @param username 커리어톡 게시글 생성 사용자
     * @param requestDto 커리어톡 게시글 생성 요청 DTO
     * @return 커리어톡 게시글 응답 DTO
     */
    @Override
    public CreateCareertalkResponseDto createCareertalk(String username, CreateCareertalkRequestDto requestDto){

        User user = userRepository.findByUsername(username);

        Careertalk careertalk = Careertalk.builder()
                .category(requestDto.getCategory())
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .user(user)
                .build();

        careertalkRepository.save(careertalk);
        return CareertalkConverter.toCreateCareertalkResponseDto(careertalk);
    }
}
