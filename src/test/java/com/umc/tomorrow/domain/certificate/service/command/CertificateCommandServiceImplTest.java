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
import com.umc.tomorrow.domain.member.entity.User;
import com.umc.tomorrow.global.infrastructure.s3.S3Uploader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@DisplayName("CertificateCommandServiceImpl 단위 테스트")
@ExtendWith(MockitoExtension.class)
class CertificateCommandServiceImplTest {

    @InjectMocks
    private CertificateCommandServiceImpl service;

    @Mock
    private S3Uploader s3Uploader;

    @Mock
    private ResumeRepository resumeRepository;

    @Mock
    private CertificateRepository certificateRepository;

    private final Long userId = 1L;
    private final Long resumeId = 100L;
    private final Long certificateId = 1000L;
    private final String s3Url = "https://s3.amazonaws.com/certificates/test.pdf";
    private final String filename = "test.pdf";

    private User user;
    private Resume resume;
    private Certificate certificate;
    private MockMultipartFile multipartFile;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(userId)
                .username("testuser")
                .email("test@example.com")
                .build();

        resume = Resume.builder()
                .id(resumeId)
                .user(user)
                .build();

        certificate = Certificate.builder()
                .id(certificateId)
                .name(filename)
                .fileUrl(s3Url)
                .resume(resume)
                .build();

        multipartFile = new MockMultipartFile(
                "file",
                filename,
                "application/pdf",
                "test content".getBytes()
        );
    }

    @Test
    @DisplayName("uploadCertificate: 자격증 업로드 성공")
    void uploadCertificate_success() {
        // given
        when(resumeRepository.findById(resumeId)).thenReturn(Optional.of(resume));
        when(s3Uploader.upload(any(), eq("certificates"))).thenReturn(s3Url);
        when(certificateRepository.save(any(Certificate.class))).thenReturn(certificate);

        // when
        CertificateResponse result = service.uploadCertificate(userId, resumeId, multipartFile);

        // then
        assertNotNull(result);
        assertEquals(certificateId, result.getId());
        assertEquals(s3Url, result.getFileUrl());
        assertEquals(filename, result.getFilename());

        verify(resumeRepository).findById(resumeId);
        verify(s3Uploader).upload(multipartFile, "certificates");
        verify(certificateRepository).save(any(Certificate.class));
    }

    @Test
    @DisplayName("uploadCertificate: 이력서를 찾을 수 없으면 ResumeException 발생")
    void uploadCertificate_resumeNotFound_throwsException() {
        // given
        when(resumeRepository.findById(resumeId)).thenReturn(Optional.empty());

        // when & then
        ResumeException exception = assertThrows(ResumeException.class,
                () -> service.uploadCertificate(userId, resumeId, multipartFile));

        assertEquals(ResumeErrorStatus.RESUME_NOT_FOUND, exception.getErrorCode());
        verify(resumeRepository).findById(resumeId);
        verifyNoInteractions(s3Uploader, certificateRepository);
    }

    @Test
    @DisplayName("uploadCertificate: 다른 사용자의 이력서에 접근하면 ResumeException 발생")
    void uploadCertificate_unauthorizedAccess_throwsException() {
        // given
        Long otherUserId = 999L;
        when(resumeRepository.findById(resumeId)).thenReturn(Optional.of(resume));

        // when & then
        ResumeException exception = assertThrows(ResumeException.class,
                () -> service.uploadCertificate(otherUserId, resumeId, multipartFile));

        assertEquals(ResumeErrorStatus.RESUME_FORBIDDEN, exception.getErrorCode());
        verify(resumeRepository).findById(resumeId);
        verifyNoInteractions(s3Uploader, certificateRepository);
    }

    @Test
    @DisplayName("deleteCertificate: 자격증 삭제 성공")
    void deleteCertificate_success() {
        // given
        when(certificateRepository.findById(certificateId)).thenReturn(Optional.of(certificate));
        doNothing().when(s3Uploader).delete(s3Url);
        doNothing().when(certificateRepository).delete(certificate);

        // when
        CertificateResponse result = service.deleteCertificate(userId, certificateId);

        // then
        assertNotNull(result);
        assertEquals(certificateId, result.getId());
        assertEquals(s3Url, result.getFileUrl());
        assertEquals(filename, result.getFilename());

        verify(certificateRepository).findById(certificateId);
        verify(s3Uploader).delete(s3Url);
        verify(certificateRepository).delete(certificate);
    }

    @Test
    @DisplayName("deleteCertificate: 자격증을 찾을 수 없으면 CertificateException 발생")
    void deleteCertificate_certificateNotFound_throwsException() {
        // given
        when(certificateRepository.findById(certificateId)).thenReturn(Optional.empty());

        // when & then
        CertificateException exception = assertThrows(CertificateException.class,
                () -> service.deleteCertificate(userId, certificateId));

        assertEquals(CertificateErrorStatus.CERTIFICATE_NOT_FOUND, exception.getErrorCode());
        verify(certificateRepository).findById(certificateId);
        verifyNoInteractions(s3Uploader);
        verifyNoMoreInteractions(certificateRepository);
    }

    @Test
    @DisplayName("deleteCertificate: 다른 사용자의 자격증에 접근하면 CertificateException 발생")
    void deleteCertificate_unauthorizedAccess_throwsException() {
        // given
        Long otherUserId = 999L;
        when(certificateRepository.findById(certificateId)).thenReturn(Optional.of(certificate));

        // when & then
        CertificateException exception = assertThrows(CertificateException.class,
                () -> service.deleteCertificate(otherUserId, certificateId));

        assertEquals(CertificateErrorStatus.CERTIFICATE_FORBIDDEN, exception.getErrorCode());
        verify(certificateRepository).findById(certificateId);
        verifyNoInteractions(s3Uploader);
        verifyNoMoreInteractions(certificateRepository);
    }

    @Test
    @DisplayName("uploadCertificate: S3 업로드 실패 시 적절한 예외 전파")
    void uploadCertificate_s3UploadFailure_propagatesException() {
        // given
        when(resumeRepository.findById(resumeId)).thenReturn(Optional.of(resume));
        when(s3Uploader.upload(any(), eq("certificates")))
                .thenThrow(new RuntimeException("S3 업로드 실패"));

        // when & then
        assertThrows(RuntimeException.class,
                () -> service.uploadCertificate(userId, resumeId, multipartFile));

        verify(resumeRepository).findById(resumeId);
        verify(s3Uploader).upload(multipartFile, "certificates");
        verifyNoInteractions(certificateRepository);
    }

    @Test
    @DisplayName("deleteCertificate: S3 삭제 실패 시 적절한 예외 전파")
    void deleteCertificate_s3DeleteFailure_propagatesException() {
        // given
        when(certificateRepository.findById(certificateId)).thenReturn(Optional.of(certificate));
        doThrow(new RuntimeException("S3 삭제 실패")).when(s3Uploader).delete(s3Url);

        // when & then
        assertThrows(RuntimeException.class,
                () -> service.deleteCertificate(userId, certificateId));

        verify(certificateRepository).findById(certificateId);
        verify(s3Uploader).delete(s3Url);
        verifyNoMoreInteractions(certificateRepository);
    }
}
