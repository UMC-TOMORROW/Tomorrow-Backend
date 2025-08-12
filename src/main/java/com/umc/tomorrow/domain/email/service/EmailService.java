/**
 *  EmailService 클래스 (이미지 메일만 발송, 불필요 기능 제거)
 *  작성자: 이승주
 *  작성일: 2025-07-27
 */
package com.umc.tomorrow.domain.email.service;

import com.umc.tomorrow.domain.email.dto.request.EmailRequestDTO;
import com.umc.tomorrow.domain.email.dto.response.EmailResponseDTO;
import com.umc.tomorrow.domain.email.enums.EmailContentProvider;
import com.umc.tomorrow.domain.email.enums.EmailContentType;
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
import com.umc.tomorrow.global.infrastructure.mail.ReceiptCardRenderer;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final ReceiptCardRenderer receiptCardRenderer;

    public EmailResponseDTO sendEmail(Long userId, EmailRequestDTO dto) {
        // 1) 사용자/공고 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RestApiException(GlobalErrorStatus._NOT_FOUND));
        Job job = jobRepository.findById(dto.getJobId())
                .orElseThrow(() -> new JobException(JobErrorStatus.JOB_NOT_FOUND));

        String to = user.getEmail();

        // 2) 제목(기존 프로바이더 로직 재사용)
        EmailContentProvider provider = EmailContentType.valueOf(dto.getType().name());
        String subject = provider.getSubject(user, job);

        // 3) 동적 값
        String jobTitle = job.getTitle();
        String companyName = job.getCompanyName() != null ? job.getCompanyName() : "";
        String submittedAt = ZonedDateTime.now(ZoneId.of("Asia/Seoul"))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        try {
            // 4) 베이스 JPEG 위에 텍스트 렌더링 (ReceiptCardRenderer는 JPEG 바이트 반환)
            var image = receiptCardRenderer.render(jobTitle, companyName, submittedAt);

            // 5) 인라인 이미지 메일 발송 (multipart=true)
            MimeMessage msg = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);

            // HTML 본문: 카드 이미지만 삽입
            String html = """
                <div style="background:#f5f5f5;padding:16px 0;text-align:center">
                  <img src="cid:receiptCard" alt="지원서 접수 완료"
                       style="display:block;margin:0 auto;width:100%;max-width:560px;border-radius:16px"/>
                </div>
                """;
            helper.setText(html, true);

            // ★ 인라인 이미지 첨부 (JPEG)
            helper.addInline("receiptCard", image, "image/jpeg");

            javaMailSender.send(msg);
        } catch (Exception e) {
            log.error("[메일 발송 실패] to={}, err={}", to, e.toString());
            throw new EmailException(EmailErrorStatus.EMAIL_SEND_FAILED);
        }

        return EmailResponseDTO.builder()
                .to(to)
                .build();
    }
}
