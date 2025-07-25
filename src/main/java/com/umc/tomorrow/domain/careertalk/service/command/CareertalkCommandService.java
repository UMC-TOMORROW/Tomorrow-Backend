/**
 * 커리어톡 생성 서비스 인터페이스
 * 작성자: 이승주
 * 생성일: 2020-07-10
 * 수정일: 2025-07-20
 */
package com.umc.tomorrow.domain.careertalk.service.command;

import com.umc.tomorrow.domain.careertalk.dto.request.CreateCareertalkRequestDto;
import com.umc.tomorrow.domain.careertalk.dto.request.UpdateCareertalkRequestDto;
import com.umc.tomorrow.domain.careertalk.dto.response.CareertalkResponseDto;

public interface CareertalkCommandService {

    CareertalkResponseDto createCareertalk(Long userId, CreateCareertalkRequestDto createCareertalkRequestDto);
    CareertalkResponseDto updateCareertalk(Long userId, Long careertalkId, UpdateCareertalkRequestDto updateCareertalkRequestDto);
    CareertalkResponseDto deleteCareertalk(Long userId, Long careertalkId);

}
