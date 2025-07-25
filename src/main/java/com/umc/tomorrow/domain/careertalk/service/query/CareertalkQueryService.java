/**
 * 커리어톡 조회 서비스 인터페이스
 * 작성자: 이승주
 * 생성일: 2020-07-10
 * 수정일: 2025-07-20
 */
package com.umc.tomorrow.domain.careertalk.service.query;

import com.umc.tomorrow.domain.careertalk.dto.response.GetCareertalkListResponseDto;
import com.umc.tomorrow.domain.careertalk.dto.response.GetCareertalkResponseDto;

public interface CareertalkQueryService {
    GetCareertalkResponseDto getCareertalk(Long id);
    GetCareertalkListResponseDto getCareertalks(Long cursor, int size);
    GetCareertalkListResponseDto getCareertalksByTitle(String title, Long cursor, int size);
    GetCareertalkListResponseDto getCareertalksByCategory(String category, Long cursor, int size);
}

