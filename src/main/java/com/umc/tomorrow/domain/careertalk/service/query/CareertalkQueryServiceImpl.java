package com.umc.tomorrow.domain.careertalk.service.query;

import com.umc.tomorrow.domain.careertalk.converter.CareertalkConverter;
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
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CareertalkQueryServiceImpl implements CareertalkQueryService {

    private final CareertalkRepository careertalkRepository;

    /**
     * 커리어톡 게시글 목록 조회
     * @param cursor 이전 요청에서의 마지막 게시글 번호
     * @param size 요청할 게시글 개수
     * @return 게시글 목록 DTO
     */
    @Override
    public GetCareertalkListResponseDto getCareertalks(Long cursor, int size) {
        Pageable pageable = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "id"));

        Slice<Careertalk> slice;

        if (cursor == null) {
            // 첫 페이지 요청: 최신순 전체
            slice = careertalkRepository.findAllByOrderByIdDesc(pageable);
        } else {
            // 커서 이후 데이터
            slice = careertalkRepository.findByIdLessThanOrderByIdDesc(cursor, pageable);
        }

        List<GetCareertalkResponseDto> dtoList = slice.getContent().stream()
                .map(CareertalkConverter::toGetCareertalkResponseDto)
                .toList();

        return GetCareertalkListResponseDto.builder()
                .careertalkList(dtoList)
                .hasNext(slice.hasNext())
                .build();
    }

    /**
     * 커리어톡 게시글 상세 조회
     * @param careertalkId 조회하고자 하는 커리어톡 게시글 Id
     * @return 해당 게시글 DTO
     */

    @Override
    public GetCareertalkResponseDto getCareertalk(Long careertalkId){
        Careertalk careertalk = careertalkRepository.findById(careertalkId)
                .orElseThrow(() -> new RestApiException(GlobalErrorStatus._NOT_FOUND));

        return GetCareertalkResponseDto.builder()
                .id(careertalk.getId())
                .category(careertalk.getCategory())
                .title(careertalk.getTitle())
                .content(careertalk.getContent())
                .createdAt(careertalk.getCreatedAt())
                .build();
    }
}
