package com.umc.tomorrow.domain.certificate.service.query;

import com.umc.tomorrow.domain.certificate.dto.response.CertificateResponse;
import com.umc.tomorrow.domain.certificate.entity.Certificate;
import com.umc.tomorrow.domain.certificate.repository.CertificateRepository;
import com.umc.tomorrow.domain.resume.entity.Resume;
import com.umc.tomorrow.domain.resume.exception.ResumeException;
import com.umc.tomorrow.domain.resume.repository.ResumeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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

    private final Long resumeId = 10L;

    @Test
    @DisplayName("성공: 이력서 존재 시 자격증 목록을 반환한다")
    void getCertificatesByResumeId_success() {
        // given
        Resume resume = Resume.builder().id(resumeId).build();

        Certificate c1 = Certificate.builder()
                .id(1L)
                .fileUrl("https://s3/bucket/cert1.png")
                .name("cert1.png")
                .resume(resume)
                .build();

        Certificate c2 = Certificate.builder()
                .id(2L)
                .fileUrl("https://s3/bucket/cert2.png")
                .name("cert2.png")
                .resume(resume)
                .build();

        when(resumeRepository.findById(resumeId)).thenReturn(Optional.of(resume));
        when(certificateRepository.findByResumeId(resumeId)).thenReturn(List.of(c1, c2));

        // when
        List<CertificateResponse> res = service.getCertificatesByResumeId(resumeId);

        // then
        assertNotNull(res);
        assertEquals(2, res.size());
        assertAll(
                () -> assertEquals(1L, res.get(0).getId()),
                () -> assertEquals("https://s3/bucket/cert1.png", res.get(0).getFileUrl()),
                () -> assertEquals(2L, res.get(1).getId()),
                () -> assertEquals("https://s3/bucket/cert2.png", res.get(1).getFileUrl())
        );

        verify(resumeRepository).findById(resumeId);
        verify(certificateRepository).findByResumeId(resumeId);
    }

    @Test
    @DisplayName("성공: 자격증이 없으면 빈 리스트를 반환한다")
    void getCertificatesByResumeId_emptyList() {
        // given
        Resume resume = Resume.builder().id(resumeId).build();
        when(resumeRepository.findById(resumeId)).thenReturn(Optional.of(resume));
        when(certificateRepository.findByResumeId(resumeId)).thenReturn(List.of());

        // when
        List<CertificateResponse> res = service.getCertificatesByResumeId(resumeId);

        // then
        assertNotNull(res);
        assertTrue(res.isEmpty());
        verify(resumeRepository).findById(resumeId);
        verify(certificateRepository).findByResumeId(resumeId);
    }

    @Test
    @DisplayName("실패: 이력서가 없으면 ResumeException 발생")
    void getCertificatesByResumeId_resumeNotFound() {
        // given
        when(resumeRepository.findById(resumeId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(ResumeException.class, () -> service.getCertificatesByResumeId(resumeId));

        verify(resumeRepository).findById(resumeId);
        verifyNoInteractions(certificateRepository);
    }
}
