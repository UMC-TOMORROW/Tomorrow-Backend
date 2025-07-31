/**
 *  EmailService 클래스
 *  이메일을 전송하는 서비스 로직
 *  작성자: 이승주
 *  작성일: 2025-07-27
 */
package com.umc.tomorrow.domain.email.service;

import com.umc.tomorrow.domain.email.dto.request.EmailRequestDTO;
import com.umc.tomorrow.domain.email.enums.EmailContentProvider;
import com.umc.tomorrow.domain.email.enums.EmailContentType;
import com.umc.tomorrow.domain.email.enums.EmailType;
import com.umc.tomorrow.domain.email.dto.response.EmailResponseDTO;
import com.umc.tomorrow.domain.email.exception.EmailException;
import com.umc.tomorrow.domain.email.exception.code.EmailErrorStatus;
import com.umc.tomorrow.domain.job.entity.Job;
import com.umc.tomorrow.domain.job.exception.JobException;
import com.umc.tomorrow.domain.job.exception.code.JobErrorStatus;
import com.umc.tomorrow.domain.job.repository.JobRepository;
import com.umc.tomorrow.domain.member.entity.User;
import com.umc.tomorrow.domain.member.repository.UserRepository;
import com.umc.tomorrow.global.common.exception.RestApiException;
import com.umc.tomorrow.global.common.exception.code.GlobalErrorStatus;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;

    /*
    이메일 전송 서비스 메서드
     */
    public EmailResponseDTO sendEmail(Long userId,EmailRequestDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RestApiException(GlobalErrorStatus._NOT_FOUND));

        String email = user.getEmail();

        Job job = jobRepository.findById(dto.getJobId())
                .orElseThrow(() -> new JobException(JobErrorStatus.JOB_NOT_FOUND));

        EmailContentProvider contentProvider = EmailContentType.valueOf(dto.getType().name());

        String subject = contentProvider.getSubject(user, job);
        String content = contentProvider.getContent(user, job);

        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "UTF-8");

            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(content, false);

            javaMailSender.send(mimeMessage);

        } catch (MessagingException e) {
            log.error("[메일 전송 실패] to={}, error={}", email, e.getMessage());
            throw new EmailException(EmailErrorStatus.EMAIL_SEND_FAILED);
        }

        return EmailResponseDTO.builder()
                .to(email)
                .build();
    }
}
