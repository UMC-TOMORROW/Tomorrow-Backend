//package com.umc.tomorrow.domain.email.service;
//
//import com.umc.tomorrow.domain.email.dto.request.EmailRequestDTO;
//import com.umc.tomorrow.domain.email.enums.EmailType;
//import com.umc.tomorrow.domain.job.entity.Job;
//import com.umc.tomorrow.domain.member.entity.User;
//import com.umc.tomorrow.domain.member.repository.UserRepository;
//import com.umc.tomorrow.domain.job.repository.JobRepository;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.mail.javamail.MimeMessageHelper;
//
//import jakarta.mail.MimeMessage;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@DisplayName("EmailService 단위 테스트")
//@ExtendWith(MockitoExtension.class)
//class EmailServiceTest {
//
//    @InjectMocks
//    private EmailService emailService;
//
//    @Mock
//    private JavaMailSender javaMailSender;
//
//    @Mock
//    private UserRepository userRepository;
//
//    @Mock
//    private JobRepository jobRepository;
//
//    @Mock
//    private MimeMessage mimeMessage;
//
//    @Mock
//    private MimeMessageHelper mimeMessageHelper;
//
//    private final Long userId = 1L;
//    private final Long jobId = 100L;
//
//    @Test
//    @DisplayName("지원 완료 메일 발송 성공")
//    void sendJobApplyEmail_success() throws Exception {
//        // given
//        User user = User.builder()
//                .id(userId)
//                .name("김지원")
//                .email("test@example.com")
//                .build();
//        Job job = Job.builder()
//                .id(jobId)
//                .title("백엔드 개발자")
//                .companyName("테크컴퍼니")
//                .build();
//
//        EmailRequestDTO requestDTO = EmailRequestDTO.builder()
//                .type(EmailType.JOB_APPLY)
//                .jobId(jobId)
//                .build();
//
//        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
//        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));
//        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
//
//        // when
//        var result = emailService.sendEmail(userId, requestDTO);
//
//        // then
//        assertNotNull(result);
//        assertEquals("test@example.com", result.getTo());
//        verify(javaMailSender).send(mimeMessage);
//    }
//
//    @Test
//    @DisplayName("합격 메일 발송 성공")
//    void sendJobAcceptedEmail_success() throws Exception {
//        // given
//        User user = User.builder()
//                .id(userId)
//                .name("김지원")
//                .email("test@example.com")
//                .build();
//        Job job = Job.builder()
//                .id(jobId)
//                .title("백엔드 개발자")
//                .companyName("테크컴퍼니")
//                .build();
//
//        EmailRequestDTO requestDTO = EmailRequestDTO.builder()
//                .type(EmailType.JOB_ACCEPTED)
//                .jobId(jobId)
//                .build();
//
//        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
//        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));
//        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
//
//        // when
//        var result = emailService.sendEmail(userId, requestDTO);
//
//        // then
//        assertNotNull(result);
//        assertEquals("test@example.com", result.getTo());
//        verify(javaMailSender).send(mimeMessage);
//    }
//
//    @Test
//    @DisplayName("불합격 메일 발송 성공")
//    void sendJobRejectedEmail_success() throws Exception {
//        // given
//        User user = User.builder()
//                .id(userId)
//                .name("김지원")
//                .email("test@example.com")
//                .build();
//        Job job = Job.builder()
//                .id(jobId)
//                .title("백엔드 개발자")
//                .companyName("테크컴퍼니")
//                .build();
//
//        EmailRequestDTO requestDTO = EmailRequestDTO.builder()
//                .type(EmailType.JOB_REJECTED)
//                .jobId(jobId)
//                .build();
//
//        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
//        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));
//        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
//
//        // when
//        var result = emailService.sendEmail(userId, requestDTO);
//
//        // then
//        assertNotNull(result);
//        assertEquals("test@example.com", result.getTo());
//        verify(javaMailSender).send(mimeMessage);
//    }
//
//    @Test
//    @DisplayName("사용자가 존재하지 않으면 예외 발생")
//    void sendEmail_userNotFound_throwsException() {
//        // given
//        EmailRequestDTO requestDTO = EmailRequestDTO.builder()
//                .type(EmailType.JOB_APPLY)
//                .jobId(jobId)
//                .build();
//
//        when(userRepository.findById(userId)).thenReturn(Optional.empty());
//
//        // when & then
//        assertThrows(Exception.class, () -> emailService.sendEmail(userId, requestDTO));
//        verifyNoInteractions(javaMailSender);
//    }
//
//    @Test
//    @DisplayName("직무가 존재하지 않으면 예외 발생")
//    void sendEmail_jobNotFound_throwsException() {
//        // given
//        User user = User.builder()
//                .id(userId)
//                .name("김지원")
//                .email("test@example.com")
//                .build();
//
//        EmailRequestDTO requestDTO = EmailRequestDTO.builder()
//                .type(EmailType.JOB_APPLY)
//                .jobId(jobId)
//                .build();
//
//        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
//        when(jobRepository.findById(jobId)).thenReturn(Optional.empty());
//
//        // when & then
//        assertThrows(Exception.class, () -> emailService.sendEmail(userId, requestDTO));
//        verifyNoInteractions(javaMailSender);
//    }
//}
