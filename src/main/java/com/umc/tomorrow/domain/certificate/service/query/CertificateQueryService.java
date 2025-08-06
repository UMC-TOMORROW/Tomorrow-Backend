/**
 * 자격증 관련 조회 인터페이스 서비스
 * 작성자: 이승주
 * 작성일: 2025-08-03
 */
package com.umc.tomorrow.domain.certificate.service.query;

import com.umc.tomorrow.domain.certificate.dto.response.CertificateResponse;
import java.util.List;

public interface CertificateQueryService {
    List<CertificateResponse> getCertificatesByResumeId(Long resumeId);
}
