/**
 * 자격증 관련 생성,수정,삭제 서비스 구현체
 * 작성자: 이승주
 * 작성일: 2025-07-28
 */
package com.umc.tomorrow.domain.resume.certificate.service.command;

import com.umc.tomorrow.domain.resume.certificate.dto.response.CertificateResponse;
import com.umc.tomorrow.domain.resume.certificate.entity.Certificate;
import com.umc.tomorrow.domain.resume.certificate.exception.CertificateException;
import com.umc.tomorrow.domain.resume.certificate.exception.code.CertificateErrorStatus;
import com.umc.tomorrow.domain.resume.certificate.repository.CertificateRepository;
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
    public CertificateResponse uploadCertificate(Long resumeId, MultipartFile file) {

        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new ResumeException(ResumeErrorStatus.RESUME_NOT_FOUND));

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
    public CertificateResponse deleteCertificate(Long certificateId) {
        Certificate certificate = certificateRepository.findById(certificateId)
                .orElseThrow(() -> new CertificateException(CertificateErrorStatus.CERTIFICATE_NOT_FOUND));

        s3Uploader.delete(certificate.getFileUrl()); // S3 삭제
        certificateRepository.delete(certificate); // db 삭제

        return CertificateResponse.builder()
                .id(certificate.getId())
                .fileUrl(certificate.getFileUrl())
                .build();
    }
}
