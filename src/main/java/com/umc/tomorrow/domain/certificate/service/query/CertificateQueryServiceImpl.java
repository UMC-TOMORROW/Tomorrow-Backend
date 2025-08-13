/**
 * 자격증 조회 서비스
 * 작성자: 이승주
 * 작성일: 2025-08-03
 */
package com.umc.tomorrow.domain.certificate.service.query;

import com.umc.tomorrow.domain.certificate.dto.response.CertificateResponse;
import com.umc.tomorrow.domain.certificate.entity.Certificate;
import com.umc.tomorrow.domain.certificate.repository.CertificateRepository;
import com.umc.tomorrow.domain.resume.entity.Resume;
import com.umc.tomorrow.domain.resume.exception.ResumeException;
import com.umc.tomorrow.domain.resume.exception.code.ResumeErrorStatus;
import com.umc.tomorrow.domain.resume.repository.ResumeRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CertificateQueryServiceImpl implements CertificateQueryService {

    private final CertificateRepository certificateRepository;
    private final ResumeRepository resumeRepository;

    @Override
    public List<CertificateResponse> getCertificatesByResumeId(Long resumeId) {

        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new ResumeException(ResumeErrorStatus.RESUME_NOT_FOUND));

        List<Certificate> certificates = certificateRepository.findByResumeId(resumeId);

        return certificates.stream()
                .map(cert -> CertificateResponse.builder()
                        .id(cert.getId())
                        .fileUrl(cert.getFileUrl())
                        .filename(cert.getName())
                        .build())
                .collect(Collectors.toList());
    }
}
