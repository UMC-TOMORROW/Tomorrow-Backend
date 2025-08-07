/**
 * 자격증 관련 생성,수정,삭제 서비스 구현체
 * 작성자: 이승주
 * 작성일: 2025-07-28
 */
package com.umc.tomorrow.domain.certificate.service.command;

import com.umc.tomorrow.domain.certificate.dto.response.CertificateResponse;
import com.umc.tomorrow.domain.certificate.entity.Certificate;
import com.umc.tomorrow.domain.certificate.exception.CertificateException;
import com.umc.tomorrow.domain.certificate.exception.code.CertificateErrorStatus;
import com.umc.tomorrow.domain.certificate.repository.CertificateRepository;
import com.umc.tomorrow.domain.resume.entity.Resume;
import com.umc.tomorrow.domain.resume.exception.ResumeException;
import com.umc.tomorrow.domain.resume.exception.code.ResumeErrorStatus;
import com.umc.tomorrow.domain.resume.repository.ResumeRepository;
import com.umc.tomorrow.global.infrastructure.s3.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
@RequiredArgsConstructor
public class CertificateCommandServiceImpl implements CertificateCommandService {

    private final S3Uploader s3Uploader;
    private final ResumeRepository resumeRepository;
    private final CertificateRepository certificateRepository;

    /*
    자격증 업로드
     */
    @Override
    public CertificateResponse uploadCertificate(Long userId,Long resumeId, MultipartFile file) {

        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new ResumeException(ResumeErrorStatus.RESUME_NOT_FOUND));

        if (!resume.getUser().getId().equals(userId)) {
            throw new ResumeException(ResumeErrorStatus.RESUME_FORBIDDEN);
        }

        String fileUrl = s3Uploader.upload(file, "certificates");

        Certificate certificate = Certificate.builder()
                .fileUrl(fileUrl)
                .resume(resume)
                .build();

        Certificate saved = certificateRepository.save(certificate);

        return CertificateResponse.builder()
                .id(saved.getId())
                .fileUrl(saved.getFileUrl())
                .build();
    }

    /*
    자격증 삭제
     */
    @Override
    public CertificateResponse deleteCertificate(Long userId,Long certificateId) {
        Certificate certificate = certificateRepository.findById(certificateId)
                .orElseThrow(() -> new CertificateException(CertificateErrorStatus.CERTIFICATE_NOT_FOUND));

        if (!certificate.getResume().getUser().getId().equals(userId)) {
            throw new CertificateException(CertificateErrorStatus.CERTIFICATE_FORBIDDEN);
        }

        s3Uploader.delete(certificate.getFileUrl()); // S3 삭제
        certificateRepository.delete(certificate); // db 삭제

        return CertificateResponse.builder()
                .id(certificate.getId())
                .fileUrl(certificate.getFileUrl())
                .build();
    }
}
