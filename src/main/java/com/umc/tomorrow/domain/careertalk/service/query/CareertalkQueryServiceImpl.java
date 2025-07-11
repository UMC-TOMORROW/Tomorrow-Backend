package com.umc.tomorrow.domain.careertalk.service.query;

import com.umc.tomorrow.domain.careertalk.dto.response.GetCareertalkResponseDto;
import com.umc.tomorrow.domain.careertalk.dto.response.GetCareertalkListResponseDto;
import com.umc.tomorrow.domain.careertalk.entity.Careertalk;
import com.umc.tomorrow.domain.careertalk.repository.CareertalkRepository;
import com.umc.tomorrow.global.common.exception.RestApiException;
import com.umc.tomorrow.global.common.exception.code.GlobalErrorStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CareertalkQueryServiceImpl implements CareertalkQueryService {

    private final CareertalkRepository careertalkRepository;

    @Override
    public GetCareertalkListResponseDto getCareertalks(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        // Slice 조회 (더 효율적: totalCount 없이 hasNext 판단)
        Slice<Careertalk> careertalkSlice = careertalkRepository.findAllByOrderByCreatedAtDesc(pageable);

        List<GetCareertalkResponseDto> getCareertalkResponseDtoList = careertalkSlice.getContent().stream()
                .map(c -> GetCareertalkResponseDto.builder()
                        .id(c.getId())
                        .category(c.getCategory())
                        .title(c.getTitle())
                        .createdAt(c.getCreatedAt().toString())
                        .build())
                .toList();

        return GetCareertalkListResponseDto.builder()
                .careertalkList(getCareertalkResponseDtoList)
                .hasNext(careertalkSlice.hasNext())
                .build();
    }

    @Override
    public GetCareertalkResponseDto getCareertalk(Long careertalkId){
        Careertalk careertalk = careertalkRepository.findById(careertalkId)
                .orElseThrow(() -> new RestApiException(GlobalErrorStatus._NOT_FOUND));

        return GetCareertalkResponseDto.builder()
                .id(careertalk.getId())
                .category(careertalk.getCategory())
                .title(careertalk.getTitle())
                .createdAt(careertalk.getCreatedAt().toString())
                .build();
    }
}
