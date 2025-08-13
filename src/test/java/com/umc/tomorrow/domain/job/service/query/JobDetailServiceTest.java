package com.umc.tomorrow.domain.job.service.query;

import com.umc.tomorrow.domain.job.converter.JobConverter;
import com.umc.tomorrow.domain.job.dto.response.JobDetailResponseDTO;
import com.umc.tomorrow.domain.job.entity.Job;
import com.umc.tomorrow.domain.job.repository.JobRepository;
import com.umc.tomorrow.global.common.exception.RestApiException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@DisplayName("일자리 상세조회 단위 테스트")
@ExtendWith(MockitoExtension.class)
class JobDetailServiceTest {

    @InjectMocks
    private JobQueryServiceImpl jobQueryService; // getJobDetail()이 구현된 서비스

    @Mock
    private JobRepository jobRepository;

    @Mock
    private JobConverter jobConverter;

    @Test
    @DisplayName("상세조회 성공: 유효한 jobId로 DTO 반환")
    void getJobDetail_성공() {
        // given
        Long jobId = 1L;
        Job mockJob = Job.builder().id(jobId).title("편의점 알바").build();
        JobDetailResponseDTO mockResponse = JobDetailResponseDTO.builder()
                .title("편의점 알바")
                .build();

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(mockJob));
        when(jobConverter.toJobDetailResponseDTO(mockJob)).thenReturn(mockResponse);

        // when
        JobDetailResponseDTO result = jobQueryService.getJobDetail(jobId);

        // then
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals("편의점 알바", result.getTitle())
        );
    }

    @Test
    @DisplayName("상세조회 실패: 존재하지 않는 jobId로 예외 발생")
    void getJobDetail_실패_JOB_NOT_FOUND() {
        // given
        Long jobId = 999L;
        when(jobRepository.findById(jobId)).thenReturn(Optional.empty());

        // when & then // 실패는 when, then 한번에 처리
        assertThrows(RestApiException.class,
                () -> jobQueryService.getJobDetail(jobId));
    }
}
