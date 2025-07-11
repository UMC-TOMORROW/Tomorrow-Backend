package com.umc.tomorrow.domain.careertalk.service.command;

import com.umc.tomorrow.domain.careertalk.dto.request.CreateCareertalkRequestDto;
import com.umc.tomorrow.domain.careertalk.dto.response.CreateCareertalkResponseDto;
import org.springframework.security.core.userdetails.User;

public interface CareertalkCommandService {

    CreateCareertalkResponseDto createCareertalk(String username, CreateCareertalkRequestDto createCareertalkRequestDto);
}
