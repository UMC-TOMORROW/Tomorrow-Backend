/**
 * ApplicationQueryService
 * - 사용자별 지원 현황(전체/합격) 조회 비즈니스 로직 담당
 *
 * 작성자: 정여진
 * 생성일: 2025-07-24
 */
package com.umc.tomorrow.domain.application.service.query;

import com.umc.tomorrow.domain.application.converter.ApplicationConverter;
import com.umc.tomorrow.domain.application.dto.response.ApplicationStatusListResponseDTO;
import com.umc.tomorrow.domain.application.entity.Application;
import com.umc.tomorrow.domain.application.enums.ApplicationStatus;
import com.umc.tomorrow.domain.application.repository.ApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationQueryService {

    private final ApplicationRepository applicationRepository;

    /**
     * 지원 현황 목록을 조회합니다.
     *
     * @param userId 로그인한 사용자 ID
     * @param type "all"이면 전체, "pass"이면 합격된 항목만 조회
     * @return 지원서 요약 정보 리스트
     */
    public List<ApplicationStatusListResponseDTO> getApplicationsByType(Long userId, String type) {
        List<Application> applications;

        if ("pass".equalsIgnoreCase(type)) {
            // 사용자의 합격된 지원서만 조회
            applications = applicationRepository.findAllByUserIdAndStatus(userId, ApplicationStatus.ACCEPTED);
        } else {
            // 사용자의 전체 지원서 조회
            applications = applicationRepository.findAllByUserId(userId);
        }

        // 지원서 목록을 응답 DTO로 (converter 이용)
        return applications.stream()
                .map(ApplicationConverter::toStatusListDTO)
                .collect(Collectors.toList());
    }
}
