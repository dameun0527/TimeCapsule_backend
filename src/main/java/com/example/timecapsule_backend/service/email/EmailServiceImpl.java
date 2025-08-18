package com.example.timecapsule_backend.service.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Override
    @Async
    public void sendTextEmail(String to, String subject, String content) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("noreply@timecapsule.com");
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);
            
            mailSender.send(message);
            log.info("텍스트 이메일 발송 완료: {}", to);
        } catch (Exception e) {
            log.error("텍스트 이메일 발송 실패: {}", to, e);
            throw new RuntimeException("이메일 발송에 실패했습니다.", e);
        }
    }

    @Override
    @Async
    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("noreply@timecapsule.com");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("HTML 이메일 발송 완료: {}", to);
        } catch (MessagingException e) {
            log.error("HTML 이메일 발송 실패: {}", to, e);
            throw new RuntimeException("이메일 발송에 실패했습니다.", e);
        }
    }

    @Override
    @Async
    public void sendTimeCapsuleEmail(String to, String capsuleTitle, String content, com.example.timecapsule_backend.domain.capsule.ThemeType themeType) {
        String subject = "[TimeCapsule] " + capsuleTitle + " - 타임캡슐이 도착했습니다!";
        
        Context context = new Context();
        context.setVariable("capsuleTitle", capsuleTitle);
        context.setVariable("content", content);
        context.setVariable("recipientEmail", to);
        
        String templateName = getTemplateNameByTheme(themeType);
        String processedHtml = templateEngine.process(templateName, context);
        
        sendHtmlEmail(to, subject, processedHtml);
        log.info("타임캡슐 이메일 발송 완료: {} - {} (테마: {})", to, capsuleTitle, themeType);
    }
    
    @Override
    public void sendEmail(com.example.timecapsule_backend.controller.email.dto.EmailRequest emailRequest) {
        switch (emailRequest.getEmailType()) {
            case TEXT:
                sendTextEmail(
                        emailRequest.getTo(),
                        emailRequest.getSubject(),
                        emailRequest.getContent()
                );
                break;
            case HTML:
                sendHtmlEmail(
                        emailRequest.getTo(),
                        emailRequest.getSubject(),
                        emailRequest.getContent()
                );
                break;
            case TIMECAPSULE:
                sendTimeCapsuleEmail(
                        emailRequest.getTo(),
                        emailRequest.getSubject(),
                        emailRequest.getContent(),
                        emailRequest.getThemeType()
                );
                break;
        }
    }
    
    private String getTemplateNameByTheme(com.example.timecapsule_backend.domain.capsule.ThemeType themeType) {
        if (themeType == null) {
            return "email/timecapsule-basic";
        }
        
        return switch (themeType) {
            case CHRISTMAS_TREE -> "email/christmas-tree";
            case BIRTHDAY_CAKE -> "email/birthday-cake";
            case EXAM_CALENDAR -> "email/exam-calendar";
            case ANNIVERSARY_PHOTO_FRAME -> "email/anniversary-photo-frame";
            case TIME_CAPSULE_VAULT -> "email/time-capsule-vault";
            default -> "email/timecapsule-basic";
        };
    }
}
