/**
 * 자격증 관련 생성,수정,삭제 인터페이스 서비스
 * 작성자: 이승주
 * 작성일: 2025-07-28
 */
package com.umc.tomorrow.domain.certificate.service.command;

import com.umc.tomorrow.domain.certificate.dto.response.CertificateResponse;
import org.springframework.web.multipart.MultipartFile;

public interface CertificateCommandService {
    CertificateResponse uploadCertificate(Long resumeId, MultipartFile file);
    CertificateResponse deleteCertificate(Long certificateId);

}
