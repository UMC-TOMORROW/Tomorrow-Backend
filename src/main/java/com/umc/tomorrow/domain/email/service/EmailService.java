/**
 *  EmailService 클래스
 *  이메일을 전송하는 서비스 로직
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
import com.umc.tomorrow.domain.application.entity.Application;
import com.umc.tomorrow.domain.application.repository.ApplicationRepository;
import com.umc.tomorrow.domain.application.enums.ApplicationStatus;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import com.umc.tomorrow.domain.email.enums.EmailType;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final ReceiptCardRenderer receiptCardRenderer;
    private final ApplicationRepository applicationRepository;

    @Async("emailExecutor")
    public void sendEmail(Long userId, EmailRequestDTO dto) {
        // 1) 사용자/공고 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RestApiException(GlobalErrorStatus._NOT_FOUND));
        Job job = jobRepository.findById(dto.getJobId())
                .orElseThrow(() -> new JobException(JobErrorStatus.JOB_NOT_FOUND));

        // 1-1) 지원 상태 검증
        Application application = applicationRepository.findByJobIdAndUserId(dto.getJobId(), userId)
                .orElseThrow(() -> new RestApiException(GlobalErrorStatus._NOT_FOUND));

        // 이메일 타입에 따른 지원 상태 검증
        validateEmailTypeWithApplicationStatus(dto.getType(), application.getStatus());

        String to = user.getEmail();

        // 2) 제목(기존 프로바이더 로직 재사용)
        EmailContentProvider provider = EmailContentType.valueOf(dto.getType().name());
        // 3) 동적 값
        String jobTitle = job.getTitle() != null ? job.getTitle().trim() : "공고명 없음";
        String companyName = job.getCompanyName() != null ? job.getCompanyName().trim() : "회사명 없음";
        String submittedAt = ZonedDateTime.now(ZoneId.of("Asia/Seoul"))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        // 디버깅용 로그
        log.info("[이메일 발송] jobTitle: '{}', companyName: '{}'", jobTitle, companyName);

        try {
            // 4) 접수 카드 이미지 생성
            var image = receiptCardRenderer.render(jobTitle, companyName, submittedAt);

            // 5) 인라인 이미지 메일 발송 (multipart=true)
            MimeMessage msg = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");

            helper.setTo(to);

            // 제목과 내용을 안전하게 가져오기
            String subject;
            String textContent;
            try {
                subject = provider.getSubject(user, job);
                textContent = provider.getContent(user, job);
            } catch (Exception e) {
                log.warn("이메일 제목/내용 생성 실패, 기본값 사용: {}", e.getMessage());
                subject = "[내일] 지원서 접수 완료";
                textContent = "정상적으로 지원이 완료되었습니다.";
            }

            helper.setSubject(subject);

            // HTML 본문: 카드 이미지와 이메일 내용 함께 표시
            String htmlTemplate = """
                <div style="background:#f5f5f5;padding:16px 0;text-align:center">
                  <img src="cid:receiptCard" alt="지원서 접수 완료"
                       style="display:block;margin:0 auto;width:100%;max-width:560px;border-radius:16px"/>
                </div>
                <div style="padding:20px;text-align:center;font-family:Arial,sans-serif;">
                  <p style="font-size:16px;color:#333;margin:10px 0;">
                    {{CONTENT}}
                  </p>
                </div>
                """;

            // % 문자를 이스케이프 처리하고 템플릿 치환
            String safeContent = textContent.replace("%", "%%");
            String html = htmlTemplate.replace("{{CONTENT}}", safeContent);

            // 디버깅용 로그
            log.info("[이메일 발송] textContent: '{}', safeContent: '{}'", textContent, safeContent);

            helper.setText(html, true);

            // ★ 인라인 이미지 첨부 (PNG)
            helper.addInline("receiptCard", image, "image/png");

            javaMailSender.send(msg);
        } catch (Exception e) {
            log.error("[메일 발송 실패] to={}, err={}", to, e.toString());
            throw new EmailException(EmailErrorStatus.EMAIL_SEND_FAILED);
        }
    }

    /**
     * 이메일 타입과 지원 상태 검증
     */
    private void validateEmailTypeWithApplicationStatus(EmailType emailType, ApplicationStatus currentStatus) {
        switch (emailType) {
            case JOB_APPLY:
                // 지원 완료는 PENDING 상태에서만 가능
                if (currentStatus != ApplicationStatus.PENDING) {
                    if (currentStatus == ApplicationStatus.ACCEPTED) {
                        throw new EmailException(EmailErrorStatus.ALREADY_ACCEPTED);
                    } else if (currentStatus == ApplicationStatus.REJECTED) {
                        throw new EmailException(EmailErrorStatus.ALREADY_REJECTED);
                    } else {
                        throw new EmailException(EmailErrorStatus.APPLICATION_NOT_PENDING);
                    }
                }
                break;
            case JOB_ACCEPTED:
                // 합격은 PENDING 상태에서만 가능
                if (currentStatus != ApplicationStatus.PENDING) {
                    if (currentStatus == ApplicationStatus.ACCEPTED) {
                        throw new EmailException(EmailErrorStatus.ALREADY_ACCEPTED);
                    } else if (currentStatus == ApplicationStatus.REJECTED) {
                        throw new EmailException(EmailErrorStatus.ALREADY_REJECTED);
                    } else {
                        throw new EmailException(EmailErrorStatus.APPLICATION_NOT_PENDING);
                    }
                }
                break;
            case JOB_REJECTED:
                // 불합격은 PENDING 상태에서만 가능
                if (currentStatus != ApplicationStatus.PENDING) {
                    if (currentStatus == ApplicationStatus.ACCEPTED) {
                        throw new EmailException(EmailErrorStatus.ALREADY_ACCEPTED);
                    } else if (currentStatus == ApplicationStatus.REJECTED) {
                        throw new EmailException(EmailErrorStatus.ALREADY_REJECTED);
                    } else {
                        throw new EmailException(EmailErrorStatus.APPLICATION_NOT_PENDING);
                    }
                }
                break;
        }
    }
}
