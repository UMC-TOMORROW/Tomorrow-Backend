package com.umc.tomorrow.domain.careertalk.service.command;

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
        return CreateCareertalkResponseDto.fromEntity(careertalk);
    }
}
