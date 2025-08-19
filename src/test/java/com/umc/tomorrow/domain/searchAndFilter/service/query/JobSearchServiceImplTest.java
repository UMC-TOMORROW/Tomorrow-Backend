//package com.umc.tomorrow.domain.searchAndFilter.service.query;
//
//import com.umc.tomorrow.domain.job.entity.Job;
//import com.umc.tomorrow.domain.job.enums.JobCategory;
//import com.umc.tomorrow.domain.searchAndFilter.converter.JobSearchConverter;
//import com.umc.tomorrow.domain.searchAndFilter.dto.request.JobSearchRequestDTO;
//import com.umc.tomorrow.domain.searchAndFilter.dto.response.JobSearchResponseDTO;
//import com.umc.tomorrow.domain.searchAndFilter.repository.JobJpaRepository;
//import com.umc.tomorrow.domain.searchAndFilter.repository.JobSearchRepository;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.time.LocalTime;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@DisplayName("일자리 검색 및 필터 서비스 단위 테스트")
//@ExtendWith(MockitoExtension.class)
//class JobSearchServiceImplTest {
//
//    @InjectMocks
//    private JobSearchServiceImpl jobSearchService;
//
//    @Mock
//    private JobJpaRepository jobJpaRepository;
//
//    @Mock
//    private JobSearchRepository jobSearchRepository;
//
//    @Mock
//    private JobSearchConverter converter;
//
//    @Test
//    @DisplayName("검색 성공: 키워드와 지역 조건으로 일자리 목록 반환")
//    void searchJobs_성공() {
//        // given
//        JobSearchRequestDTO requestDTO = JobSearchRequestDTO.builder()
//                .keyword("카페")
//                .locationKeyword("인천")
//                .build();
//
//        Job mockJob = Job.builder()
//                .id(1L)
//                .title("카페 알바 모집")
//                .location("인천 부평구")
//                .companyName("스타카페")
//                .salary(12000)
//                .workStart(LocalTime.of(9, 0))
//                .workEnd(LocalTime.of(15, 0))
//                .jobCategory(JobCategory.SERVING)
//                .build();
//
//        JobSearchResponseDTO mockResponse = JobSearchResponseDTO.builder()
//                .jobId(1L)
//                .title("카페 알바 모집")
//                .location("인천 부평구")
//                .companyName("스타카페")
//                .salary(12000)
//                .workStart(LocalTime.of(9, 0))
//                .workEnd(LocalTime.of(15, 0))
//                .jobCategory(JobCategory.SERVING)
//                .build();
//
//        when(jobSearchRepository.searchJobs(requestDTO)).thenReturn(List.of(mockJob));
//        when(converter.toResponseDTO(mockJob)).thenReturn(mockResponse);
//
//        // when
//        List<JobSearchResponseDTO> result = jobSearchService.searchJobs(requestDTO);
//
//        // then
//        assertAll(
//                () -> assertNotNull(result),
//                () -> assertEquals(1, result.size()),
//                () -> assertEquals("카페 알바 모집", result.get(0).getTitle()),
//                () -> assertEquals("인천 부평구", result.get(0).getLocation())
//        );
//
//        verify(jobSearchRepository, times(1)).searchJobs(requestDTO);
//        verify(converter, times(1)).toResponseDTO(mockJob);
//    }
//
//    @Test
//    @DisplayName("전체 활성 공고 조회 성공")
//    void getAllActiveJobs_성공() {
//        // given
//        Job mockJob = Job.builder()
//                .id(2L)
//                .title("편의점 야간 알바")
//                .location("서울 강남구")
//                .companyName("GS편의점")
//                .salary(10000)
//                .workStart(LocalTime.of(22, 0))
//                .workEnd(LocalTime.of(6, 0))
//                .jobCategory(JobCategory.SERVING)
//                .build();
//
//        JobSearchResponseDTO mockResponse = JobSearchResponseDTO.builder()
//                .jobId(2L)
//                .title("편의점 야간 알바")
//                .location("서울 강남구")
//                .companyName("GS편의점")
//                .salary(10000)
//                .workStart(LocalTime.of(22, 0))
//                .workEnd(LocalTime.of(6, 0))
//                .jobCategory(JobCategory.SERVING)
//                .build();
//
//        when(jobJpaRepository.findByIsActiveTrue()).thenReturn(List.of(mockJob));
//        when(converter.toResponseDTO(mockJob)).thenReturn(mockResponse);
//
//        // when
//        List<JobSearchResponseDTO> result = jobSearchService.getAllActiveJobs();
//
//        // then
//        assertAll(
//                () -> assertNotNull(result),
//                () -> assertEquals(1, result.size()),
//                () -> assertEquals("편의점 야간 알바", result.get(0).getTitle())
//        );
//
//        verify(jobJpaRepository, times(1)).findByIsActiveTrue();
//        verify(converter, times(1)).toResponseDTO(mockJob);
//    }
//
//    @Test
//    @DisplayName("검색 실패: 조건에 맞는 일자리가 없을 경우 빈 리스트 반환")
//    void searchJobs_결과없음() {
//        // given
//        JobSearchRequestDTO requestDTO = JobSearchRequestDTO.builder()
//                .keyword("없는키워드")
//                .build();
//
//        when(jobSearchRepository.searchJobs(requestDTO)).thenReturn(List.of());
//
//        // when
//        List<JobSearchResponseDTO> result = jobSearchService.searchJobs(requestDTO);
//
//        // then
//        assertAll(
//                () -> assertNotNull(result),
//                () -> assertTrue(result.isEmpty())
//        );
//
//        verify(jobSearchRepository, times(1)).searchJobs(requestDTO);
//        verify(converter, never()).toResponseDTO(any());
//    }
//}
