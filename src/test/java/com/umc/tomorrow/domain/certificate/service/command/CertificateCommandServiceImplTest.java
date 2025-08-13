package com.umc.tomorrow.domain.certificate.service.command;

import com.umc.tomorrow.domain.certificate.dto.response.CertificateResponse;
import com.umc.tomorrow.domain.certificate.entity.Certificate;
import com.umc.tomorrow.domain.certificate.exception.CertificateException;
import com.umc.tomorrow.domain.certificate.repository.CertificateRepository;
import com.umc.tomorrow.domain.member.entity.User;
import com.umc.tomorrow.domain.resume.entity.Resume;
import com.umc.tomorrow.domain.resume.exception.ResumeException;
import com.umc.tomorrow.domain.resume.repository.ResumeRepository;
import com.umc.tomorrow.global.infrastructure.s3.S3Uploader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("CertificateCommandServiceImpl 단위 테스트")
@ExtendWith(MockitoExtension.class)
class CertificateCommandServiceImplTest {

    @InjectMocks
    private CertificateCommandServiceImpl service;

    @Mock private S3Uploader s3Uploader;
    @Mock private ResumeRepository resumeRepository;
    @Mock private CertificateRepository certificateRepository;

    private final Long userId = 1L;
    private final Long otherUserId = 999L;
    private final Long resumeId = 10L;
    private final Long certId = 100L;

    @Test
    @DisplayName("업로드 성공: 소유자 검증 통과 → S3 업로드 → DB 저장 → DTO 반환")
    void upload_success() {
        // given
        User owner = User.builder().id(userId).build();
        Resume resume = Resume.builder().id(resumeId).user(owner).build();
        MultipartFile file = mock(MultipartFile.class);

        when(resumeRepository.findById(resumeId)).thenReturn(Optional.of(resume));
        when(file.getOriginalFilename()).thenReturn("cert.png");
        when(s3Uploader.upload(eq(file), eq("certificates"))).thenReturn("https://s3/url/cert.png");

        // DB save는 받은 엔티티에 id만 세팅된 걸 반환한다고 가정
        Answer<Certificate> returnsWithId = inv -> {
            Certificate c = inv.getArgument(0);
            return Certificate.builder()
                    .id(certId)
                    .name(c.getName())
                    .fileUrl(c.getFileUrl())
                    .resume(c.getResume())
                    .build();
        };
        when(certificateRepository.save(any(Certificate.class))).thenAnswer(returnsWithId);

        // when
        CertificateResponse res = service.uploadCertificate(userId, resumeId, file);

        // then
        assertNotNull(res);
        assertEquals(certId, res.getId());
        assertEquals("https://s3/url/cert.png", res.getFileUrl());
        assertEquals("cert.png", res.getFilename());

        // 저장 시 전달된 엔티티 내용도 확인
        ArgumentCaptor<Certificate> captor = ArgumentCaptor.forClass(Certificate.class);
        verify(certificateRepository).save(captor.capture());
        Certificate saved = captor.getValue();
        assertEquals("cert.png", saved.getName());
        assertEquals("https://s3/url/cert.png", saved.getFileUrl());
        assertSame(resume, saved.getResume());
    }

    @Test
    @DisplayName("업로드 실패: 이력서가 없으면 ResumeException")
    void upload_resumeNotFound() {
        when(resumeRepository.findById(resumeId)).thenReturn(Optional.empty());
        MultipartFile file = mock(MultipartFile.class);

        assertThrows(ResumeException.class, () -> service.uploadCertificate(userId, resumeId, file));

        verifyNoInteractions(s3Uploader, certificateRepository);
    }

    @Test
    @DisplayName("업로드 실패: 소유자 아님 → ResumeException")
    void upload_forbidden() {
        User other = User.builder().id(otherUserId).build();
        Resume resume = Resume.builder().id(resumeId).user(other).build();
        MultipartFile file = mock(MultipartFile.class);

        when(resumeRepository.findById(resumeId)).thenReturn(Optional.of(resume));

        assertThrows(ResumeException.class, () -> service.uploadCertificate(userId, resumeId, file));
        verifyNoInteractions(s3Uploader, certificateRepository);
    }

    @Test
    @DisplayName("업로드 중 DB 저장 예외 → 보상 삭제(S3 delete) 호출")
    void upload_compensation_deleteOnDbFailure() {
        User owner = User.builder().id(userId).build();
        Resume resume = Resume.builder().id(resumeId).user(owner).build();
        MultipartFile file = mock(MultipartFile.class);

        when(resumeRepository.findById(resumeId)).thenReturn(Optional.of(resume));
        when(file.getOriginalFilename()).thenReturn("x.pdf");
        when(s3Uploader.upload(eq(file), eq("certificates"))).thenReturn("s3://bucket/x.pdf");

        when(certificateRepository.save(any(Certificate.class)))
                .thenThrow(new RuntimeException("db save fail"));

        assertThrows(RuntimeException.class, () -> service.uploadCertificate(userId, resumeId, file));

        // 업로드 성공 후 save 실패 → delete 보상 호출됨
        verify(s3Uploader).delete("s3://bucket/x.pdf");
    }

    @Test
    @DisplayName("업로드 중 S3 업로드 단계 예외 → 보상 삭제 미호출")
    void upload_s3UploadThrows_noCompDelete() {
        User owner = User.builder().id(userId).build();
        Resume resume = Resume.builder().id(resumeId).user(owner).build();
        MultipartFile file = mock(MultipartFile.class);

        when(resumeRepository.findById(resumeId)).thenReturn(Optional.of(resume));
        when(file.getOriginalFilename()).thenReturn("y.pdf");
        when(s3Uploader.upload(eq(file), eq("certificates")))
                .thenThrow(new RuntimeException("s3 fail"));

        assertThrows(RuntimeException.class, () -> service.uploadCertificate(userId, resumeId, file));

        // fileUrl이 설정되지 못했으므로 delete 호출 안 됨
        verify(s3Uploader, never()).delete(anyString());
        verifyNoInteractions(certificateRepository);
    }

    @Test
    @DisplayName("삭제 성공: DB 먼저 삭제 후 S3 삭제(실패해도 무시)")
    void delete_success() {
        User owner = User.builder().id(userId).build();
        Resume resume = Resume.builder().id(resumeId).user(owner).build();
        Certificate cert = Certificate.builder()
                .id(certId)
                .name("del.pdf")
                .fileUrl("s3://bucket/del.pdf")
                .resume(resume)
                .build();

        when(certificateRepository.findById(certId)).thenReturn(Optional.of(cert));

        // when
        CertificateResponse res = service.deleteCertificate(userId, certId);

        // then
        assertNotNull(res);
        assertEquals(certId, res.getId());
        assertEquals("s3://bucket/del.pdf", res.getFileUrl());
        assertEquals("del.pdf", res.getFilename());

        // 호출 순서 검증: delete(repo) -> delete(s3)
        InOrder inOrder = inOrder(certificateRepository, s3Uploader);
        inOrder.verify(certificateRepository).delete(cert);
        inOrder.verify(s3Uploader).delete("s3://bucket/del.pdf");
    }

    @Test
    @DisplayName("삭제 성공: S3 삭제 실패해도 예외 삼키고 성공 응답")
    void delete_s3DeleteFails_butSuccess() {
        User owner = User.builder().id(userId).build();
        Resume resume = Resume.builder().id(resumeId).user(owner).build();
        Certificate cert = Certificate.builder()
                .id(certId)
                .name("del.pdf")
                .fileUrl("s3://bucket/del.pdf")
                .resume(resume)
                .build();

        when(certificateRepository.findById(certId)).thenReturn(Optional.of(cert));
        doThrow(new RuntimeException("s3 down")).when(s3Uploader).delete("s3://bucket/del.pdf");

        CertificateResponse res = service.deleteCertificate(userId, certId);

        assertNotNull(res);
        verify(certificateRepository).delete(cert);
        verify(s3Uploader).delete("s3://bucket/del.pdf"); // 호출은 되었음
    }

    @Test
    @DisplayName("삭제 실패: 자격증 없음 → CertificateException")
    void delete_notFound() {
        when(certificateRepository.findById(certId)).thenReturn(Optional.empty());

        assertThrows(CertificateException.class, () -> service.deleteCertificate(userId, certId));
        verify(certificateRepository, never()).delete(any());
        verifyNoInteractions(s3Uploader);
    }

    @Test
    @DisplayName("삭제 실패: 소유자 아님 → CertificateException, delete 호출 안 됨")
    void delete_forbidden() {
        User other = User.builder().id(otherUserId).build();
        Resume resume = Resume.builder().id(resumeId).user(other).build();
        Certificate cert = Certificate.builder()
                .id(certId)
                .name("x")
                .fileUrl("s3://bucket/x")
                .resume(resume)
                .build();

        when(certificateRepository.findById(certId)).thenReturn(Optional.of(cert));

        assertThrows(CertificateException.class, () -> service.deleteCertificate(userId, certId));
        verify(certificateRepository, never()).delete(any());
        verifyNoInteractions(s3Uploader);
    }
}
