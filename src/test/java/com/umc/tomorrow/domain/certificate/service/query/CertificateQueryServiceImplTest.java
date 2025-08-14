package com.umc.tomorrow.domain.certificate.service.query;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@DisplayName("CertificateQueryServiceImpl 단위 테스트")
@ExtendWith(MockitoExtension.class)
class CertificateQueryServiceImplTest {

    @InjectMocks
    private CertificateQueryServiceImpl service;

    @Mock
    private CertificateRepository certificateRepository;

    @Mock
    private ResumeRepository resumeRepository;

    private final Long resumeId = 100L;
    private final Long certificateId1 = 1000L;
    private final Long certificateId2 = 1001L;
    private final String fileUrl1 = "https://s3.amazonaws.com/certificates/cert1.pdf";
    private final String fileUrl2 = "https://s3.amazonaws.com/certificates/cert2.pdf";
    private final String filename1 = "AWS_Solutions_Architect.pdf";
    private final String filename2 = "Oracle_OCP.pdf";

    private User user;
    private Resume resume;
    private Certificate certificate1;
    private Certificate certificate2;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .build();

        resume = Resume.builder()
                .id(resumeId)
                .user(user)
                .build();

        certificate1 = Certificate.builder()
                .id(certificateId1)
                .name(filename1)
                .fileUrl(fileUrl1)
                .resume(resume)
                .build();

        certificate2 = Certificate.builder()
                .id(certificateId2)
                .name(filename2)
                .fileUrl(fileUrl2)
                .resume(resume)
                .build();
    }

    @Test
    @DisplayName("getCertificatesByResumeId: 이력서별 자격증 목록 조회 성공")
    void getCertificatesByResumeId_success() {
        // given
        List<Certificate> certificates = Arrays.asList(certificate1, certificate2);
        
        when(resumeRepository.findById(resumeId)).thenReturn(Optional.of(resume));
        when(certificateRepository.findByResumeId(resumeId)).thenReturn(Optional.of(certificates));

        // when
        List<CertificateResponse> result = service.getCertificatesByResumeId(resumeId);

        // then
        assertNotNull(result);
        assertEquals(2, result.size());

        // 첫 번째 자격증 검증
        CertificateResponse firstCert = result.get(0);
        assertEquals(certificateId1, firstCert.getId());
        assertEquals(fileUrl1, firstCert.getFileUrl());
        assertEquals(filename1, firstCert.getFilename());

        // 두 번째 자격증 검증
        CertificateResponse secondCert = result.get(1);
        assertEquals(certificateId2, secondCert.getId());
        assertEquals(fileUrl2, secondCert.getFileUrl());
        assertEquals(filename2, secondCert.getFilename());

        verify(resumeRepository).findById(resumeId);
        verify(certificateRepository).findByResumeId(resumeId);
    }

    @Test
    @DisplayName("getCertificatesByResumeId: 이력서를 찾을 수 없으면 ResumeException 발생")
    void getCertificatesByResumeId_resumeNotFound_throwsException() {
        // given
        when(resumeRepository.findById(resumeId)).thenReturn(Optional.empty());

        // when & then
        ResumeException exception = assertThrows(ResumeException.class,
                () -> service.getCertificatesByResumeId(resumeId));

        assertEquals(ResumeErrorStatus.RESUME_NOT_FOUND, exception.getErrorCode());
        verify(resumeRepository).findById(resumeId);
        verifyNoInteractions(certificateRepository);
    }

    @Test
    @DisplayName("getCertificatesByResumeId: 자격증을 찾을 수 없으면 CertificateException 발생")
    void getCertificatesByResumeId_certificateNotFound_throwsException() {
        // given
        when(resumeRepository.findById(resumeId)).thenReturn(Optional.of(resume));
        when(certificateRepository.findByResumeId(resumeId)).thenReturn(Optional.empty());

        // when & then
        CertificateException exception = assertThrows(CertificateException.class,
                () -> service.getCertificatesByResumeId(resumeId));

        assertEquals(CertificateErrorStatus.CERTIFICATE_NOT_FOUND, exception.getErrorCode());
        verify(resumeRepository).findById(resumeId);
        verify(certificateRepository).findByResumeId(resumeId);
    }

    @Test
    @DisplayName("getCertificatesByResumeId: 빈 자격증 목록 반환")
    void getCertificatesByResumeId_emptyList_success() {
        // given
        List<Certificate> emptyCertificates = Arrays.asList();
        
        when(resumeRepository.findById(resumeId)).thenReturn(Optional.of(resume));
        when(certificateRepository.findByResumeId(resumeId)).thenReturn(Optional.of(emptyCertificates));

        // when
        List<CertificateResponse> result = service.getCertificatesByResumeId(resumeId);

        // then
        assertNotNull(result);
        assertEquals(0, result.size());
        assertTrue(result.isEmpty());

        verify(resumeRepository).findById(resumeId);
        verify(certificateRepository).findByResumeId(resumeId);
    }

    @Test
    @DisplayName("getCertificatesByResumeId: 단일 자격증 조회 성공")
    void getCertificatesByResumeId_singleCertificate_success() {
        // given
        List<Certificate> singleCertificate = Arrays.asList(certificate1);
        
        when(resumeRepository.findById(resumeId)).thenReturn(Optional.of(resume));
        when(certificateRepository.findByResumeId(resumeId)).thenReturn(Optional.of(singleCertificate));

        // when
        List<CertificateResponse> result = service.getCertificatesByResumeId(resumeId);

        // then
        assertNotNull(result);
        assertEquals(1, result.size());

        CertificateResponse cert = result.get(0);
        assertEquals(certificateId1, cert.getId());
        assertEquals(fileUrl1, cert.getFileUrl());
        assertEquals(filename1, cert.getFilename());

        verify(resumeRepository).findById(resumeId);
        verify(certificateRepository).findByResumeId(resumeId);
    }

    @Test
    @DisplayName("getCertificatesByResumeId: repository 예외 발생 시 적절한 예외 전파")
    void getCertificatesByResumeId_repositoryException_propagatesException() {
        // given
        when(resumeRepository.findById(resumeId)).thenReturn(Optional.of(resume));
        when(certificateRepository.findByResumeId(resumeId))
                .thenThrow(new RuntimeException("DB 조회 실패"));

        // when & then
        assertThrows(RuntimeException.class,
                () -> service.getCertificatesByResumeId(resumeId));

        verify(resumeRepository).findById(resumeId);
        verify(certificateRepository).findByResumeId(resumeId);
    }

    @Test
    @DisplayName("getCertificatesByResumeId: DTO 변환 검증 - 올바른 매핑")
    void getCertificatesByResumeId_dtoMapping_verification() {
        // given
        List<Certificate> certificates = Arrays.asList(certificate1);
        
        when(resumeRepository.findById(resumeId)).thenReturn(Optional.of(resume));
        when(certificateRepository.findByResumeId(resumeId)).thenReturn(Optional.of(certificates));

        // when
        List<CertificateResponse> result = service.getCertificatesByResumeId(resumeId);

        // then
        assertNotNull(result);
        assertEquals(1, result.size());

        CertificateResponse cert = result.get(0);
        // Entity의 모든 필드가 DTO에 올바르게 매핑되었는지 검증
        assertEquals(certificate1.getId(), cert.getId());
        assertEquals(certificate1.getFileUrl(), cert.getFileUrl());
        assertEquals(certificate1.getFilename(), cert.getFilename());

        verify(resumeRepository).findById(resumeId);
        verify(certificateRepository).findByResumeId(resumeId);
    }
}
