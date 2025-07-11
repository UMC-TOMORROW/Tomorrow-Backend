package com.umc.tomorrow.domain.careertalk.service.query;

import com.umc.tomorrow.domain.careertalk.dto.response.GetCareertalkListResponseDto;
import com.umc.tomorrow.domain.careertalk.dto.response.GetCareertalkResponseDto;
import com.umc.tomorrow.global.common.base.BaseResponse;

public interface CareertalkQueryService {
    GetCareertalkListResponseDto getCareertalks(int page, int size);
    GetCareertalkResponseDto getCareertalk(Long id);
}
